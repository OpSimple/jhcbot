package org.simplelabz.jhcbot.command;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;

/**
 *
 * @author simple
 */
public class GOOGLE implements Command
{
    public static String      GOOGLE_KEY   ;
    public static String      GOOGLE_CX_ID ;
    
    static
    {
        updateGoogleKeys();
    }
    
    private int ITEMS = 3;
    
    @Override
    public String name()
    {
        return "Google Search";
    }

    @Override
    public String call()
    {
        return "google";
    }

    @Override
    public String desc()
    {
        return "Google search (google.com) Usage: "+Conf.TRIG+call()+" [keywords]";
    }

    @Override
    public boolean isHidden()
    {
        return false;
    }

    @Override
    public boolean listens()
    {
        return false;
    }

    @Override
    public void action(CommandEvent ev)
    {
        String args = ev.getArgsStr();
        if(args==null || args.trim().isEmpty())
        {
            ev.send("Incorrect Usage!\r"+desc());
            return;
        }
        args = args.trim();
        String send = "\r";
        {
            String[] arrargs = ev.getArgs();
            if(arrargs[0].startsWith("i="))
            {
                try
                {
                    int count = Integer.parseInt(arrargs[0].substring(2));
                    if(count>=1 && count<=10)
                    {
                        ITEMS = count;
                        args = args.substring(args.indexOf("i=")+arrargs[0].length());
                    }
                }
                catch(NumberFormatException ex)
                {
                    //do nothing
                }
            }
        }
        
        try
        {
            String urlstr = "https://www.googleapis.com/customsearch/v1?key="
                    +GOOGLE_KEY+"&cx="+GOOGLE_CX_ID+"&q="+URLEncoder.encode(args, "UTF-8");
            URL url = new URL(urlstr);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            
            JsonObject json = Json.parse(new java.io.InputStreamReader(con.getInputStream())).asObject();
            if(json.getString("error", null)!=null)
            {
                JsonObject err = json.get("error").asObject();
                ev.sendError(err.get("code").asInt()+"  "+err.get("message").asString());
                return;
            }
            JsonValue get = json.get("items");
            if(get==null || get.asArray().isEmpty())
            {
                ev.send("No results found for "+args+" !");
                return;
            }
            JsonArray items = get.asArray();
            Iterator<JsonValue> itr = items.values().iterator();
            for(int i = 1; itr.hasNext() ;i++)
            {
                if(i==(ITEMS+1))break;
                JsonObject next = itr.next().asObject();
                send=send+next.getString("title", "")+"\r"
                        +next.getString("link", "")+"\r"
                        +next.getString("snippet", "")+"\r\r";
            }
            ev.send(send);
        }
        catch(IOException ex)
        {
            Logger.getLogger(GOOGLE.class.getName()).log(Level.SEVERE, args, ex);
        }
    }

    @Override
    public void listen(CommandEvent e)
    {
        //nothing to do here
    }
    
    public static void updateGoogleKeys()
    {
        String key = Conf.conf.getProperty("GOOGLE_API_KEY");
        String cx = Conf.conf.getProperty("GOOGLE_CX_ID");
        if(key!=null && cx!=null && !key.trim().isEmpty() && !cx.trim().isEmpty())
        {
            GOOGLE_KEY = key.trim();
            GOOGLE_CX_ID = cx.trim();
        }
        else
        {
            throw new RuntimeException("Google keys empty");
        }
    }
}
