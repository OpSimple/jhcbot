package org.simplelabz.jhcbot.command;

import java.util.Properties;
import java.util.regex.Pattern;
import org.simplelabz.jhcbot.Chat.ParsedData;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;
import org.simplelabz.jhcbot.store.DataStore;

/**
 * I'll bring this command in the next release
 * @author simple
 */
public class TALK implements Command
{
    private static Properties values = new Properties();
    static
    {
        values.setProperty("BOTNAME", Conf.NAME);
    }
    
    @Override
    public String name()
    {
        return "Talk";
    }

    @Override
    public String call()
    {
        return "talk";
    }

    @Override
    public String desc()
    {
        return "The most simple communication dialogues for "+Conf.NAME+"\rUsage: "+Conf.TRIG+call()
                +" regular expression >> and the reply for it here\r";
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
        if(ev.isAdmin())
        {
            String args = ev.getArgsStr();
            if(args==null || args.trim().isEmpty() || !args.contains(">>"))
            {
                ev.send("Incorrect Usage!\r"+desc());
                return;
            }
            
            String regex = args.substring(0, args.indexOf(">>")).trim();
            String reply = args.substring(args.indexOf(">>")+2, args.length()).trim();
            if(regex==null || regex.trim().isEmpty() || reply==null || reply.trim().isEmpty())
            {
                ev.send("Incorrect Usage!\r"+desc());
                return;
            }
            DataStore store = DataStore.getDataStore("dialogues", "all");
            store.store(regex, reply);
            ev.reply("Dialogue added!");
        }
    }
    
    private static void collectValues(CommandEvent ev)
    {
        if(!values.containsKey("USER"))
        {
            values.setProperty("USER", ev.getNick());
            values.setProperty("TRIP", ev.getTrip());
            return;
        }
        values.replace("USER", ev.getNick());
    }
    
    @Override
    public void listen(CommandEvent ev)
    {
        Thread talk = new Thread(new Runnable()
        {
            public void run()
            {
                if(ev.getData().getCallType()==ParsedData.CHAT && ev.getData().getText().contains(Conf.NAME))
                {
                    String text = ev.getData().getText();
                    DataStore store = DataStore.getDataStore("dialogues", "all");
                    collectValues(ev);
                    for(String regex:store.getKeys())
                    {

                        boolean find = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(text).find();
                        if(find)
                        {
                            DataStore.DataEntry[] rpents = store.get(regex);
                            String reply = rpents[0].getValue();
                            ev.send(org.apache.commons.text.StrSubstitutor.replace(reply, values));
                            return;
                        }
                    }
                }
            }
        },"talk");
        talk.start();
    }
}
