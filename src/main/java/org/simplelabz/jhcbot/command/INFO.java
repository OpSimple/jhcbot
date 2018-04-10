package org.simplelabz.jhcbot.command;

import org.simplelabz.jhcbot.Bot;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;


/**
 *
 * @author simple
 */
public class INFO implements Command
{
    @Override
    public String name()
    {
        return "Info";
    }

    @Override
    public String call()
    {
        return "info";
    }

    @Override
    public String desc()
    {
        return "Info about any command. Usage: "+Conf.TRIG+call()+" <command>";
    }

    @Override
    public boolean isHidden()
    {
        return false;
    }

    @Override
    public void action(CommandEvent e)
    {
        String[] args = e.getArgs();
        Command command = null;
        if(args.length==0)
        {
             String info = "[INFO] "+e.getCommand().name()
                     +"("+e.getCommand().call()+") : "+e.getCommand().desc()+"\r";
             e.send(info);
             return;
        }
        for(String part: args)
        {
             for(String com : Bot.COMMANDS)
            {
                if(part.equals(com))
                {
                    command = Bot.callCommand(com);
                    if(command.isHidden())continue;
                    String info = "[INFO] "+command.name()
                        +"("+command.call()+") : "+command.desc()+"\r";
                    e.send(info);
                }
            }
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