package org.simplelabz.jhcbot.command;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;


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
        return "Get current time of any timezone(default UTC). Usage: "+Conf.TRIG+call()+" <timezone>";
    }

    @Override
    public boolean isHidden()
    {
        return false;
    }

    @Override
    public void action(CommandEvent e)
    {
        String[] text = e.getArgs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        if(text.length==0)
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        else
            dateFormat.setTimeZone(TimeZone.getTimeZone(text[0]));
        
        e.send(dateFormat.format(e.getDate()));
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
