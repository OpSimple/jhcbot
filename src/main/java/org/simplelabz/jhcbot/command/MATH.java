package org.simplelabz.jhcbot.command;

import delight.rhinosandox.RhinoSandbox;
import delight.rhinosandox.RhinoSandboxes;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;

/**
 *
 * @author simple
 */
public class MATH implements Command
{
    @Override
    public String name()
    {
        return "Math";
    }

    @Override
    public String call()
    {
        return "math";
    }

    @Override
    public String desc()
    {
        return "Solve simple math expressions Usage: "+Conf.TRIG+call()+" <math expression>";
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
    public void action(CommandEvent ev)
    {
        String args = ev.getArgsStr();
        if(args==null)return;
        RhinoSandbox sandbox = RhinoSandboxes.create();
        sandbox.setInstructionLimit(10);
        sandbox.setMaxDuration(2000);
        
        try
        {
            Object obj = sandbox.eval("botjs", args);
            if(obj!=null && (obj instanceof Number))ev.send(String.valueOf(obj));
        }
        catch(Exception ex)
        {
            String msg = ex.getMessage();
            if(msg!=null)ev.sendError(msg);
        }
    }
    
    @Override
    public void listen(CommandEvent e)
    {
        //nothing to do here
    }
}
