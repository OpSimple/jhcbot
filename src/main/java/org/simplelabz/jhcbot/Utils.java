package org.simplelabz.jhcbot;

import bsh.EvalError;
import bsh.Interpreter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.text.StrSubstitutor;

/**
 *
 * @author simple
 */
public class Utils
{
    /**
     * A global timer
     */
    public static Timer timer = null;
    
    static
    {
        timer = new Timer(true);
    }
    
    /**
     * Reads the bot's banner and replaces all declared variables.
     * @param bannerText the banner with variables in
     * @return the banner with substituted variables out
     */
    public static String parseBanner(String bannerText)
    {
        Properties conf = Conf.readConf(Conf.CONFIG);
        SimpleDateFormat date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        date.setTimeZone(TimeZone.getTimeZone("UTC"));
        conf.setProperty("DATE", date.format(new Date(System.currentTimeMillis())));
        return StrSubstitutor.replace(bannerText, conf);
    }
    
    /**
     * Reads all String line by line out of an InputStream
     * @param is InputStream
     * @param separator a separator between each line
     * @return collected String
     * @throws IOException 
     */
    public static String readAllString(InputStream is, String separator) throws IOException
    {
        BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = "";
        while((line=br.readLine())!=null)sb.append(line).append(separator);
        return sb.toString();
    }
    
    /**
     * Trims null and empty strings out of a String array
     * @param array array to be trimmed
     * @return trimmed array
     */
    public static String[] trimStrArray(String[] array)
    {
        Iterator<String> itr = Arrays.asList(array).iterator();
        ArrayList<String> trimed = new ArrayList<>();
        while(itr.hasNext())
        {
            String next = itr.next();
            if(next==null || next.trim().isEmpty())continue;
            trimed.add(next.trim());
        }
        return trimed.toArray(new String[trimed.size()]);
    }
        
    /**
     * Show Error message
     * @param msg error message to display
     */
    public static void showError(String msg)
    {
        System.err.println("[ERROR] "+msg);
    }

    /**
     * Show Info message
     * @param msg info message to display
     */
    public static void showInfo(String msg)
    {
        System.out.println("[INFO] "+msg);
    }
    
    /**
     * A handy method to log errors for a bot
     * @param id
     * @param text 
     */
    public static void logBotErr(String id, String text)
    {
        showError("@"+id+">> "+text);
    }
    
    /**
     * A handy method to log info messages for a bot
     * @param id
     * @param text 
     */
    public static void logBotInfo(String id, String text)
    {
        showInfo("@"+id+">> "+text);
    }
    
    /**
     * Creates a new beanshell interpreter with all commands pre loaded
     * @return the created beanshell interpreter
     */
    public static Interpreter newBshInterpreter()
    {
        Interpreter i = new Interpreter();
        try
        {
            if(Conf.CMDSDIR!=null && !Conf.CMDSDIR.trim().isEmpty())
                i.eval("addClassPath(\""+Conf.CMDSDIR+"\");");
            i.eval("addClassPath(\""+System.getProperty("user.dir")+"/cmds\");");
            i.eval("importCommands(\"/\");");
        }
        catch(EvalError ex)
        {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return i;
    }
    
    /**
     * Generate Random String
     * @param count length of random string
     * @param STRING chars to be randomised
     * @return a random generated string
     */
    public static String generateRandomString(int count, String STRING)
    {
        if(count<1)return null;
        StringBuilder builder = new StringBuilder();
        while(count-- != 0)
        {
            int character = (int)(Math.random()*STRING.length());
            builder.append(STRING.charAt(character));
        }
        return builder.toString();
    }
    
    /**
     * Generate Random String of all chars a-z, A-Z and 0-9
     * @param count length of random string
     * @return a random generated string
     */
    public static String generateRandomString(int count)
    {
        return generateRandomString(count, "abcdefghijklmnopqrstuvwxyz0123456789");
    }
    
    /**
     * Generate Random String of all chars 0-9
     * @param count length of random string
     * @return a random generated string
     */
    public static String generateRandomInt(int count)
    {
        return generateRandomString(count, "0123456789");
    }
    
    /**
     * A handy method to check if a class exists
     * @param name class
     * @return <code>true</code> if the class exists and <code>false</code> if not
     */
    public static boolean classExists(String name)
    {
        try
        {
            Class.forName(name);
        }
        catch(ClassNotFoundException ex)
        {
            return false;
        }
        return true;
    }
    
    /**
     * Returns human readable format of amount of data in bytes.
     * @param bytes amount of bytes to be represented in human readable format
     * @param si set true to represent in KB,MB,GB,.. and false for KiB,MiB,GiB,..
     * @return 
     */
    public static String hum(long bytes, boolean si)
    {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    
    /**
     * Tries to verify a trip
     * @param trip a trip to be verified
     * @return <code>true</code> if the trip is fine
     */
    public static boolean verifyTrip(String trip)
    {
        if(trip!=null)
        {
            return trip.matches("^[a-zA-Z0-9+/]{6}$");
        }
        throw new NullPointerException("Trip is null");
    }
    
    /**
     * Tries to verify a trip
     * @param nick a nick to be verified
     * @return <code>true</code> if the nick is fine
     */
    public static boolean verifyNick(String nick)
    {
        if(nick!=null)
        {
            return nick.matches("^[a-zA-Z0-9_]{1,"+Conf.MAX_NICK_LENGTH+"}$");
        }
        throw new NullPointerException("Nick is null");
    }
    
    /**
     * Tries to verify server's URI
     * @param URI a URI to be verified
     * @return <code>true</code> if the URI is fine
     */
    public static boolean verifyURI(String URI)
    {
        try
        {
            new java.net.URI(URI);
        }
        catch (URISyntaxException ex)
        {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return (URI.startsWith("ws://")||URI.startsWith("wss://"));
    }
}
