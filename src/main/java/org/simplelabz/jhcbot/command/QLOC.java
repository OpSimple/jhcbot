/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simplelabz.jhcbot.command;

import com.eclipsesource.json.JsonObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;
import org.simplelabz.jhcbot.command.svc.GoogleSvc;

/**
 *
 * @author simple
 */
public class QLOC implements Command
{
    @Override
    public String name()
    {
        return "Query Location with Google maps";
    }

    @Override
    public String call()
    {
        return "qloc";
    }

    @Override
    public String desc()
    {
        return "Search a location by its address using google maps. Usage: "+Conf.TRIG+call()+" [address]";
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
        if(args==null || args.trim().isEmpty())
        {
            ev.send("Incorrect Usage!\r"+desc());
            return;
        }
        args = args.trim();
        String send = "\r";
        
        try
        {
            JsonObject loc = GoogleSvc.location(args).get(0).asObject();
            send = send+loc.getString("formatted_address", "");
            JsonObject locpoint = loc.get("geometry").asObject().get("location").asObject();
            send = send+" ("+locpoint.getDouble("lat", 0)+","+locpoint.getDouble("lng", 0)+") ";
            send = send+loc.get("types").asArray().toString();
            
            ev.send(send);
        }
        catch(GoogleSvc.GoogleSvcException ex)
        {
            Logger.getLogger(QLOC.class.getName()).log(Level.SEVERE, null, ex);
            ev.sendError(ex.getMessage());
        }
    }

    @Override
    public void listen(CommandEvent e)
    {
        //nothing to do here
    }
}
