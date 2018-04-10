package org.simplelabz.jhcbot.command;

import org.simplelabz.jhcbot.Chat.ParsedData;
import org.simplelabz.jhcbot.command.event.CommandEvent;

/**
 *
 * @author simple
 */
public class HCSTATS implements Command
{
    @Override
    public String name()
    {
        return "hack.chat stats";
    }

    @Override
    public String call()
    {
        return "hcstats";
    }

    @Override
    public String desc()
    {
        return "Display current hack.chat stats";
    }

    @Override
    public boolean isHidden()
    {
        return false;
    }

    @Override
    public boolean listens()
    {
        return true;
    }

    @Override
    public void action(CommandEvent e)
    {
        e.getChat().stats();
    }

    @Override
    public void listen(CommandEvent e)
    {
        if(e.getData().getCallType()==ParsedData.STATS)
        {
            e.send(e.getText());
        }
    }
}
