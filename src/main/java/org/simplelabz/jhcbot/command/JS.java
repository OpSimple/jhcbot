package org.simplelabz.jhcbot.command;

import delight.rhinosandox.RhinoSandbox;
import delight.rhinosandox.RhinoSandboxes;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;
import org.simplelabz.jhcbot.command.svc.JSServices;

/**
 *
 * @author simple
 */
public class JS implements Command
{
    private static String[] CENSORED = {
        "org.simplelabz.jhcbot", 
    };
    
    private String text = null;
    private CommandEvent ev = null;
    
    @Override
    public String name()
    {
        return "JavaScript";
    }

    @Override
    public String call()
    {
        return "js";
    }

    @Override
    public String desc()
    {
        return "Evaluate JavaScript expressions Usage: "+Conf.TRIG+call()+" <script expression>";
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
        sandbox.setUseSealedScope(true);
        if(ev.isAdmin())sandbox.setUseSafeStandardObjects(true);
        sandbox.inject("bot", JSServices.getJSServices(ev.getChat()));
        
        try
        {
            Object obj = sandbox.eval("botjs", args);
            if(obj!=null && !(obj instanceof org.mozilla.javascript.Undefined))ev.send(String.valueOf(obj));
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
