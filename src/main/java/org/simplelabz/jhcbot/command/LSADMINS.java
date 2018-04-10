package org.simplelabz.jhcbot.command;

import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;


/**
 *
 * @author simple
 */
public class LSADMINS implements Command
{
    @Override
    public String name()
    {
        return "List Admins";
    }

    @Override
    public String call()
    {
        return "lsadmins";
    }

    @Override
    public String desc()
    {
        return "List the current list of "+Conf.NAME+" admins.";
    }

    @Override
    public boolean isHidden()
    {
        return false;
    }

    @Override
    public void action(CommandEvent e)
    {
        String text = "Current list of "+Conf.NAME+" admins:\r";
        int pair=1;
        for(String trip: Conf.TRIPS)
        {
            text = text + trip+"   ";
            ++pair;
            if(pair==3)
            {
                text = text+"\r";
                pair=1;
            }
        }
        e.send(text);
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
