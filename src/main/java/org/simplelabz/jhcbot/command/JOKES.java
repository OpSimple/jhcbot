package org.simplelabz.jhcbot.command;

import com.eclipsesource.json.Json;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.text.StringEscapeUtils;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;

/**
 *
 * @author simple
 */
public class JOKES implements Command
{
    @Override
    public String name()
    {
        return "Jokes";
    }

    @Override
    public String call()
    {
        return "jokes";
    }
    
    @Override
    public String desc()
    {
        return "Laughing is good for health!! Usage: "+Conf.TRIG+call()+" <enter your name>";
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
        String fname = "Chuck";
        String lname = "Norris";
        String[] args = ev.getArgs();
        if(args.length!=0)
        {
            fname = args[0];
            lname = (args.length != 1)?args[args.length-1]:"";
        }
        
        try {
            URL url = new URL("http://api.icndb.com/jokes/random?firstName="+fname+"&lastName="+lname);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            
            String joke = Json.parse(new java.io.InputStreamReader(con.getInputStream()))
                    .asObject().get("value").asObject().get("joke").asString();
            ev.send(StringEscapeUtils.unescapeXml(joke));
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(JOKES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JOKES.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void listen(CommandEvent e)
    {
        //nothing to do here
    }
}
