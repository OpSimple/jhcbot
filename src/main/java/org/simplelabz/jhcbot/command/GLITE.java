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
public class GLITE implements Command
{
    private int ITEMS = 5;
    
    @Override
    public String name()
    {
        return "Google Lite";
    }

    @Override
    public String call()
    {
        return "glite";
    }

    @Override
    public String desc()
    {
        return "Google search with no descriptions Usage: "+Conf.TRIG+call()+" [keywords]";
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
        
        try
        {
            String urlstr = "https://www.googleapis.com/customsearch/v1?key="
                    +GOOGLE.GOOGLE_KEY+"&cx="+GOOGLE.GOOGLE_CX_ID+"&q="+URLEncoder.encode(args, "UTF-8");
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
                        +next.getString("link", "")+"\r";
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
}