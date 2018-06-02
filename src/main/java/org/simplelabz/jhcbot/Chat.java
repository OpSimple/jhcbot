package org.simplelabz.jhcbot;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.neovisionaries.ws.client.HostnameUnverifiedException;
import com.neovisionaries.ws.client.OpeningHandshakeException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketCloseCode;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates a connection to any hack.chat server and manages all chat and other stuffs
 * @author simple
 */
public class Chat extends WebSocketAdapter
{
    private final String     URL     ;
    private final String     CHANNEL ;
    private final String     NICK    ;
    private final String     PASSWORD;
    
    private ParseListener listener;
    
    private WebSocket ws;
    private Parser parser;
    private Timer pingtimer;
    
    /**
     * Create a new Chat to any hack.chat server.
     * @param url the URL of the server
     * @param channel the channel to join
     * @param nick the nickname to join with
     * @param password a password to get a trip on the server (optional)
     */
    public Chat(String url, String channel, String nick, String password)
    {
        if(!Utils.verifyURI(url))
            throw new IllegalArgumentException("The URL doesn't seems to be a valid one!");
        if(!Utils.verifyNick(nick))
            throw new IllegalArgumentException("The nick doesn't seems to be a valid one!");
        
        URL = url;
        CHANNEL = channel;
        NICK = nick;
        PASSWORD = password;

        parser = new Parser();
        ws = null;
        pingtimer = null;
        listener = null;
    }
    
    /**
     * Sends ping at every 50s
     */
    private void startPing()
    {
        if(pingtimer!=null)return;

        TimerTask pingtask = new TimerTask()
        {
            @Override
            public void run()
            {
                ping();
            }
        };
        pingtimer = new Timer();
        pingtimer.schedule(pingtask, 0, 50000);
    }
    
    /**
     * Make a connection to the server
     */
    public void connect()
    {
        if(ws!=null && ws.isOpen())
        {
            disconnect();
            ws = null;
        }

        Logger.getLogger(Chat.class.getName()).log(Level.FINE, "@"+CHANNEL+">> "+"Preparing to connect the server [{0}]",URL);
        try
        {
            ws = new WebSocketFactory()
               .setVerifyHostname(false)
               .createSocket(URL)
               .setAutoFlush(true)
               .addExtension(WebSocketExtension.PERMESSAGE_DEFLATE)
               .addListener(this)
               .connect();
            
            startPing();
            join();
        }
        catch (OpeningHandshakeException ex)
        {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, "@"+CHANNEL+">> "+"Error trying to open handshake with the server!", ex);
        }
        catch (HostnameUnverifiedException ex)
        {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, "@"+CHANNEL+">> "+"Hostname of the server not verified with its owner!", ex);
        }
        catch(WebSocketException ex)
        {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, "@"+CHANNEL+">> "+"Error trying to create a websocket connection with the server!", ex);
        }
        catch(IOException ex)
        {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, "@"+CHANNEL+">> ", ex);
        }
    }
    
    /**
     * Disconnect from the server
     */
    public void disconnect()
    {
        if(ws==null)return;
        Logger.getLogger(Chat.class.getName()).log(Level.INFO, "@"+CHANNEL+">> "+"Disconnecting.. [{0}]",URL);
        pingtimer.cancel();
        pingtimer = null;
        ws.flush();
        ws.disconnect(WebSocketCloseCode.NORMAL);
    }
    
    /**
     * Set a {@link org.simplelabz.jhcbot.Chat.ParseListener} to receive all parsed
     * data from the server as {@link org.simplelabz.jhcbot.Chat.ParsedData}
     * @param listener 
     * @see org.simplelabz.jhcbot.Chat.ParseListener
     * @see org.simplelabz.jhcbot.Chat.ParsedData
     */
    public void setParseListener(ParseListener listener)
    {
        this.listener = listener;
    }
    
    /**
     * Add an Observer (if needed) for getting messages from websocket connection.
     * @param o Observer
     * @see java.util.Observer
     */
    public void addParsedObserver(Observer o)
    {
        parser.addObserver(o);
    }
    
    /**
     * Returns if we are connected to the server
     * @return <code>true</code> if connected
     */
    public boolean isConnected()
    {
        if(ws==null)return false;
        return ws.isOpen();
    }
    
    /**
     * Send a raw String to the server
     * @param str String to be sent
     */
    public void sendString(String str)
    {
        if(ws==null)return;
        Logger.getLogger(Chat.class .getClass().getName()).log(Level.FINE, "@"+CHANNEL+">> "+" -> {0}", str);
        ws.sendText(str);
    }
    
    /**
     * Send some raw data to the server
     * @param data bytes of data to be sent
     */
    public void sendData(byte[] data)
    {
        if(ws==null)return;
        ws.sendBinary(data);
    }
    
    /**
     * Get the channel
     * @return channel
     */
    public String getChannel()
    {
        return CHANNEL;
    }
    
    /**
     * Get the URL
     * @return the URL
     */
    public String getURL()
    {
        return URL;
    }
    
    /**
     * Get the nick
     * @return nick
     */
    public String getNick()
    {
        return NICK;
    }
    
    /**
     * Get the password
     * @return the password
     */
    public String getPassword()
    {
        return PASSWORD;
    }
    
    /**
     * Get the {@link com.neovisionaries.ws.client.WebSocket} for the connection
     * @return WebSocket for the connection
     */
    public WebSocket getWebSocket()
    {
        return ws;
    }
    
    /*
    All hack.chat commands below
    */
    
    /**
     * Send: <code>{ cmd: 'ping' }</code>
     */
    public void ping()
    {
        String ping = Json.object().add("cmd", "ping").toString();
        sendString(ping);
    }

    /**
     * Send: <code>{ cmd: 'join', channel: 'CHANNEL', nick: 'NAME#PASSWORD' }</code>
     */
    public void join()
    {
        String join = Json.object()
            .add("cmd", "join")
            .add("channel", CHANNEL)
            .add("nick", ((PASSWORD!=null && !PASSWORD.trim().isEmpty())?NICK+"#"+PASSWORD:NICK)).toString();
        sendString(join);
        Logger.getLogger(Chat.class .getClass().getName()).log(Level.FINER, "Joined the channel {0} !", CHANNEL);
    }
    
    /**
     * Send: <code>{ cmd: 'join', channel: 'CHANNEL', nick: 'NAME#PASSWORD' }</code>
     * @param channel a channel name to join to
     * @param nick a nickname
     * @param pass an optional password for trip gen
     * @return json string to be sent
     */
    public static String join(String channel, String nick, String pass)
    {
        if(!Utils.verifyNick(nick))throw new RuntimeException("Nickname invalid!");
        String join = Json.object()
            .add("cmd", "join")
            .add("channel", channel)
            .add("nick", ((pass!=null && !pass.trim().isEmpty())?nick+"#"+pass:nick)).toString();
        return join;
    }
    
    /**
     * Send: <code>{ cmd: 'chat', text: 'text' }</code>
     * @param text some text to send
     */
    public void send(String text)
    {
        if(text.length()>=Conf.MAX_TEXT_LENGTH)
        {
            text = text.substring(0, text.length()-5)+"...";
        }
        String chat = Json.object().add("cmd", "chat").add("text", text).toString();
        sendString(chat);
    }

    /**
     * Send: <code>{ cmd: 'invite', nick: 'nick' }</code>
     * @param nick a nick to invite
     */
    public void invite(String nick)
    {
        String invite = Json.object().add("cmd", "invite").add("nick", nick).toString();
        sendString(invite);
    }

    /**
     * Send: <code>{ cmd: 'stats' }</code>
     */
    public void stats()
    {
        String stats = Json.object().add("cmd", "stats").toString();
        sendString(stats);
    }

    /**
     * Send: <code>{ cmd: 'ban', nick: 'nick' }</code>
     * @param nick a nick to ban
     */
    public void ban(String nick)
    {
        String ban = Json.object().add("cmd", "ban").add("nick", nick).toString();
        sendString(ban);
    }
    
    /**
     * Send: <code>{ cmd: 'kick', nick: 'nick' }</code>
     * @param nick a nick to kick
     */
    public void kick(String nick)
    {
        String kick = Json.object().add("cmd", "kick").add("nick", nick).toString();
        sendString(kick);
    }

    /**
     * Send: <code>{ cmd: 'unban', nick: 'nick' }</code>
     * @param nick a nick to unban
     */
    public void unban(String nick)
    {
        String unban = Json.object().add("cmd", "unban").add("nick", nick).toString();
        sendString(unban);
    }

    /**
     * Send: <code>{ cmd: 'listUsers' }</code>
     */
    public void lsusers()
    {
        String lsusers = Json.object().add("cmd", "listUsers").toString();
        sendString(lsusers);
    }

    /**
     * Send: <code>{ cmd: 'broadcast', text: 'text' }</code>
     * @param text some text to broadcast
     */
    public void broadcast(String text)
    {
        String broadcast = Json.object().add("cmd", "broadcast").add("text", text).toString();
        sendString(broadcast);
    }
    
    /*
    WebSocket handlers below
    */
    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers)
    {
        Utils.showInfo("@"+CHANNEL+">> "+"Connected "+ws.getURI().getHost()+"/?"+CHANNEL+" !");
        Logger.getLogger(Chat.class .getClass().getName()).log(Level.FINE, "@{0}>> Connected!", CHANNEL);
    }

    @Override
    public void onTextMessage(WebSocket ws, String msg)
    {
        //Logger.getLogger(Chat.class .getClass().getName()).log(Level.INFO, "<- {0}", msg);
        if(parser!=null && listener!=null)
        {
            parser.parse(msg, listener);
            parser.notifyObservers(msg);
        }
    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer)
    {
        Utils.logBotInfo(CHANNEL, "Disconnected!");
        Logger.getLogger(Chat.class .getClass().getName()).log(Level.WARNING,
            "@"+CHANNEL+">> "+"Disconnected! ClientFrame:{0}[{1}]  ServerFrame:{2}[{3}]",
            new Object[]{clientCloseFrame.getCloseReason(), clientCloseFrame.getCloseCode(),
                serverCloseFrame.getCloseReason(), serverCloseFrame.getCloseCode()});

        if(!closedByServer && clientCloseFrame.getCloseCode()!=WebSocketCloseCode.NORMAL)
        {
            Utils.logBotInfo(CHANNEL, "Attempting to reconnect...");
            try
            {
                ws = websocket.recreate().connect();
                join();
            }
            catch(WebSocketException | IOException e)
            {
                Logger.getLogger(Chat.class .getClass().getName()).log(Level.FINE, "@"+CHANNEL+">> ",e);
                Utils.logBotErr(CHANNEL, "Error while trying to reconnect the server.\nReason:"+e.toString()+"\n"+e.getMessage());
            }
        }
        else
             ws = null;
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException cause)
    {
        Utils.logBotErr(CHANNEL, cause.toString()+"\n"+cause.getMessage());
        Logger.getLogger(Chat.class .getClass().getName()).log(Level.SEVERE, "@{0}>> {1}", new Object[]{CHANNEL, cause.toString()});
    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException ex)
    {
        Utils.logBotErr(CHANNEL, "Error while trying to reconnect the server.\nReason:"+ex.toString()+"\n"+ex.getMessage());
        Logger.getLogger(Chat.class .getClass().getName()).log(Level.SEVERE, "@{0}>> {1}", new Object[]{CHANNEL, ex.toString()});
        disconnect();
    }

    @Override
    public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame)
    {
        Utils.logBotErr(CHANNEL, "Error while trying to send texts to the server.\nReason:"+cause.toString());
        Logger.getLogger(Chat.class .getClass().getName()).log(Level.SEVERE, "@{0}>> {1}", new Object[]{CHANNEL, cause.toString()});
        disconnect();
    }

    @Override
    public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames)
    {
        Utils.logBotErr(CHANNEL, "Error while trying to recieve texts from the server.\nReason:"+cause.toString());
        Logger.getLogger(Chat.class .getClass().getName()).log(Level.SEVERE, "@{0}>> {1}", new Object[]{CHANNEL, cause.toString()});
    }
    
    /*
    A Parser for parsing hack.chat messages
    */
    private class Parser extends Observable
    {
        protected void parse(String msg, ParseListener listner)
        {
            JsonObject json = Json.parse(msg).asObject();
            ParsedData ev = null;
            long time = System.currentTimeMillis();
            
            //Checks against server bugs 1
            if(msg==null || msg.trim().isEmpty())return;
            
            String cmd = json.getString("cmd", "error");
            switch(cmd)
            {
                case "chat":
                    //Checks against bugs 2
                    if(json.getString("text", null)==null)return;
                    
                    ev = new ParsedData(ParsedData.CHAT,
                            json.getString("nick", null),
                            json.getString("trip", null),
                            json.getBoolean("admin", false),
                            json.getBoolean("mod", false),
                            json.getString("text", null),
                            null,
                            json.getLong("time", time)
                    );
                    break;
                case "onlineSet":
                    ev = new ParsedData(ParsedData.ONLINE_SET,
                            null,null,false,false,null,
                            Json.object().add("nicks", json.get("nicks")).toString(),
                            json.getLong("time", time)
                    );
                    break;
                case "onlineAdd":
                    ev = new ParsedData(ParsedData.ONLINE_ADD,
                            json.getString("nick", null),null,false,false,null,null,
                            json.getLong("time", time)
                    );
                    break;
                case "onlineRemove":
                    ev = new ParsedData(ParsedData.ONLINE_REMOVE,
                            json.getString("nick", null),null,false,false,null,null,
                            json.getLong("time", time)
                    );
                    break;
                case "info":
                    String itext = json.getString("text", null);
                    String[] parts = itext.split(" ");
                    if(itext.contains("You invited"))
                    {
                        ev = new ParsedData(ParsedData.INVITE,
                                parts[2],null,false,false,itext,Json.object().add("channel", parts[4].substring(1)).toString(),
                                json.getLong("time", time));
                    }
                    else if(itext.contains("invited you"))
                    {
                        ev = new ParsedData(ParsedData.INVITED,
                                parts[0],null,false,false,itext,Json.object().add("channel", parts[4].substring(1)).toString(),
                                json.getLong("time", time));
                    }
                    else if(itext.contains("IPs"))
                    {
                        ev = new ParsedData(ParsedData.STATS,
                                null,null,false,false,itext,
                                Json.object().add("IPs", parts[0]).add("channels", parts[4]).toString(),
                                json.getLong("time", time)
                        );
                    }
                    else if(itext.contains("Banned"))
                    {
                        ev = new ParsedData(ParsedData.BANNED,
                                parts[1],null,false,false,itext,null,
                                json.getLong("time", time)
                        );
                    }
                    else if(itext.contains("Unbanned"))
                    {
                        ev = new ParsedData(ParsedData.UNBANNED,
                                parts[1],null,false,false,itext,null,
                                json.getLong("time", time)
                        );
                    }
                    else if(itext.contains("Server broadcast:"))
                    {
                        ev = new ParsedData(ParsedData.BROADCAST,
                                null,null,false,false,itext,null,
                                json.getLong("time", time)
                        );
                    }
                    else
                    {
                        ev = new ParsedData(ParsedData.INFO,
                                null,null,false,false,itext,null,
                                json.getLong("time", time)
                        );
                    }
                    break;
                case "warn":
                    String wtext = json.getString("text", null);
                    if(wtext.contains("rate-limited or blocked"))
                    {
                        ev = new ParsedData(ParsedData.WARN_RATE_LTD,
                                null,null,false,false,wtext,null,
                                json.getLong("time", time)
                        );
                    }
                    else
                    {
                        ev = new ParsedData(ParsedData.WARN,
                                null,null,false,false,
                                json.getString("text", null),null,
                                json.getLong("time", time)
                        );
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Error: Unidentified message found while parsing! \n"+msg);
            }
            
            if(ev!=null)
            {
                listener.afterParsing(ev);
            }
        }
    }
    
    /**
     * A listener to get all parsed data 
     */
    public interface ParseListener
    {
        /**
         * It is called just after the messages are parsed
         * @param ev ParsedData
         */
        void afterParsing(ParsedData ev);
    }
    
    /**
     * It stores all the parsed data
     */
    public class ParsedData
    {
        public static final int CHAT            = 0;
        public static final int ONLINE_SET      = 1;
        public static final int ONLINE_ADD      = 2;
        public static final int ONLINE_REMOVE   = 3;
        public static final int INFO            = 4;
        public static final int WARN            = 5;
        public static final int BANNED          = 6;
        public static final int UNBANNED        = 7;
        public static final int STATS           = 8;
        public static final int INVITED         = 9;
        public static final int INVITE          = 10;
        public static final int BROADCAST       = 11;
        public static final int WARN_RATE_LTD   = 12;
        
        private final int type;
        private final String nick;
        private final String trip;
        private final String text;
        private final boolean isAdmin;
        private final boolean isMod;
        private final long time;
        private final String datJson;
        
        public ParsedData(int type, String nick, String trip, boolean isAdmin,
                boolean isMod, String text, String jsonData, long time)
        {
            this.type = type;
            this.nick = nick;
            this.trip = trip;
            this.isAdmin = isAdmin;
            this.isMod = isMod;
            this.text = text;
            this.time = time;
            this.datJson = jsonData;
        }
        
        /**
         * Returns the type of call/message received from the server
         * @return the type of call/message
         */
        public int getCallType()
        {
            return type;
        }
        /**
         * Returns the nick
         * @return the nick
         */
        public String getNick()
        {
            return nick;
        }
        /**
         * Returns the trip
         * @return the trip
         */
        public String getTrip()
        {
            return trip;
        }
        /**
         * Returns the text
         * @return the text
         */
        public String getText()
        {
            return text;
        }
        /**
         * Returns if it is admin
         * @return true if it is admin
         */
        public boolean isAdmin()
        {
            return isAdmin;
        }
        /**
         * Returns if it is a mod
         * @return true if it is a mod
         */
        public boolean isMod()
        {
            return isMod;
        }
        /**
         * Extra Data
         * @return JsonObject of extra data
         */
        public JsonObject getExtDataAsJson()
        {
            return Json.parse(this.datJson).asObject();
        }
        /**
         * Returns time in milliseconds as received form the server
         * @return time in milliseconds
         */
        public long getTime()
        {
            return time;
        }
    }
}
