package org.simplelabz.jhcbot.command;

import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;
import org.simplelabz.jhcbot.store.DataStore;


/**
 *
 * @author simple
 */
public class UNBAN implements Command
{
    @Override
    public String name()
    {
        return "Unban";
    }

    @Override
    public String call()
    {
        return "unban";
    }

    @Override
    public String desc()
    {
        return "Use it to unban a nickname or trip for using "+Conf.NAME+" Usage: "+Conf.TRIG+call()+" <nick/trip>";
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
            String ban = (args[0].startsWith("@")?args[0].substring(1):args[0]);
            if(ban.trim().isEmpty())
            {
                e.send("Incorrect Usage!\r"+desc());
                return;
            }
            DataStore store = e.getStore("ban");
            DataStore.DataEntry[] bans = store.get(ban);
            if(bans.length>0)
            {
                e.send(ban+" unbanned and may use "+Conf.NAME+"! :)");
                for(DataStore.DataEntry user: bans)
                    store.delete(user.getID());
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
