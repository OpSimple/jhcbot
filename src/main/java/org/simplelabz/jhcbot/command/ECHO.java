package org.simplelabz.jhcbot.command;

import org.simplelabz.jhcbot.command.event.CommandEvent;

/**
 *
 * @author simple
 */
public class ECHO implements Command
{
    @Override
    public String name()
    {
        return "Echo";
    }

    @Override
    public String call()
    {
        return "echo";
    }

    @Override
    public String desc()
    {
        return "Outputs the strings it is being passed as arguments";
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
        e.send(e.getArgsStr());
    }

    @Override
    public void listen(CommandEvent e)
    {
        //nothing to do here
    }
}
