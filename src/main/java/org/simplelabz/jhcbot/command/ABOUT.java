package org.simplelabz.jhcbot.command;

import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;

/**
 *
 * @author simple
 */
public class ABOUT implements Command
{
    @Override
    public String name()
    {
        return "About";
    }

    @Override
    public String call()
    {
        return "about";
    }

    @Override
    public String desc()
    {
        return "About "+Conf.NAME;
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
        String send = Conf.ART+" "+Conf.NAME+" is a simple bot for hack.chat created in java by @simple .\r"
                + "It can run on JavaSE as well as on Android.\r"
                + "This bot is a gift for my dear friend @wwandrew\r"
                + "Sources: https://github.com/OpSimple/jhcbot.git";
        e.send(send);
    }

    @Override
    public void listen(CommandEvent e)
    {
        //nothing to do here
    }
}
