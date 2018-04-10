package org.simplelabz.jhcbot.command;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.simplelabz.jhcbot.BotsManager;
import org.simplelabz.jhcbot.Chat.ParsedData;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;


/**
 *
 * @author simple
 */
public class INVITE implements Command
{
    @Override
    public String name()
    {
        return "Invite";
    }

    @Override
    public String call()
    {
        return "invite";
    }

    @Override
    public String desc()
    {
        return "Invite "+Conf.NAME+" to another channel. Usage: "+Conf.TRIG+call()+" <channel>";
    }

    @Override
    public boolean isHidden()
    {
        return false;
    }

    @Override
    public void action(CommandEvent ev)
    {
        String[] args = ev.getArgs();
        if(args.length==0)
        {
            ev.send("Incorrect Usage!\r"+desc());
            return;
        }
        String chan = (args[0].startsWith("?"))?args[0].substring(1):args[0];
        chan = (chan.startsWith("?"))?chan.substring(1):chan;
        if(chan!=null)
        {
            try {
                BotsManager.createNewBot(chan, ev.getNick(), ev.getTrip());
                ev.reply(Conf.NAME+" has joined ?"+chan);
            } catch (BotsManager.BotNotCreatedException ex) {
                ev.sendError("BotNotCreatedException: "+ex.getMessage());
            }
        }
        else
        {
            ev.reply("Invalid arguments!\r"+desc());
        }
    }

    @Override
    public boolean listens()
    {
        return true;
    }
    @Override
    public void listen(CommandEvent ev)
    {
        if(ev.getData().getCallType()==ParsedData.INVITED)
        {
            String chan = ev.getData().getExtDataAsJson().getString("channel", null);
            if(chan!=null)
            {
                try {
                    BotsManager.createNewBot(chan, ev.getNick(), ev.getTrip());
                    ev.reply("Invite Accepted!");
                } catch (BotsManager.BotNotCreatedException ex) {
                    Logger.getLogger(INVITE.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
