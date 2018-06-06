package org.simplelabz.jhcbot;

import bsh.EvalError;
import bsh.Interpreter;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simplelabz.jhcbot.Chat.ParseListener;
import org.simplelabz.jhcbot.Chat.ParsedData;
import org.simplelabz.jhcbot.command.Command;
import org.simplelabz.jhcbot.command.event.CommandEvent;
import org.simplelabz.jhcbot.store.DataStore;

/**
 * Manages the bot
 * @author simple
 */
public class Bot
{
    //Accessible Commands here
    public static String[] COMMANDS = {
        //General bot commands
        "h", "help", "info", "about", "afk", "amsg", "msg", "bsh", "lschan", "lsadmins", "invite", "lsbots", "time",
        "wttr", "ipinfo", "ping", "uptime", "echo", "g", "google", "jokes", "ytb", "hcstats", "privmsg","talk",
        "urban","math", "js", "glite", "sources","wiki",
        //Admin bot commands
        "killbot","mkadmin", "rmadmin", "ban", "unban", "enable" , "disable", "leave", "stop","calm","health"
    };
    
    private Conf config;
    private Chat chat;
    
    private boolean enabled;
    private boolean isRunning;
    private long lastupdated;
    private long starttime;
    
    /**
     * Create a new bot
     * @param conf a config for the bot
     */
    public Bot(Conf conf)
    {
        this.config = conf;
        this.enabled = true;
        this.isRunning = false;
        this.starttime = System.currentTimeMillis();
        this.lastupdated = System.currentTimeMillis();
        chat = new Chat(Conf.URI, conf.getChannel(), Conf.NAME, Conf.PASSWORD);
        logInfo("Bot has initialized!");
    }
    
    /**
     * Returns if the bot is enabled
     * @return enabled
     */
    public boolean isEnabled()
    {
        return this.enabled;
    }
    /**
     * Returns if the bot is running
     * @return running
     */
    public boolean isRunning()
    {
        return this.isRunning;
    }
    
    /**
     * Get the bot config {@link org.simplelabz.jhcbot.Conf}
     * @return config
     */
    public Conf getConfig()
    {
        return this.config;
    }
    
    /**
     * Get the {@link org.simplelabz.jhcbot.Chat} for this bot
     * @return 
     */
    public Chat getChat()
    {
        return this.chat;
    }
    
    /**
     * Returns the channel
     * @return channel
     */
    public String getChannel()
    {
        return config.getChannel();
    }
    
    /**
     * Returns the milliseconds when some message was received from the server
     * @return the milliseconds
     */
    public long getLastUpdated()
    {
        return this.lastupdated;
    }
    
    /**
     * Returns the milliseconds when the bot was created
     * @return 
     */
    public long getStartTime()
    {
        return this.starttime;
    }
    
    /**
     * Returns the running time for this bot in milliseconds
     * @return 
     */
    public long getUptime()
    {
        return (System.currentTimeMillis()-starttime);
    }
    
    /**
     * Enable or disable the bot for the general users
     * @param enabled 
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        logInfo("Enabled = "+enabled);
    }
    
    /**
     * Start the bot
     */
    public void start()
    {
        if(!chat.isConnected())
        {
            chat.setParseListener(new ParseListener()
            {
                @Override
                public void afterParsing(ParsedData dat)
                {
                    process(dat);
                }
            });
            chat.connect();
            if(config.getAgentNick()!=null)startIdleTimer(this);
            logInfo("Bot Started!");
            this.isRunning = true;
        }
    }
    
    /**
     * Disable the bot for some give time
     * @param time time in seconds
     */
    public void disable(int time)
    {
        if(enabled)
        {
            enabled = false;
            TimerTask disabletask = new TimerTask()
            {
                @Override
                public void run()
                {
                    enabled = true;
                }
            };
            Utils.timer.schedule(disabletask, TimeUnit.SECONDS.toMillis(time));
        }
    }
    
    /*
    Checks and stops the bot if it is idle
    */
    private void startIdleTimer(Bot bot)
    {
        TimerTask idletask = new TimerTask()
        {
            @Override
            public void run()
            {
                long idle  = System.currentTimeMillis()-lastupdated;
                if(idle > TimeUnit.MINUTES.toMillis(5))
                {
                    chat.send("Bot Idle for "+TimeUnit.MILLISECONDS.toSeconds(idle)+" seconds ! Leaving!");
                    logInfo("Bot futile and Idle! Stopping!");
                    BotsManager.remove(bot);
                    this.cancel();
                }
            }
        };
        Utils.timer.scheduleAtFixedRate(idletask, 0, TimeUnit.SECONDS.toMillis(60));
    }
    
    /**
     * Stop the bot
     */
    public void stop()
    {
        if(chat.isConnected())
        {
            chat.disconnect();
            this.isRunning = false;
            logInfo("Bot stopped!");
        }
    }
    
    /*
    Process messages
    */
    private void process(ParsedData dat)
    {
        //First don't process for your own texts
        if(dat.getNick()!=null && dat.getNick().equals(Conf.NAME))return;
        
        // Needed Inits
        CommandEvent event = new CommandEvent(config, chat, this, dat);
        
        /* Checks */
        if(!isRunning)return;//If bot stopped don't process even if invoked
        // Check if bot enabled for general users
        //Set last updated
        lastupdated = System.currentTimeMillis();
        if((!this.enabled) && !(event.isAdmin()||event.isAgent()||dat.isMod()||dat.isAdmin()))return;
        
        //Check if nick is a banned
        {
            List<String> bans = Arrays.asList(DataStore.getDataStore("ban", config.getChannel()).getKeys());
            if((bans.contains(dat.getNick()) || bans.contains(dat.getTrip())) && 
                    !Arrays.asList(Conf.TRIPS).contains(dat.getTrip()))
            {
                event.replyPermissionDenied();
                return;
            }
        }
        
        //Show Banner if online
        if(dat.getCallType()==ParsedData.ONLINE_SET)
            event.send(Utils.parseBanner(config.getBanner()));
        //Check for other errors
        if(dat.getCallType()==ParsedData.WARN && (dat.getText().contains("Nickname")||dat.getText().contains("channels too fast")))
        {
            logErr(dat.getText());
            BotsManager.remove(this);
        }
        //Check if banned!
        if(dat.getCallType()==ParsedData.BANNED && dat.getNick().equals(Conf.NAME))
        {
            logErr(dat.getText());
            System.exit(2);//Exit with error
        }
        
        //Check for a called command by user
        Command command= null;
        if(dat.getText().startsWith(Conf.TRIG))
        {
            for(String cmd : COMMANDS)
            {
                Command com = callCommand(cmd);
                if(com!=null && hasCommand(dat.getText(), com.call()))
                {
                    logInfo("@"+dat.getNick()+" => "+dat.getText());
                    command = com;
                }
            }
            event.setTriggeredCommand(command);
        }
        
        // Begin commands listenings
        startListening(event);
        
        //Run the detected command in a new thread!
        if(command!=null && !command.isHidden() && isRunning)
        {
            event.setCommand(command);
            Thread run = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    event.getCommand().action(event);
                }
            }, event.getCommand().call());
            run.start();
        }
        System.gc();
    }
    
    /*
    Update all listeners
    */
    private void startListening(CommandEvent event)
    {
        Thread listen = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    CommandEvent ev = (CommandEvent)event.clone();
                    Command command;
                    for(String cmd : COMMANDS)
                    {
                        if(!isRunning)return;
                        command = callCommand(cmd);
                        if(command!=null && command.listens())
                        {
                            ev.setCommand(command);
                            command.listen(ev);
                        }
                    }
                }
                catch (CloneNotSupportedException ex)
                {
                    Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        },"listening");
        listen.start();
    }
    
    /*
    Checks if this text contains a bot command
    */
    private static boolean hasCommand(String text, String cmd)
    {
        return (text.startsWith(Conf.TRIG) && text.trim().split(" ")[0].equals(Conf.TRIG+cmd));
    }
    
    /**
     * Call any {@link org.simplelabz.jhcbot.command.Command} by its lowercase name
     * @param cmd lowercase name
     * @return Command
     */
    public static Command callCommand(String cmd)
    {
        Command command = null;
        try
        {
            command = (Command) ClassLoader.getSystemClassLoader()
                    .loadClass("org.simplelabz.jhcbot.command."+cmd.toUpperCase())
                    .newInstance();
        }
        catch(ClassNotFoundException ex)
        {
            Interpreter i = Utils.newBshInterpreter();
            try
            {
                i.eval("org.simplelabz.jhcbot.command.Command cmd = "+cmd.toUpperCase()+"();");
                command = (Command) i.get("cmd");
            }
            catch (EvalError evExp)
            {
                Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, "Error: "+cmd, evExp);
            }
        }
        catch( IllegalAccessException | InstantiationException ex)
        {
            Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, "Error: "+cmd, ex);
        }
        return command;
    }
    
    /**
     * An error logger for this bot
     * @param msg error messages
     */
    public void logErr(String msg)
    {
        Utils.logBotErr(chat.getChannel(), msg);
    }
    
    /**
     * An info logger for this bot
     * @param msg 
     */
    public void logInfo(String msg)
    {
        Utils.logBotInfo(chat.getChannel(), msg);
    }
}
