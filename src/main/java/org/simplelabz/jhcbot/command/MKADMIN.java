package org.simplelabz.jhcbot.command;

import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.Utils;
import org.simplelabz.jhcbot.command.event.CommandEvent;

/**
 *
 * @author simple
 */
public class MKADMIN implements Command
{
    @Override
    public String name()
    {
        return "Make Admin";
    }

    @Override
    public String call()
    {
        return "mkadmin";
    }

    @Override
    public String desc()
    {
        return "Make a trip "+Conf.NAME+"'s admin. Usage: "+Conf.TRIG+call()+" <trip>";
    }

    @Override
    public boolean isHidden()
    {
        return false;
    }
    
    @Override
    public void action(CommandEvent e)
    {
        if(e.isAdmin())
        {
            String[] args = e.getArgs();
            if(args.length==0)
            {
                e.send("Incorrect Usage!\r"+desc());
                return;
            }
            String trip = args[0];
            if(!Utils.verifyTrip(trip))
            {
                e.send("Invalid trip!");
                return;
            }
            String[] TRIPS = Conf.TRIPS;
            if(Arrays.asList(TRIPS).contains(trip))return;
            Conf.TRIPS = ArrayUtils.insert(TRIPS.length-1, TRIPS, trip);
            e.send(trip+" is now an admin!");
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
