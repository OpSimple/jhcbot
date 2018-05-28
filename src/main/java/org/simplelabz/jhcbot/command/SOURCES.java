/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simplelabz.jhcbot.command;

import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;

/**
 *
 * @author simple
 */
public class SOURCES implements Command
{
    @Override
    public String name()
    {
        return "Sources";
    }

    @Override
    public String call()
    {
        return "sources";
    }

    @Override
    public String desc()
    {
        return "Sources of "+Conf.NAME;
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
        e.send("https://github.com/OpSimple/jhcbot.git");
    }

    @Override
    public void listen(CommandEvent e)
    {
        //nothing to do here
    }
}
