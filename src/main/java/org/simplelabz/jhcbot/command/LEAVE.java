package org.simplelabz.jhcbot.command;

import org.simplelabz.jhcbot.BotsManager;
import org.simplelabz.jhcbot.command.event.CommandEvent;


/**
 *
 * @author simple
 */
public class LEAVE implements Command
{
    @Override
    public String name()
    {
        return "Leave";
    }

    @Override
    public String call()
    {
        return "leave";
    }

    @Override
    public String desc()
    {
        return "Leave this channel";
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
        if(e.isAdmin()||e.isAgent())
        {
            BotsManager.remove(e.getBot());
        }
        else
        {
            e.replyPermissionDenied();
        }
    }

    @Override
    public void listen(CommandEvent e)
    {
        //nothing to do here
    }
}
