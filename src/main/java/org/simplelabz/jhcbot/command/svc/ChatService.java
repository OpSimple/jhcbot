package org.simplelabz.jhcbot.command.svc;

import com.eclipsesource.json.Json;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.simplelabz.jhcbot.BotsManager.BotNotCreatedException;
import org.simplelabz.jhcbot.Chat;
import org.simplelabz.jhcbot.Chat.ParsedData;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.Utils;

/**
 *
 * @author simple
 */
public class ChatService
{
    private ServiceAdapter svc;
    private ParsedData cmdData;
    private Chat parent;
    private String banner;
    private Chat chat;
    String id;
    
    private boolean userOn;
    private long lastupdated;
    private boolean checkUser;
    
    private ChatService(String chan, String nick, String pass, Chat parent, ParsedData cmdData, ServiceAdapter svc)
    {
        this.svc = svc;
        this.banner = null;
        this.parent = parent;
        this.cmdData = cmdData;
        this.userOn = false;
        this.checkUser = true;
        this.lastupdated = System.currentTimeMillis();
        this.id = Utils.generateRandomString(4);
        
        chat = new Chat(Conf.URI, chan, nick, pass);
    }
    
    public static ChatService createChatService(String channel, String nick, String pass, Chat parent, ParsedData cmdData, ServiceAdapter svc)throws BotNotCreatedException
    {
        if(Thread.activeCount()>=Conf.MAX_THREADS_LIMIT)throw new BotNotCreatedException("Maximum allowed threads limit exceeded!");
        else return new ChatService(channel, nick, pass, parent, cmdData, svc);
    }
    
    public ChatService setWaitforUser(boolean check)
    {
        this.checkUser = check;
        return this;
    }
    
    public ChatService setBanner(String banner)
    {
        this.banner = banner;
        return this;
    }
    public void execute()
    {
        if(!chat.isConnected())
        {
            chat.setParseListener(new Chat.ParseListener()
            {
                @Override
                public void afterParsing(ParsedData dat)
                {
                    process(dat);
                }
            });
            chat.connect();
            startIdleTimer();
            Utils.showInfo("Service "+id+" Started!");
        }
    }
    
    private void end()
    {
        if(chat.isConnected())
        {
            chat.disconnect();
        }
        System.gc();
    }
    
    private void startIdleTimer()
    {
        TimerTask idletask = new TimerTask()
        {
            @Override
            public void run()
            {
                long idle  = System.currentTimeMillis()-lastupdated;
                if(idle >TimeUnit.SECONDS.toMillis(60))
                {
                    chat.send("Bot Idle for "+TimeUnit.MILLISECONDS.toSeconds(idle)+" seconds ! Stopping!");
                    end();
                    this.cancel();
                }
            }
        };
        Utils.timer.scheduleAtFixedRate(idletask, 0, TimeUnit.SECONDS.toMillis(60));
    }
    
    private void process(ParsedData dat)
    {
        if(checkUser && dat.getCallType()==ParsedData.ONLINE_SET
                && dat.getExtDataAsJson().asObject().get("nicks").asArray().values().contains(Json.value(cmdData.getNick())))
        {
            userOn = true;
        }
        if(checkUser && dat.getCallType()==ParsedData.ONLINE_ADD && dat.getNick().equals(cmdData.getNick()))
        {
            userOn = true;
        }
        if(checkUser && dat.getCallType()==ParsedData.ONLINE_REMOVE && dat.getNick().equals(cmdData.getNick()))
        {
            end();
            return;
        }
        
        //mills status for our idle timer
        lastupdated = System.currentTimeMillis();
        
        if(checkUser && !userOn)return;
        if(banner!=null)
        {
            chat.send(banner);
            banner = null;
        }
        
        boolean served = svc.onServe(parent, chat, cmdData, dat);
        
        if(served)end();
    }
}
