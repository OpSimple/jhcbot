package org.simplelabz.jhcbot.command;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.Utils;
import org.simplelabz.jhcbot.command.event.CommandEvent;


/**
 *
 * @author simple
 */
public class WTTR implements Command
{
    @Override
    public String name()
    {
        return "Weather";
    }

    @Override
    public String call()
    {
        return "wttr";
    }

    @Override
    public String desc()
    {
        return "Get today's weather with the help of wttr.in. Visit http://wttr.in/:help for more details. Usage: "+Conf.TRIG+call()+" <location>";
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
    public void action(CommandEvent e)
    {
        String args = e.getArgsStr();
        if(args==null || args.trim().isEmpty())
        {
            e.send("Incorrect Usage!\r"+desc());
            return;
        }
        args = args.trim();
        String send = "\r";
        try
        {
            URL url = new URL("http://wttr.in/"+URLEncoder.encode(args, "UTF-8")+"?0pqm");
            if(args.endsWith(".png")||args.endsWith(".PNG"))
            {
                e.send("http://wttr.in/"+URLEncoder.encode(args, "UTF-8"));
                return;
            }
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "curl");
            send = Utils.readAllString(con.getInputStream(),"\r");
            if(send.contains("Unknown location"))
                e.sendError("Unknown location: "+args);
            else
                e.send(send.replaceAll("\u001B\\[[;\\d]*m", ""));
        }
        catch (MalformedURLException ex)
        {
            e.send("Incorrect Usage!\r"+desc());
            Logger.getLogger(WTTR.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            e.send("[ERROR] Could not connect and read to wttr.in!");
            Logger.getLogger(WTTR.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void listen(CommandEvent e)
    {
        //nothing to do here
    }
}
