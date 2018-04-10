package org.simplelabz.jhcbot.command;

import bsh.EvalError;
import bsh.Interpreter;
import java.io.PrintStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.Utils;
import org.simplelabz.jhcbot.command.event.CommandEvent;
import org.simplelabz.jhcbot.command.io.BotOutputSctream;


/**
 *
 * @author simple
 */
public class BSH implements Command,Runnable
{
    private String text = null;
    private CommandEvent ev = null;
    
    @Override
    public String name()
    {
        return "BeanShell";
    }

    @Override
    public String call()
    {
        return "bsh";
    }

    @Override
    public String desc()
    {
        return "Evaluate Beanshell commands. (beanshell.org) Usage: "+Conf.TRIG+call()+" <commands>";
    }

    @Override
    public boolean isHidden()
    {
        return false;
    }
    
    @Override
    public void action(CommandEvent ev)
    {
        String args = ev.getArgsStr();
        if(args==null)return;
        
        if(!ev.isAdmin())//containsCensoredTexts(args) && !ev.isAdmin())
        {
            ev.replyPermissionDenied();
            return;
        }
        //init
        text = args;
        this.ev = ev;
        
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future<?> future = exec.submit(this);
        
        try
        {
            exec.shutdown();
            
            future.get(1, TimeUnit.SECONDS);
            
            if(!exec.awaitTermination(1, TimeUnit.SECONDS))
                exec.shutdownNow();
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            Logger.getLogger(BSH.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run()
    {
        Interpreter i = Utils.newBshInterpreter();
        i.setOut(new PrintStream(new BotOutputSctream(ev.getChat())));
        try
        {
            if(text.contains("clear();"))
            {
                i = Utils.newBshInterpreter();
                System.gc();
                ev.send("Beanshell interpreter recreated!");
                return;
            }
            Object obj = i.eval(text);
            if(obj!=null)ev.send(obj.toString());
        }
        catch(EvalError ex)
        {
            ev.sendError(ex.getErrorText()+": "+ex.getMessage());
        }
        catch(Exception ex)
        {
            Logger.getLogger(BSH.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.gc();
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
