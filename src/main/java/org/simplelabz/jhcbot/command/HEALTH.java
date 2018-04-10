package org.simplelabz.jhcbot.command;

import org.simplelabz.jhcbot.BotsManager;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.Utils;
import org.simplelabz.jhcbot.command.event.CommandEvent;

/**
 *
 * @author simple
 */
public class HEALTH implements Command
{
    @Override
    public String name()
    {
        return "Health";
    }

    @Override
    public String call()
    {
        return "health";
    }

    @Override
    public String desc()
    {
        return "Display "+Conf.NAME+"'s health info";
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
        if(e.isAdmin())
        {
            StringBuilder sb = new StringBuilder();
            Runtime rtm = Runtime.getRuntime();
            long total = rtm.totalMemory();
            long free = rtm.freeMemory();
            long max = rtm.maxMemory();
            sb.append("\rTotal Memory: ").append(Utils.hum(total, false)).append(" [").append(total).append("]");
            sb.append("\rFree  Memory: ").append(Utils.hum(free, false)).append(" [").append(free).append("]");
            sb.append("\rUsed  Memory: ").append(Utils.hum((total-free), false)).append(" [").append(total-free).append("]");
            sb.append("\rMax   Memory: ").append(Utils.hum(max, false)).append(" [").append(max).append("]");
            sb.append("\rActive Threads: ").append(Thread.activeCount());
            sb.append("\rTotal Instances:").append(BotsManager.getCount());
            sb.append("\rUptime: ").append(new java.text.SimpleDateFormat("HH:mm:ss:SSS").format(e.getBot().getUptime()));
            sb.append("\r");
            e.send(sb.toString());
        }
        else
            e.replyPermissionDenied();
    }

    @Override
    public void listen(CommandEvent e)
    {
        //nothing to do here
    }
}
