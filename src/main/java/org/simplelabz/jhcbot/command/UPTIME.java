package org.simplelabz.jhcbot.command;

import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;

/**
 *
 * @author simple
 */
public class UPTIME implements Command
{
    @Override
    public String name()
    {
        return "Uptime";
    }

    @Override
    public String call()
    {
        return "uptime";
    }

    @Override
    public String desc()
    {
        return "Total running time of "+Conf.NAME+" in this channel";
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
        e.send(new java.text.SimpleDateFormat("HH:mm:ss:SSS").format(e.getBot().getUptime()));
    }

    @Override
    public void listen(CommandEvent e)
    {
        //nothing to do here
    }
}
