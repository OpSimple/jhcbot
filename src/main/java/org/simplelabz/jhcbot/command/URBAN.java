package org.simplelabz.jhcbot.command;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;

/**
 *
 * @author simple
 */
public class URBAN implements Command
{
    @Override
    public String name()
    {
        return "Urban Dictionary";
    }

    @Override
    public String call()
    {
        return "urban";
    }

    @Override
    public String desc()
    {
        return "Find a word in urban dictionary Usage:"+Conf.TRIG+call()+" <a word to search>";
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
        String[] args = ev.getArgs();
        if(args==null)
        {
            ev.send("Incorrect Usage!\r"+desc());
            return;
        }
        String word = args[0];
        if(word.trim().isEmpty())
        {
            ev.reply("Its not a word!");
            return;
        }
        try
        {
            StringBuilder sb = new StringBuilder();
            URL url = new URL("http://api.urbandictionary.com/v0/define?term="+URLEncoder.encode(word, "UTF-8"));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            JsonObject json = Json.parse(new java.io.InputStreamReader(con.getInputStream())).asObject();
            JsonArray list = json.get("list").asArray();
            if(!list.isEmpty())
            {
                JsonObject item = list.get(0).asObject();
                sb.append("[").append(item.getString("word", "")).append("]\r");
                sb.append(item.getString("definition", "")).append("\r");
                sb.append(item.getString("permalink", ""));
            }
            ev.send(sb.toString());
        } catch (MalformedURLException ex) {
            Logger.getLogger(URBAN.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(URBAN.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void listen(CommandEvent e)
    {
        //nothing to do here
    }
}
