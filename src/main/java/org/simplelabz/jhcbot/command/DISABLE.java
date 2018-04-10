package org.simplelabz.jhcbot.command;

import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;


/**
 *
 * @author simple
 */
public class DISABLE implements Command
{

    @Override
    public String name()
    {
        return "Disable";
    }

    @Override
    public String call()
    {
        return "disable";
    }

    @Override
    public String desc()
    {
        return "Disable the bot! (optionally for some seconds) Usage: "+Conf.TRIG+call()+" <seconds to disable for(optional)>";
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
            String[] args = ev.getArgs();
            
            if(!ev.getBot().isEnabled())
            {
                ev.send(Conf.NAME+" is already disabled!");
                return;
            }
            
            if(args.length!=0)
            {
                String argv = args[0];
                try
                {
                    int time = Integer.parseInt(argv);
                    ev.getBot().disable(time);
                    ev.send(Conf.NAME+" is disabled for all non-admins for "+time+"s !");
                    return;
                }
                catch(NumberFormatException ex)
                {
                    ev.send("Given argument was not a time in seconds!");
                }
            }
            ev.getBot().setEnabled(false);
            ev.send(Conf.NAME+" is now disabled for all non-admins!");
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
