package org.simplelabz.jhcbot.command;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import java.util.Iterator;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;
import org.simplelabz.jhcbot.command.svc.GoogleSvc;

/**
 *
 * @author simple
 */
public class GOOGLE implements Command
{
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
            JsonArray items = GoogleSvc.search(args);
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
        catch(GoogleSvc.GoogleSvcException ex)
        {
            ev.sendError(ex.getMessage());
        }
    }

    @Override
    public void listen(CommandEvent e)
    {
        //nothing to do here
    }
}
