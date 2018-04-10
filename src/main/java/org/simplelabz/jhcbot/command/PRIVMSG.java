package org.simplelabz.jhcbot.command;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.simplelabz.jhcbot.BotsManager;
import org.simplelabz.jhcbot.Chat;
import org.simplelabz.jhcbot.Chat.ParsedData;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.Utils;
import org.simplelabz.jhcbot.command.event.CommandEvent;
import org.simplelabz.jhcbot.command.svc.ChatService;
import org.simplelabz.jhcbot.command.svc.ServiceAdapter;
import org.simplelabz.jhcbot.store.DataStore;


/**
 *
 * @author simple
 */
public class PRIVMSG implements Command,ServiceAdapter
{
    private static final String BANNER = ""
            + "***************************\r"
            + "Greetings! Now enter your private message as follows:\r"
            + "   #  First enter the receipt's nickname and press send.\r"
            + "   #  Then, enter your message contents and send.\r"
            + "\r"
            + "A 60s of inactivity will lead to cancellation!"
            + "SEND   -CANCEL   TO CANCEL THIS MESSAGE AT ANY MOMENT!\r"
            + "Always use http://pastebin.com/ for long pastes, for the betterment of this useful bot :)\r"
            + "***************************\r";
    
    private int[] CALLS = {ParsedData.CHAT, ParsedData.ONLINE_ADD, ParsedData.ONLINE_SET};
    
    private String nick = null;
    private String msg = null;
    
    @Override
    public String name()
    {
        return "Private Message";
    }

    @Override
    public String call()
    {
        return "privmsg";
    }

    @Override
    public String desc()
    {
        return "Send private messages without exposing to the whole group!";
    }

    @Override
    public boolean isHidden()
    {
        return false;
    }

    @Override
    public boolean listens()
    {
        return true;
    }

    @Override
    public void action(CommandEvent ev)
    {
        ev.getChat().invite(ev.getNick());
    }

    @Override
    public void listen(CommandEvent ev)
    {
        try
        {
            DataStore store = DataStore.getDataStore("pmsg", ev.getChat().getChannel());
            if(store.containsKey(ev.getNick()))
            {
                if(ev.getData().getCallType()==ParsedData.INVITE)
                {
                    String chan = ev.getData().getExtDataAsJson().getString("channel", "");
                    ChatService cs = ChatService.createChatService(chan, Conf.NAME, Conf.PASSWORD, ev.getChat(), ev.getData(), this);
                    cs.execute();
                }
                if(ev.getData().getCallType()==ParsedData.CHAT)
                {
                    if(ev.getText().contains("SHOW"))
                    {
                        ev.reply("Now, join within 60s to read the message!");
                        ev.getChat().invite(ev.getNick());
                    }
                    else
                    {
                        ev.reply("I've some private messages for you! Do you wanna see them?\rif yes, reply me with SHOW!");
                    }
                }
            }
            else if(ev.getData().getCallType()==ParsedData.INVITE)
            {
                String chan = ev.getData().getExtDataAsJson().getString("channel", "");
                ChatService cs = ChatService.createChatService(chan, Conf.NAME, Conf.PASSWORD, ev.getChat(), ev.getData(), this);
                cs.setBanner(BANNER).execute();
            }
        } catch (BotsManager.BotNotCreatedException ex) {
            ev.sendError("BotNotCreatedException: "+ex.getMessage());
            Logger.getLogger(PRIVMSG.class.getName()).log(Level.SEVERE, null, ex);
        }      
    }
    
    protected static void appendPrivMSG(DataStore store, String nick, String from, String msg, long time)
    {
        SimpleDateFormat date = new SimpleDateFormat("EEE dd MMM HH:mm:ss");
        date.setTimeZone(TimeZone.getTimeZone("UTC"));
        String msgfmt = "["+date.format(new Date(time))+"] "+from+": "+msg;
        store.store(nick, msgfmt);
    }
    
    @Override
    public boolean onServe(Chat parent, Chat chat, Chat.ParsedData cmdData, Chat.ParsedData data)
    {
        if(!ArrayUtils.contains(CALLS, data.getCallType()))return false;
        if(data.getCallType()==ParsedData.CHAT && (!data.getNick().equals(cmdData.getNick())))return false;
        if(StringUtils.contains(data.getText(), "-CANCEL"))return true;
        
        DataStore store = DataStore.getDataStore("pmsg", parent.getChannel());
        if(store.containsKey(cmdData.getNick()))
        {
            String send = "Welcome @"+cmdData.getNick()+"\rMessages for you:\r\r";
            for(DataStore.DataEntry m:store.get(cmdData.getNick()))
            {
                send = send+m.getValue()+"\r";
                store.delete(m.getID());
            }
            send=send+"\r\r\r"+BANNER;
            chat.send(send);
            return false;
        }
        else if(nick==null)
        {
            if(!Utils.verifyNick(data.getText().trim()))
            {
                chat.send("Invalid Nickname! Nickname can only have a-z, A-Z and _ and it can only be "+Conf.MAX_NICK_LENGTH+" chars long!");
                return false;
            }
            nick = data.getText().trim();
            chat.send("Nickname: "+nick+"\rNow, Enter your Message content!");
            return false;
        }
        else if(msg==null)
        {
            msg = data.getText();
            appendPrivMSG(store, nick, cmdData.getNick(), msg, data.getTime());
            chat.send("Message saved!");
            return true;
        }
        return false;
    }
}
