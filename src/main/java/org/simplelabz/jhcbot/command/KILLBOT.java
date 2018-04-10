package org.simplelabz.jhcbot.command;

import org.simplelabz.jhcbot.BotsManager;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;


/**
 *
 * @author simple
 */
public class KILLBOT implements Command
{
    @Override
    public String name()
    {
        return "Kill Bots";
    }

    @Override
    public String call()
    {
        return "killbot";
    }

    @Override
    public String desc()
    {
        return "Kill bots instances of "+Conf.NAME+" are running. Usage: "+Conf.TRIG+call()+" <channel>";
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
            String channel = (args[0].startsWith("?"))?args[0].substring(1):args[0];
            BotsManager.removeByID(channel);
            e.send(Conf.NAME+" terminated on ?"+channel);
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
