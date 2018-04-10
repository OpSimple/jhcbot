package org.simplelabz.jhcbot.command;

import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;


/**
 *
 * @author simple
 */
public class ENABLE implements Command
{
    @Override
    public String name()
    {
        return "Enable";
    }

    @Override
    public String call()
    {
        return "enable";
    }

    @Override
    public String desc()
    {
        return "Enable the bot if the bot is disabled! Usage: "+Conf.TRIG+call();
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
    public void action(CommandEvent ev)
    {
        if(ev.isAdmin() || ev.isAgent() || ev.getData().isMod() || ev.getData().isAdmin())
        {
            if(ev.getBot().isEnabled())
            {
                ev.send(Conf.NAME+" is already enabled!");
                return;
            }
            ev.getBot().setEnabled(true);
            ev.send(Conf.NAME+" is now enabled for all non-admins!");
        }
        else
        {
            ev.replyPermissionDenied();
        }
    }

    @Override
    public void listen(CommandEvent event)
    {
        //nothing
    }
}
