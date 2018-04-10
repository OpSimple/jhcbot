package org.simplelabz.jhcbot.command;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import org.simplelabz.jhcbot.Chat.ParsedData;
import org.simplelabz.jhcbot.command.event.CommandEvent;

/**
 *
 * @author simple
 */
public class YTB implements Command
{
    private final String    YTBURLREGEX = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
    private final String[]  VIDEOREGEX = { "\\?vi?=([^&]*)","watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)"};
    private final String    URLSREGEX = "\\b(https)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private final String    APIURL = "https://www.googleapis.com/youtube/v3/videos?part=id%2C+snippet%2C+contentDetails%2C+statistics&key=";
    
    @Override
    public String name()
    {
        return "Youtube";
    }

    @Override
    public String call()
    {
        return "ytb";
    }

    @Override
    public String desc()
    {
        return "Prints info of content on Youtube (youtube.com)   Usage: "+call()+" <url>";
    }

    @Override
    public boolean isHidden()
    {
        return true;
    }

    @Override
    public boolean listens()
    {
        return true;
    }
    
    private ArrayList<String> pullLinks(String text)
    {
        ArrayList<String> links = new ArrayList();
        Pattern p = Pattern.compile(URLSREGEX, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        while(m.find())
        {
            String urlStr = m.group();
            if(urlStr.startsWith("(") && urlStr.endsWith(")"))
            {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            links.add(urlStr);
        }
        return links;
    }
    
    private String extractVideoID(String url)
    {
        Pattern pttr = Pattern.compile(YTBURLREGEX);
        Matcher m1 = pttr.matcher(url);
        if(m1.find())
        {
            url = url.replace(m1.group(), "");
        }
        
        for(String regex : VIDEOREGEX)
        {
           Pattern cp = Pattern.compile(regex);
           Matcher m2 = cp.matcher(url);
           if(m2.find())return m2.group(1);
        }
        return null;
    }
    
    @Override
    public void action(CommandEvent ev)
    {
        
    }
    
    @Override
    public void listen(CommandEvent ev)
    {
        if(ev.getData().getCallType() != ParsedData.CHAT) return;
        if(ev.getText().contains("youtube.com") || ev.getText().contains("youtu.be"))
        {
            try
            {
                ArrayList<String> links = this.pullLinks(ev.getText());
                Iterator<String> itr = links.iterator();
                while(itr.hasNext())
                {
                    String chan, title, duration, views;
                    
                    {
                        String yurl = itr.next();
                        String id = extractVideoID(yurl);
                        String surl = APIURL+GOOGLE.GOOGLE_KEY+"&id="+id;
                        URL url = new URL(surl);
                        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
                        JsonObject json = Json.parse(new java.io.InputStreamReader(con.getInputStream())).asObject();
                        if(json.getString("error", null)!=null)
                        {
                            JsonObject err = json.get("error").asObject();
                            ev.sendError(err.get("code").asInt()+"  "+err.get("message").asString());
                            continue;
                        }
                        JsonValue items = json.get("items");
                        if(items==null || items.asArray().isEmpty())return;
                        JsonObject item = items.asArray().get(0).asObject();
                        JsonObject snippet = item.get("snippet").asObject();
                        chan = snippet.getString("channelTitle", "");
                        title = snippet.getString("title", "");
                        duration = item.get("contentDetails").asObject().getString("duration", "");
                        views = item.get("statistics").asObject().getString("viewCount", "");
                    }
                    duration = duration.replace("PT", "").replace("H", "hrs ").replace("M", "mins ").replace("S", "secs");
                    ev.send("[$\\red{\\mathsf{Youtube}}$] @"+chan+": "+title+" (Duration: "+duration+", Views: "+views+")");
                }
            }
            catch(IOException ex)
            {
                Logger.getLogger(YTB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
