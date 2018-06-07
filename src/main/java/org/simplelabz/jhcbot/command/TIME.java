package org.simplelabz.jhcbot.command;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;
import org.simplelabz.jhcbot.command.svc.GoogleSvc;


/**
 *
 * @author simple
 */
public class TIME implements Command
{
    @Override
    public String name()
    {
        return "Time";
    }

    @Override
    public String call()
    {
        return "time";
    }

    @Override
    public String desc()
    {
        return "Get current time of any timezone(default GMT). Usage: "+Conf.TRIG+call()+" <timezone>";
    }

    @Override
    public boolean isHidden()
    {
        return false;
    }

    @Override
    public void action(CommandEvent ev)
    {
        String args = ev.getArgsStr().trim();
        TimeZone zone = TimeZone.getTimeZone(args);
        if(!zone.getID().equals(args))
        {
            try
            {
                String timezone = GoogleSvc.getTimezoneByLocation(args, ev.getTime());
                zone = TimeZone.getTimeZone(timezone);
            }
            catch (GoogleSvc.GoogleSvcException ex)
            {
                Logger.getLogger(TIME.class.getName()).log(Level.SEVERE, null, ex);
                ev.sendError(ex.getMessage());
            }
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        dateFormat.setTimeZone(zone);
        ev.send(dateFormat.format(ev.getDate()));
    }

    public boolean listens()
    {
        return false;
    }
    public void listen(CommandEvent e)
    {
        //nothing to do here
    }
}
