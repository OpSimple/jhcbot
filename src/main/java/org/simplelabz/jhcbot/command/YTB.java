package org.simplelabz.jhcbot.command;

import com.eclipsesource.json.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.simplelabz.jhcbot.Chat.ParsedData;
import org.simplelabz.jhcbot.command.event.CommandEvent;
import org.simplelabz.jhcbot.command.svc.GoogleSvc;

/**
 *
 * @author simple
 */
public class YTB implements Command
{
    private final String    YTBURLREGEX = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
    private final String[]  VIDEOREGEX = { "\\?vi?=([^&]*)","watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)"};
    private final String    URLSREGEX = "\\b(https)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    
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
        //Do nothing
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
                        JsonObject item = GoogleSvc.getYoutubeDetails(id);
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
            catch(GoogleSvc.GoogleSvcException ex)
            {
                Logger.getLogger(YTB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
