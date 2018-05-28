package org.simplelabz.jhcbot;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Manages all global configurations
 * @author simple
 */
public class Conf
{
    // BotConfig values
    public static final String      VERSION = "1.0";
    
    public static String    URI  ;
    public static String[]  CHANNELS;
    public static String    NAME    ;
    public static String    PASSWORD;
    public static String    ART     ;
    public static String    TRIG   ;
    public static String[]  TRIPS   ;
    public static String    ADMIN   ;
    public static String    BANNER  ;
    public static String[]  EXTCMDS ;
    public static String    CONFIG  ;
    public static String    DATADIR ;
    public static String    CMDSDIR ;
    
    public static int       MAX_BOTS = 8;
    public static int       MAX_NICK_LENGTH  = 24;
    public static int       MAX_TEXT_LENGTH = 700;
    public static int       MAX_THREADS_LIMIT = 200;
    
    public static Properties conf;
    
    static
    {
        updateConf();
    }
    
    /**
     * Read the config file and update config values
     */
    public static void updateConf()
    {
        conf = readConf(CONFIG);
        
        if(conf!=null)
        {
            //setup conf values
            URI = conf.getProperty("URI");
            NAME = conf.getProperty("NAME");
            ART = conf.getProperty("ART");
            PASSWORD = conf.getProperty("PASSWORD");
            TRIG = conf.getProperty("TRIGGER");
            ADMIN = conf.getProperty("ADMIN");
            DATADIR = conf.getProperty("DATADIR");
            CMDSDIR = conf.getProperty("CMDSDIR");
            BANNER = conf.getProperty("BANNER");
            String channels = conf.getProperty("CHANNELS");
            String trips = conf.getProperty("TRIPS");
            String extcmds = conf.getProperty("EXTCMDS");
            TRIPS = (trips!=null)?ArrayUtils.add(trips.split(";"), ADMIN):null;
            EXTCMDS = (extcmds!=null)?extcmds.split(";"):null;
            CHANNELS = (channels!=null)?channels.split(";"):null;
            String maxbots = conf.getProperty("MAXBOTS");
            String maxthreads = conf.getProperty("MAXTHREADS");
            
            //fix some values if possible
            if(URI!=null)URI = URI.trim();
            if(CHANNELS!=null)CHANNELS = Utils.trimStrArray(CHANNELS);
            if(NAME!=null)NAME = NAME.trim();
            if(ART!=null)ART = ART.trim();
            if(PASSWORD!=null)PASSWORD = PASSWORD.trim();
            if(TRIG!=null)TRIG = TRIG.trim();
            if(ADMIN!=null)ADMIN = ADMIN.trim();
            if(DATADIR!=null)
            {
                DATADIR = DATADIR.trim();
                new File(DATADIR).mkdirs();
            }
            if(CMDSDIR!=null)
            {
                CMDSDIR = CMDSDIR.trim();
                new File(DATADIR).mkdirs();
            }
            if(TRIPS!=null && TRIPS.length!=0)TRIPS = Utils.trimStrArray(TRIPS);
            if(EXTCMDS!=null && EXTCMDS.length!=0)
                Bot.COMMANDS = ArrayUtils.addAll(Bot.COMMANDS, Utils.trimStrArray(EXTCMDS));
            if(maxbots!=null)MAX_BOTS = Integer.parseInt(maxbots);
            if(maxthreads!=null)MAX_THREADS_LIMIT = Integer.parseInt(maxthreads);
        }
        else
        {
            Utils.showError("No jhcbot.properties or jhcbot.xml file exists!");
        }

        if(conf!=null)
        {
            //Give a view of current values to a cmdline user. :)
            System.out.println("Read Config:");
            conf.list(System.out);
            System.out.println("\n");
            Logger.getLogger(Conf.class.getName()).log(Level.FINE, "Done reading config file. [{0}]",CONFIG);
        }
    }

    /**
     * Read Bot's configuration from the respected xml or properties config file.
     * @param file File's Path to read config (if null then config file auto decided)
     * @return Properties file read
     */
    public static Properties readConf(String file)
    {
        Properties conf = null;
        File confFile;
        try
        {
            conf = new Properties();
            // Check for a valid config file.
            if(file!=null && (file.endsWith(".properties") || file.endsWith(".xml")) 
                && new File(file).getAbsoluteFile().exists() && !(new File(file).isDirectory()))
            {
                confFile = new File(file);
                CONFIG = file;
            }
            else if(!(new File("jhcbot.properties").exists()))
            {
                confFile = new File("jhcbot.xml");
                CONFIG = "jhcbot.xml";
            }
            else
            {
                confFile = new File("jhcbot.properties");
                CONFIG = "jhcbot.properties";
            }

            //Check if config file exists
            if(!(new File(CONFIG).exists()))return null;

            //try to load config file as properties
            if(CONFIG.endsWith(".xml"))
                conf.loadFromXML(new java.io.FileInputStream(confFile));
            else
                conf.load(new java.io.FileInputStream(confFile));
        }
        catch(IOException ex)
        {
            Utils.showError("Error while trying to read config file!\n"+ex);
            Logger.getLogger(Conf.class.getName()).log(Level.SEVERE, "Bot's config file not found! [{0}]", file);
            Logger.getLogger(Conf.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
        return conf;
    }

    /**
     * Check if all set values of Bot configurations are valid!
     * @return true if all values read from config are acceptable
     */
    public static boolean isConfigAcceptable()
    {
        //Check if the required values aren't null or empty
        for(String value : new String[]{URI,NAME,ART,TRIG})
        {
            if(value==null || value.isEmpty())
            {
                
                Logger.getLogger(Conf.class.getName()).log(Level.SEVERE, "Some required value in config is absent or empty!");
                return false;
            }
        }

        //Check Bot's Display name
        if(!Utils.verifyNick(NAME))
        {
            Logger.getLogger(Conf.class.getName()).log(Level.SEVERE, "NAME in config file should have only a-z,A-Z,0-9 and _");
            return false;
        }
        
        //check server url
        if(!Utils.verifyURI(URI))
        {
            Logger.getLogger(Conf.class.getName()).log(Level.SEVERE, "URI must be a valid websocket server url!");    
            return false;
        }
        
        //Check if we have at least one channel to join
        if(CHANNELS.length==0 || CHANNELS[0].trim().isEmpty())
        {
            Logger.getLogger(Conf.class.getName()).log(Level.SEVERE, "CHANNELS must have at least one channel!");    
            return false;
        }
        
        //Check ADMIN's trip
        if(ADMIN!=null && !ADMIN.isEmpty())
        {
            if(!Utils.verifyTrip(ADMIN))
            {
                Logger.getLogger(Conf.class.getName()).log(Level.SEVERE, "ADMIN trip seems to have illegal characters!");    
                return false;
            }
        }
        //Check other trips
        if(TRIPS!=null || TRIPS.length != 0)
        {
            for(String trip : TRIPS)
            {
                if(!Utils.verifyTrip(trip))
                {
                    Logger.getLogger(Conf.class.getName()).log(Level.SEVERE, "Some TRIPS seems to have illegal characters!");
                    return false;
                }
            }
        }
        return true;
    }
    
    
    private String channel;
    private String agentNick;
    private String agentTrip;
    private String banner;
    
    /**
     * Creates a new config
     */
    public Conf()
    {
        this.banner = BANNER;
        this.channel = CHANNELS[0];
        this.agentNick = null;
        this.agentTrip = null;
    }
    
    /**
     * Set the channel
     * @param channel 
     */
    public void setChannel(String channel)
    {
        this.channel = channel;
    }
    
    /**
     * Set the invite agent's nick and trip
     * @param agentNick
     * @param agentTrip 
     */
    public void setAgent(String agentNick, String agentTrip)
    {
        this.agentNick = agentNick;
        this.agentTrip = agentTrip;
    }
    
    /**
     * Set a banner
     * @param banner 
     */
    public void setBanner(String banner)
    {
        this.banner = banner;
    }
    
    /**
     * Get channel
     * @return channel
     */
    public String getChannel()
    {
        return channel;
    }
    
    /**
     * Get banner text
     * @return banner text
     */
    public String getBanner()
    {
        return banner;
    }
    
    /**
     * Get Invite agent's nick
     * @return invite agent's nick
     */
    public String getAgentNick()
    {
        return agentNick;
    }
    
    /**
     * Get Invite agent's trip
     * @return invite agent's trip
     */
    public String getAgentTrip()
    {
        return agentTrip;
    }
}
