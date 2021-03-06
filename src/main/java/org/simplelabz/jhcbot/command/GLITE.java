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
            JsonArray items = GoogleSvc.search(args);
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