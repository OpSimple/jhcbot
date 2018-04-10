package org.simplelabz.jhcbot;

import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * The main class for the Java hack.chat bot.
 * @author simple
 */
public class JHCBot
{
    
    /**
     * We'll start our story from here, i mean the bot.
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // Just a general way to parse the arguments
        Options opts = new Options();
        opts.addOption("f", "config"       , true    , "Config file for the bot");
        opts.addOption("h", "help"         , false   , "Show Help text");
        
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(opts, args);
        } catch (ParseException ex) {
            System.out.println("[ERROR] Unable to parse inputs!");
            Logger.getLogger(JHCBot.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        //Help formatter creates a help for our program
        if(cmd.hasOption("h"))
        {
            HelpFormatter helpfmt = new HelpFormatter();
            helpfmt.setLeftPadding(5);
            helpfmt.setDescPadding(10);
            helpfmt.setOptionComparator(new Comparator<Option>()
            {
                @Override
                public int compare(Option o1, Option o2)
                {
                    return (("h".equals(o1.getOpt()))?1:o1.getOpt().compareToIgnoreCase(o2.getOpt()));
                }
            });
            helpfmt.printHelp("java -jar jhcbot.jar", "A minimal bot for hack.chat in java", opts, "\nJHCBot v"+Conf.VERSION, true);
            return;
        }
        String file = cmd.getOptionValue("f");
        if(file!=null && !file.trim().isEmpty())
        {
            Conf.CONFIG = file;
            Conf.updateConf();
        }
        
        if(Conf.isConfigAcceptable())
        {
            joinBootChannels();
        }
        else
        {
            System.exit(0);
        }
    }
    
    public static void joinBootChannels()
    {
        try
        {
            for(String chan: Conf.CHANNELS)
            {
                BotsManager.createNewBot(chan, Conf.BANNER);
            }
        }
        catch (BotsManager.BotNotCreatedException ex)
        {
            Logger.getLogger(JHCBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
