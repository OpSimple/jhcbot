package org.simplelabz.jhcbot.command.event;

import java.util.Arrays;
import java.util.Date;
import java.util.StringTokenizer;
import org.simplelabz.jhcbot.Bot;
import org.simplelabz.jhcbot.Chat;
import org.simplelabz.jhcbot.Chat.ParsedData;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.Utils;
import org.simplelabz.jhcbot.command.Command;
import org.simplelabz.jhcbot.store.DataStore;


/**
 * 
 * @author simple
 */
public class CommandEvent implements Cloneable
{
    private Conf config;
    private Chat chat;
    private ParsedData data;
    private Command command ;
    private Bot bot;
    
    public CommandEvent(Conf config, Chat chat, Bot bot, ParsedData data)
    {
        this.config = config;
        this.chat = chat;
        this.data = data;
        this.bot = bot;
        this.command = null;
    }
    
    public void setCommand(Command com)
    {
        this.command = com;
    }
    public Command getCommand()
    {
        return command;
    }
    
    /**
     * Returns if the current nick is bot's admin
     * @return 
     */
    public boolean isBotAdmin()
    {
        return Conf.ADMIN.equals(data.getTrip());
    }
    
    /**
     * Returns if the current nick is allowed admin
     * @return 
     */
    public boolean isAdmin()
    {
        return Arrays.asList(Conf.TRIPS).contains(data.getTrip());
    }
    
    /**
     * Returns if the current nick is the invite agent for this bot
     * @return 
     */
    public boolean isAgent()
    {
        return (config.getAgentTrip()!=null && config.getAgentTrip().equals(data.getTrip()))
                || (config.getAgentNick()!=null && config.getAgentNick().equals(data.getNick()));
    }
    
    /**
     * Get text from current chat
     * @return 
     */
    public String getText()
    {
        return data.getText();
    }
    
    /**
     * Get time in milliseconds as sent by the server
     * @return 
     */
    public long getTime()
    {
        return data.getTime();
    }
    
    /**
     * Get current nick
     * @return 
     */
    public String getNick()
    {
        return data.getNick();
    }
    
    /**
     * Get current nick's trip
     * @return 
     */
    public String getTrip()
    {
        return data.getTrip();
    }
    
    /**
     * Get time as {@link java.util.Date} as sent by the server
     * @return 
     */
    public Date getDate()
    {
        return new Date(data.getTime());
    }
    
    /**
     * Returns the arguments after the command as a single string
     * @return arguments
     */
    public String getArgsStr()
    {
        String args;
        if(command!=null && data.getText().startsWith(Conf.TRIG+command.call()))
        {
            if(data.getText().length()>(this.command.call().length()+1))
                args = this.data.getText().substring(command.call().length()+2);
            else
                args = this.data.getText().substring(command.call().length()+1);
        }
        else
            args = data.getText();
        return args;
    }
    
    /**
     * Returns the arguments after the command as an array of chars (all white spaces are trimmed)
     * @return 
     */
    public String[] getArgs()
    {
        String args = getArgsStr();
        
        if (args == null || args.length() == 0)
        {
            return new String[0];
        }
        
        StringTokenizer tk = new StringTokenizer(args, " ", false);
        String[] values = new String[tk.countTokens()];
        for(int i=0;tk.hasMoreTokens();i++)
            values[i] = tk.nextToken();
        return values;
    }
    
    /**
     * Get Chat
     * @return chat
     */
    public Chat getChat()
    {
        return chat;
    }
    
    /**
     * Get ParsedData
     * @return data
     */
    public ParsedData getData()
    {
        return data;
    }
    
    /**
     * Get Bot's Config
     * @return config
     */
    public Conf getConfig()
    {
        return config;
    }
    
    /**
     * Get the bot
     * @return 
     */
    public Bot getBot()
    {
        return bot;
    }
    
    /**
     * Get DataStore for this channel with given store name
     * @param name store name
     * @return store
     */
    public DataStore getStore(String name)
    {
        return DataStore.getDataStore(name,config.getChannel());
    }
    
    /**
     * Get DataStore for this channel with the value from call() as store name
     * @return store
     */
    public DataStore getCallAsStore()
    {
        return DataStore.getDataStore(command.call(),config.getChannel());
    }
    
    /**
     * Send some text to the server
     * @param text text
     */
    public void send(String text)
    {
        chat.send(text);
    }
    /**
     * Send some text as reply to the current nick
     * @param text text
     */
    public void reply(String text)
    {
        if(data.getNick()!=null)send("@"+data.getNick()+" "+text);
    }
    
    /**
     * Reply the current nick with "Permission Denied!"
     */
    public void replyPermissionDenied()
    {
        reply("Permission Denied!");
    }
    
    /**
     * Handy method to send some text as error
     * @param text 
     */
    public void sendError(String text)
    {
        send("[ERROR] "+text);
    }
    
    /**
     * Handy method to send some text as info
     * @param text 
     */
    public void sendInfo(String text)
    {
        send("[INFO] "+text);
    }
    
    /**
     * Log some error for this bot
     * @param text 
     */
    public void logErr(String text)
    {
        Utils.logBotErr(chat.getChannel(), text);
    }
    
    /**
     * Log some info for this bot
     * @param text 
     */
    public void logInfo(String text)
    {
        Utils.logBotInfo(chat.getChannel(), text);
    }
    
    /**
     * Clone this object of CommandEvent
     * @return cloned new object
     * @throws CloneNotSupportedException 
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
