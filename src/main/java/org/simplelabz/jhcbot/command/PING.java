package org.simplelabz.jhcbot.command;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;


/**
 *
 * @author simple
 */
public class PING implements Command
{
    @Override
    public String name()
    {
        return "Ping";
    }

    @Override
    public String call()
    {
        return "ping";
    }

    @Override
    public String desc()
    {
        return "Ping a host/IP  Usage: "+Conf.TRIG+call()+" <IP/Host> <timeout in mills>";
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
        String args[] = e.getArgs();
        if(args.length==0)
        {
            e.send("Incorrect Usage!\r"+desc());
            return;
        }
        String host = args[0];
        
        String send = "";
        int timeout = 2000;
        if(args.length>1)
        {
            try
            {
                timeout = Integer.parseInt(args[1]);
            }
            catch(NumberFormatException ex)
            {
                send = send+args[1]+" is not a value in millisecs\r";
            }
        }
        
        try
        {
            InetAddress ipaddr = InetAddress.getByName(host);
            long pre = System.currentTimeMillis();
            boolean reach = ipaddr.isReachable(timeout);
            long post = System.currentTimeMillis();
            send = send+"PING "+ipaddr.getHostName()+"("+ipaddr.getHostAddress()+") is ALIVE "+(post-pre)+"ms";
        } catch (UnknownHostException ex) {
            e.send("[ERROR] Given host or ip is invalid!");
        } catch (IOException ex) {
            Logger.getLogger(PING.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        e.send(send);
    }

    @Override
    public void listen(CommandEvent e)
    {
        //nothing to do here
    }
}
