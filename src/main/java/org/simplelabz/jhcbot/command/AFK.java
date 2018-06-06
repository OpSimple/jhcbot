package org.simplelabz.jhcbot.command;

import org.apache.commons.lang3.ArrayUtils;
import org.simplelabz.jhcbot.Chat.ParsedData;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;
import org.simplelabz.jhcbot.store.DataStore;


/**
 *
 * @author simple
 */
public class AFK implements Command
{
    private final int[] CALLS = {ParsedData.CHAT};
    
    @Override
    public String name()
    {
        return "Away From Keyboard";
    }

    @Override
    public String call()
    {
        return "afk";
    }

    @Override
    public String desc()
    {
        return "Use it notify that you're going away from PC/keyboard.";
    }

    @Override
    public boolean isHidden()
    {
        return false;
    }

    @Override
    public boolean listens()
    {
        return true;
    }

    @Override
    public void action(CommandEvent ev)
    {
        ev.getCallAsStore().store(ev.getNick(), "afk");
        ev.send("-> @"+ev.getNick()+" is now AFK! <-");
        ev.logInfo("@"+ev.getNick()+" is AFK!");
    }

    @Override
    public void listen(CommandEvent ev)
    {
        //Checks
        //Check for call type
        if(!ArrayUtils.contains(CALLS, ev.getData().getCallType()) || ev.getTriggeredCommand()!=null)return;
        //Check if current command was afk
        if(ev.getText().trim().startsWith(Conf.TRIG+call()))return;
        //init datastore
        DataStore store = ev.getCallAsStore();
        //first recover user from afk
        DataStore.DataEntry[] afks = store.get(ev.getNick());
        if(afks.length>0)
        {
            ev.send("Welcome Back @"+ev.getNick());
            for(DataStore.DataEntry afk: afks)
                store.delete(afk.getID());
            ev.logInfo("@"+ev.getNick()+" is Back!");
        }
        //check if someone is calling an afk user
        for(String user:store.getKeys())
        {
            if(ev.getText().contains("@"+user))
            {
                ev.send("@"+user+" is AFK! your message recorded!");
                MSG.appendMSG(DataStore.getDataStore("msg", ev.getConfig().getChannel()),user, ev.getNick(), ev.getText(), ev.getTime());
                ev.logInfo("Message for @"+user+" queued!");
            }
        }
    }
}
