package org.simplelabz.jhcbot.command;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;

/**
 *
 * @author simple
 */
public class WIKI implements Command
{
    public static int MAX_CHARS = 400;
    
    @Override
    public String name()
    {
        return "Wikipedia Search";
    }

    @Override
    public String call()
    {
        return "wiki";
    }

    @Override
    public String desc()
    {
        return "Wikipedia article Usage: "+Conf.TRIG+call()+" [article]";
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
            Connection con = Jsoup.connect("https://en.wikipedia.org/wiki/"+URLEncoder.encode(args.trim().replaceAll(" +", "_"), "UTF-8"));
            Connection.Response resp = con.execute();
            Document doc = resp.parse();
            send = send+"["+doc.title()+"]\r";
            String text = doc.select("div.mw-parser-output p").first().text();
            if(text.length()>500)text = text.substring(0, (MAX_CHARS-3))+"...";
            send = send+text+"\r"+resp.url();
            ev.send(send);
        }
        catch(IOException ex)
        {
            Logger.getLogger(WIKI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void listen(CommandEvent event)
    {
        //Nothing to do here
    }
    
}
