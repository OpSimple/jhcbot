package org.simplelabz.jhcbot.command;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.command.event.CommandEvent;


/**
 *
 * @author simple
 */
public class IPINFO implements Command
{
    @Override
    public String name()
    {
        return "Ip Info";
    }

    @Override
    public String call()
    {
        return "ipinfo";
    }

    @Override
    public String desc()
    {
        return "Get info for an ip or host Usage: "+Conf.TRIG+call()+" <ip/hostname>";
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
    
    private String recurseJson(JsonObject json, String send)
    {
        Iterator<JsonObject.Member> itr = json.iterator();
        while(itr.hasNext())
        {
            JsonObject.Member mem = itr.next();
            JsonValue value = mem.getValue();
            send = send+ mem.getName()+": "+((value.isString())?value.asString():value.toString())+"\r";
        }
        return send;
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
        String send = "\r";
        try
        {
            InetAddress ipaddr = InetAddress.getByName(args[0]);
            URL url = new URL("http://ipinfo.io/"+ipaddr.getHostAddress());
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "curl");
            
            JsonObject json = Json.parse(new java.io.InputStreamReader(con.getInputStream())).asObject();
            
            if(json.get("error")!=null)
            {
                JsonObject error = json.get("error").asObject();
                send = send+"[ERROR] "+error.getString("title", "Error")+": "+error.getString("message"
                        , "Error while trying to get details for "+args[0]);
            }
            else
            {
                send = recurseJson(json, send);
            }
            e.send(send);
        }
        catch(UnknownHostException ex)
        {
            e.sendError(args[0]+" is not a valid hostname!");
        }
        catch (MalformedURLException ex)
        {
            Logger.getLogger(IPINFO.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(IPINFO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void listen(CommandEvent e)
    {
        //nothing to do here
    }
}
