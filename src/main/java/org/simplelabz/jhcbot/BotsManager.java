package org.simplelabz.jhcbot;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A bot handler to create, destroy and keep a record of all bots.
 * @author simple
 */
public class BotsManager
{
    private static Map<String,Bot> bots = new HashMap<>(1);
    
    /**
     * Add a bot to the record
     * @param bot to be added
     */
    public static void add(Bot bot)
    {
        bots.put(bot.getConfig().getChannel(), bot);
    }
    
    /**
     * Remove a bot from the record and kill it as well.
     * @param id bot id
     */
    public static void removeByID(String id)
    {
        remove(bots.get(id));
    }
    
    /**
     * Remove a bot from the record and kill it as well.
     * @param bot bot to be removed
     */
    public static void remove(Bot bot)
    {
        bot.stop();
        bots.remove(bot.getChannel());
        Logger.getLogger(BotsManager.class.getName()).log(Level.INFO, "[MAIN] Bot Removed by BotsManager on ?{0} !",bot.getChannel());
        System.gc();
    }
    
    /**
     * Get a Set of Bots in the record
     * @return 
     */
    public static Set<Entry<String,Bot>> getBots()
    {
        return bots.entrySet();
    }
    
    /**
     * Get a Bot from the record by its id
     * @param id the Bot's id
     * @return the bot
     */
    public static Bot get(String id)
    {
        return bots.get(id);
    }
    
    /**
     * Get the count of bots present in the record
     * @return the count of bots present in the record
     */
    public static int getCount()
    {
        return bots.size();
    }
    
    /**
     * Create a new bot, start it and add it to the record, all using the provided 
     * {@link org.simplelabz.jhcbot.Conf} config.
     * @param config the config needed to start the bot
     * @return the created bot
     * @throws org.simplelabz.jhcbot.BotsManager.BotNotCreatedException in two cases:
     * <ul>
     * <li>When the count of bots is about to exceed the maximum allowed</li>
     * <li>When the bot already exist on any given channel</li>
     * </ul>
     * @see org.simplelabz.jhcbot.Conf
     */
    public static Bot createNewBot(Conf config)throws BotNotCreatedException
    {
        if(bots.size() >= Conf.MAX_BOTS || Thread.activeCount()>=200) throw new BotNotCreatedException("No more bots allowed!");
        if(bots.keySet().contains(config.getChannel())) throw new BotNotCreatedException("Bot already exists on ?"+config.getChannel());
        Bot bot = new Bot(config);
        bot.start();
        add(bot);
        Logger.getLogger(BotsManager.class.getName()).log(Level.INFO, "[MAIN] Bot Created by BotsManager on ?{0} !",config.getChannel());
        return bot;
    }
    
    /**
     * Create a new bot, start it and add it to the record, all using the provided channel.
     * An optional banner maybe given to send when the bot joins the channel.
     * @param channel the channel to make the bot join it
     * @param banner it is send when the bot joins the channel
     * @return the created bot
     * @throws org.simplelabz.jhcbot.BotsManager.BotNotCreatedException in two cases:
     * <ul>
     * <li>When the count of bots is about to exceed the maximum allowed</li>
     * <li>When the bot already exist on any given channel</li>
     * </ul>
     */
    public static Bot createNewBot(String channel, String banner)throws BotNotCreatedException
    {
        Conf config = new Conf();
        config.setChannel(channel);
        config.setBanner(banner);
        return createNewBot(config);
    }
    
    /**
     * Create a new bot, start it and add it to the record, all using the provided channel.
     * The invite agent's nick and trip maybe set to make the bot aware that its an invitation.
     * @param channel
     * @param anick
     * @param atrip
     * @return
     * @throws org.simplelabz.jhcbot.BotsManager.BotNotCreatedException in two cases:
     * <ul>
     * <li>When the count of bots is about to exceed the maximum allowed</li>
     * <li>When the bot already exist on any given channel</li>
     * </ul>
     */
    public static Bot createNewBot(String channel, String anick, String atrip)throws BotNotCreatedException
    {
        Conf config = new Conf();
        config.setChannel(channel);
        config.setAgent(anick, atrip);
        return createNewBot(config);
    }
    
    /**
     * It is thrown when We can't create the Bot cause of some reasons.
     */
    public static class BotNotCreatedException extends Exception
    {
        public BotNotCreatedException(String msg)
        {
            super(msg);
        }
    }
}
