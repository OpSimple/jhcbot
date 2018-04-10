package org.simplelabz.jhcbot.command;

import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;

/**
 *
 * @author simple
 */
public class RMADMIN implements Command
{
    @Override
    public String name()
    {
        return "Remove an Admin";
    }

    @Override
    public String call()
    {
        return "rmadmin";
    }

    @Override
    public String desc()
    {
        return "Remove a trip from "+Conf.NAME+"'s admin. Usage: "+Conf.TRIG+call()+" <trip>";
    }

    @Override
    public boolean isHidden()
    {
        return false;
    }

    @Override
    public void action(CommandEvent e)
    {
        if(e.isBotAdmin())
        {
            String[] args = e.getArgs();
            if(args.length==0)
            {
                e.send("Incorrect Usage!\r"+desc());
                return;
            }
            String trip = args[0];
            
            if(trip.equals(Conf.ADMIN))
            {
                e.reply("LoL!");
            }
            else
            {
                String[] TRIPS = Conf.TRIPS;
                if(!Arrays.asList(TRIPS).contains(trip))return;
                Conf.TRIPS = ArrayUtils.removeElement(TRIPS, args[0]);
                e.send(trip+" is not an admin now!");
            }
        }
        else
        {
            e.replyPermissionDenied();
        }
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
