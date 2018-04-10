package org.simplelabz.jhcbot.command;

import org.simplelabz.jhcbot.Bot;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;


/**
 *
 * @author simple
 */
public class HELP implements Command
{
    @Override
    public String name()
    {
        return "Help";
    }

    @Override
    public String call()
    {
        return "help";
    }

    @Override
    public String desc()
    {
        return "show help text for "+Conf.NAME;
    }

    @Override
    public boolean isHidden()
    {
        return false;
    }

    @Override
    public void action(CommandEvent e)
    {
        String help = "\r"+Conf.ART+" "+Conf.NAME+" v"+Conf.VERSION+"  Usage: "+Conf.TRIG+"<command>\r\r"
            + "Available commands:\r";
        Command command = null;
        for(String com : Bot.COMMANDS)
        {
            command = Bot.callCommand(com);
            if(command!=null && !command.isHidden())
            {
                help = help+command.call()+", ";
            }
        }
        help = help.substring(0, help.length()-2)+"\r";
        e.send(help);
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
