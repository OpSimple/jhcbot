package org.simplelabz.jhcbot.command;

import java.util.Iterator;
import java.util.Map;
import org.simplelabz.jhcbot.Bot;
import org.simplelabz.jhcbot.BotsManager;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;


/**
 *
 * @author simple
 */
public class LSBOTS implements Command
{
    @Override
    public String name()
    {
        return "List Bots";
    }

    @Override
    public String call()
    {
        return "lsbots";
    }

    @Override
    public String desc()
    {
        return "List channels where a new instance of "+Conf.NAME+" is running";
    }

    @Override
    public boolean isHidden()
    {
        return false;
    }

    @Override
    public void action(CommandEvent e)
    {
        String send = "Instances of "+Conf.NAME+" are running on channels:\r";
        Iterator<Map.Entry<String, Bot>> itr = BotsManager.getBots().iterator();
        while(itr.hasNext())
        {
            Map.Entry<String, Bot> entry = itr.next();
            
            send = send+ "?"+entry.getValue().getConfig().getChannel()+" ,";
        }
        e.send(send.substring(0, send.length()-1));
    }
    
    @Override
    public boolean listens()
    {
        return false;
    }
    @Override
    public void listen(CommandEvent e)
    {
        //nothing to do here
    }
}
