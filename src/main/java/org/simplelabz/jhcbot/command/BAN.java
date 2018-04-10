package org.simplelabz.jhcbot.command;

import java.util.Arrays;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;
import org.simplelabz.jhcbot.store.DataStore;

/**
 *
 * @author simple
 */
public class BAN implements Command
{
    @Override
    public String name()
    {
         return "Ban";
    }

    @Override
    public String call()
    {
         return "ban";
    }

    @Override
    public String desc()
    {
         return "Use it to ban a nickname or trip from using "+Conf.NAME+" Usage: "+Conf.TRIG+call()+" <nick/trip>";
    }

    @Override
    public boolean isHidden()
    {
         return false;
    }

    @Override
    public void action(CommandEvent e)
    {
        if(e.isAdmin() || e.getData().isAdmin() || e.getData().isMod())
        {
            String[] args = e.getArgs();
            if(args.length==0)
            {
                e.send("Incorrect Usage!\r"+desc());
                return;
            }
            
            String user = (args[0].startsWith("@")?args[0].substring(1):args[0]);
            DataStore store = e.getCallAsStore();
            if(Arrays.asList(Conf.TRIPS).contains(user))
            {
                e.reply(" LOL!");
            }
            else if(store.containsKey(user))
            {
                e.send("Sorry @"+e.getNick()+"! No rebans!");
            }
            else
            {
                e.send(user+" is banned from using "+Conf.NAME);
                store.store(user,"ban");
            }
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
