package org.simplelabz.jhcbot.command;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;
import org.simplelabz.jhcbot.Chat.ParsedData;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.Utils;
import org.simplelabz.jhcbot.command.event.CommandEvent;
import org.simplelabz.jhcbot.store.DataStore;

/**
 *
 * @author simple
 */
public class MSG implements Command
{
    private int[] CALLS = {ParsedData.CHAT, ParsedData.ONLINE_ADD};
            
    @Override
    public String name()
    {
        return "Message";
    }

    @Override
    public String call()
    {
        return "msg";
    }

    @Override
    public String desc()
    {
        return "Message someone. Usage: "+Conf.TRIG+call()+" <nickname> <message>";
    }

    @Override
    public boolean isHidden()
    {
        return false;
    }

    @Override
    public void action(CommandEvent e)
    {
        String[] args = e.getArgs();
        if(args.length<2)
        {
            e.send("Incorrect Usage!\r"+desc());
            return;
        }
        String nickname = args[0].startsWith("@")?args[0].substring(1):args[0];
        if(Utils.verifyNick(nickname))
        {
            String message = e.getArgsStr().substring(e.getArgsStr().indexOf(nickname)+nickname.length()+1);
            e.reply("your message for @"+nickname+" queued!");
            appendMSG(e.getCallAsStore(),nickname, e.getNick(),message,e.getTime());
            e.logInfo("Message for @"+nickname+" queued!");
        }
        else
        {
            e.reply("Invalid Nickname! Nickname can only have a-z, A-Z and _ and it can only be "+Conf.MAX_NICK_LENGTH+" chars long!");
        }
    }

    @Override
    public boolean listens()
    {
        return true;
    }
    @Override
    public void listen(CommandEvent e)
    {
        //Checks
        if(!ArrayUtils.contains(CALLS, e.getData().getCallType()))return;
        // Init
        DataStore store = e.getCallAsStore();
        if(!store.containsKey(e.getNick()))return;
        SimpleDateFormat date = new SimpleDateFormat("EEE dd MMM HH:mm:ss zzz");
        date.setTimeZone(TimeZone.getTimeZone("UTC"));
        String send = "Welcome Back @"+e.getNick()+" ("+date.format(new Date(e.getTime()))+")"
                + "\rMessages for you:\r\r";
        for(DataStore.DataEntry msg:store.get(e.getNick()))
        {
            send = send+msg.getValue()+"\r";
            store.delete(msg.getID());
        }
        e.send(send);
        Logger.getLogger("Bot").log(Level.INFO, "msgs for {0} cleared", e.getNick());
    }

    protected static void appendMSG(DataStore store, String nick, String from, String msg, long time)
    {
        SimpleDateFormat date = new SimpleDateFormat("EEE dd MMM HH:mm:ss");
        date.setTimeZone(TimeZone.getTimeZone("UTC"));
        String msgfmt = "["+date.format(new Date(time))+"] "+from+": "+msg;
        store.store(nick, msgfmt);
    }
}
