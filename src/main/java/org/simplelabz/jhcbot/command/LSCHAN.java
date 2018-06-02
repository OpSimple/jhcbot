package org.simplelabz.jhcbot.command;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simplelabz.jhcbot.Chat;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.Utils;
import org.simplelabz.jhcbot.command.event.CommandEvent;


/**
 *
 * @author simple
 */
public class LSCHAN implements Command
{
    Chat parent = null;
    String nick = null;
    String send = "";
    
    @Override
    public String name()
    {
        return "List Channel";
    }

    @Override
    public String call()
    {
        return "lschan";
    }

    @Override
    public String desc()
    {
        return "List users online on a channel Usage: "+Conf.TRIG+call()+" <channel>";
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
        String[] args = ev.getArgs();
        if(args.length==0)
        {
            ev.send("Incorrect Usage!\r"+desc());
            return;
        }
        String chan = (args[0].startsWith("?"))?args[0].substring(1):args[0];
        nick = Utils.generateRandomString(6);
        parent = ev.getChat();
        
        send = send+"Users Online on ?"+chan+" : \r";
        
        try
        {
            WebSocket ws = new WebSocketFactory()
                .setVerifyHostname(false)
                .createSocket(Conf.URI)
                .setAutoFlush(true)
                .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
                .addListener(new WebSocketAdapter()
                {
                    @Override
                    public void onTextMessage(WebSocket ws, String msg)
                    {
                        JsonObject json = Json.parse(msg).asObject();
                        if("onlineSet".equals(json.getString("cmd", "error")))
                        {
                            List<JsonValue> users = json.asObject().get("nicks").asArray().values();
                            Iterator<JsonValue> itr = users.iterator();
                            
                            //int count = 0;
                            while(itr.hasNext())
                            {
                                String user = itr.next().asString();
                                if(user.trim().equals(nick))continue;
                                send = send+user+", ";
                                /*if(count==9)
                                    send = send+"\r";
                                count++;*/
                            }
                            if((users.size()-1)==0)
                                send = send + " (empty)";
                            else
                                send = send.substring(0, send.length()-2);
                            
                            parent.send(send);
                        }
                        ws.disconnect();
                    }
                    
                    @Override
                    public void onError(WebSocket ws, WebSocketException cause)
                    {
                        Logger.getLogger(LSCHAN.class.getName()).log(Level.SEVERE, ws.getURI().toString(), cause);
                    }
                })
                .connect();
            ws.sendText(Chat.join(chan, nick, null));
        }
        catch (IOException | WebSocketException ex)
        {
            Logger.getLogger(LSCHAN.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void listen(CommandEvent event)
    {
        //nothing
    }
}
