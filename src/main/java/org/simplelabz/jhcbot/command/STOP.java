package org.simplelabz.jhcbot.command;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;


/**
 *
 * @author simple
 */
public class STOP implements Command
{
    @Override
    public String name()
    {
        return "Stop";
    }

    @Override
    public String call()
    {
        return "stop";
    }

    @Override
    public String desc()
    {
        return "Stop "+Conf.NAME+"! Usage: "+Conf.TRIG+call()+" <reason to log (optional)>";
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
            String args = e.getArgsStr();
            if(args!=null && !args.trim().isEmpty())
            {
                Logger.getGlobal().log(Level.SEVERE, args);
            }
            
            Logger.getLogger("Bot").log(Level.INFO, "{0} stopped the bot!", e.getNick());
            e.send(Conf.NAME+" going offline! BYE!");
            Logger.getLogger("Bot").log(Level.SEVERE, "Shutting down the system completely!");
            System.exit(0);
        }
        else
        {
            e.send("@"+e.getNick()+" Permission denied!");
        }
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
