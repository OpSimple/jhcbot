/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simplelabz.jhcbot.command;

import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.Utils;
import org.simplelabz.jhcbot.command.event.CommandEvent;
import org.simplelabz.jhcbot.store.DataStore;

/**
 *
 * @author simple
 */
public class AMSG implements Command
{
    @Override
    public String name()
    {
        return "Message Anywhere";
    }

    @Override
    public String call()
    {
        return "amsg";
    }

    @Override
    public String desc()
    {
        return "Message someone on any channel. Usage: "+Conf.TRIG+call()+" <channel> <nickname> <message>";
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
        if(args.length<2)
        {
            e.send("Incorrect Usage!\r"+desc());
            return;
        }
        String channel = (args[0].startsWith("?"))?args[0].substring(1):args[0];
        String nickname = args[1].startsWith("@")?args[1].substring(1):args[1];
        if(Utils.verifyNick(nickname) && channel!=null && !channel.trim().isEmpty())
        {
            String message = e.getArgsStr().substring(e.getArgsStr().indexOf(nickname)+nickname.length()+1);
            e.reply("your message for @"+nickname+" queued on ?"+channel);
            MSG.appendMSG(DataStore.getDataStore("msg", channel),nickname, e.getNick(),message,e.getTime());
            e.logInfo("Message for @"+nickname+" queued!");
        }
        else
        {
            e.reply("Invalid Nickname! Nickname can only have a-z, A-Z and _ and it can only be "+Conf.MAX_NICK_LENGTH+" chars long!");
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
