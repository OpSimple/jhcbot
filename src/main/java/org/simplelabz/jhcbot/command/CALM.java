package org.simplelabz.jhcbot.command;

import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.Utils;
import org.simplelabz.jhcbot.command.event.CommandEvent;


/**
 *
 * @author simple
 */
public class CALM implements Command
{
    @Override
    public String name()
    {
        return "Calm";
    }

    @Override
    public String call()
    {
        return "calm";
    }

    @Override
    public String desc()
    {
        return "Calm down "+Conf.NAME+" memory.";
    }

    @Override
    public boolean isHidden()
    {
        return false;
    }

    private String getMem()
    {
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long used = total-free;
        return Utils.hum(total,false)+"  "+Utils.hum(free,false)+"  "+Utils.hum(used,false);
    }

    @Override
    public void action(CommandEvent e)
    {
        String send = "\r        Total     Free      Used\r";
        send = send + "Before  "+getMem()+"\r";
        System.gc();
        System.gc();
        send = send + "After   "+getMem()+"\r"
                + "\rGARBAGE COLLECTED!";
        e.send(send);
        e.logInfo(e.getNick()+" called gc() !");
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
