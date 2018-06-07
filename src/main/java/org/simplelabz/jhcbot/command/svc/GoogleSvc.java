package org.simplelabz.jhcbot.command.svc;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import org.simplelabz.jhcbot.Conf;

/**
 *
 * @author simple
 */
public class GoogleSvc
{
    public static String      GOOGLE_KEY   ;
    public static String      GOOGLE_CX_ID ;
    
    static
    {
        updateGoogleKeys();
    }
    
    public static void updateGoogleKeys()
    {
        String key = Conf.conf.getProperty("GOOGLE_API_KEY");
        String cx = Conf.conf.getProperty("GOOGLE_CX_ID");
        if(key!=null && cx!=null && !key.trim().isEmpty() && !cx.trim().isEmpty())
        {
            GOOGLE_KEY = key.trim();
            GOOGLE_CX_ID = cx.trim();
        }
        else
        {
            throw new RuntimeException("Google keys empty!");
        }
    }
    
    /**
     * Do a google search
     * @param keywords the keywords to be searched
     * @return {@link com.eclipsesource.json.JsonArray} of search results
     * @throws org.simplelabz.jhcbot.command.svc.GoogleSvc.GoogleSvcException
     */
    public static JsonArray search(String keywords)throws GoogleSvcException
    {
        if(keywords==null) throw new NullPointerException("Google Search keywords null!");
        
        try
        {
            String urlstr = "https://www.googleapis.com/customsearch/v1?key="
                +GOOGLE_KEY+"&cx="+GOOGLE_CX_ID+"&q="
                    +URLEncoder.encode(keywords.trim().replaceAll(" +", "+"), "UTF-8");
            
            JsonObject json = requestURLJSON(urlstr);
            if(json.getString("error", null)!=null)
            {
                JsonObject err = json.get("error").asObject();
                throw new GoogleSvcException(err.get("code").asInt()+":  "+err.get("message").asString());
            }
            JsonValue get = json.get("items");
            if(get==null || get.asArray().isEmpty())
            {
                throw new GoogleSvcException("No results found for "+keywords+" !");
            }
            
            return get.asArray();
        }
        catch(IOException ex)
        {
            Logger.getLogger(GoogleSvc.class.getName()).log(Level.SEVERE, keywords, ex);
            throw new GoogleSvcException("Unable to process your request!");
        }
    }
    
    /**
     * Search google maps for an address
     * @param address an address to search
     * @return {@link com.eclipsesource.json.JsonArray} of search results 
     * @throws org.simplelabz.jhcbot.command.svc.GoogleSvc.GoogleSvcException 
     */
    public static JsonArray location(String address)throws GoogleSvcException
    {
        if(address==null) throw new NullPointerException("Google maps address null!");
        
        try
        {
            String urlstr = "https://maps.googleapis.com/maps/api/geocode/json?key="
                    +GOOGLE_KEY+"&address="
                    +URLEncoder.encode(address.trim().replaceAll(" +", address), "UTF-8");
            
            JsonObject json = requestURLJSON(urlstr);
            
            if(isQueryStatusOK(json))
            {
                JsonValue get = json.get("results");
                if(get==null || get.asArray().isEmpty())
                {
                    throw new GoogleSvcException("No results found for "+address+" !");
                }
                return get.asArray();
            }
        }
        catch(IOException ex)
        {
            Logger.getLogger(GoogleSvc.class.getName()).log(Level.SEVERE, address, ex);
            throw new GoogleSvcException("Unable to process your request!");
        }
        return null;
    }
    
    /**
     * Selects the top most search result for a searched address
     * @param address an address to search
     * @return {@link org.simplelabz.jhcbot.command.svc.GoogleSvc.Location}
     * @throws org.simplelabz.jhcbot.command.svc.GoogleSvc.GoogleSvcException 
     */
    public static Location approxLocation(String address) throws GoogleSvcException
    {
        JsonArray locs = location(address);
        JsonObject loc = locs.get(0).asObject().get("geometry").asObject().get("location").asObject();
        return new Location(loc.getDouble("lat", 0),loc.getDouble("lng", 0));
    }
    
    /**
     * Gets the Time Zone for a given address
     * @param address an address to search
     * @param timestamp a timestamp
     * @return timezone
     * @throws org.simplelabz.jhcbot.command.svc.GoogleSvc.GoogleSvcException 
     */
    public static String getTimezoneByLocation(String address, long timestamp) throws GoogleSvcException
    {
        Location loc = approxLocation(address);
        try
        {
            String urlstr = "https://maps.googleapis.com/maps/api/timezone/json?key="+GOOGLE_KEY
                    +"&timestamp="+TimeUnit.MILLISECONDS.toSeconds(timestamp)
                    +"&location="+loc.latitude()+","+loc.longitude();
            JsonObject json = requestURLJSON(urlstr);
            if(isQueryStatusOK(json)) return json.getString("timeZoneId", "UTC");
        }
        catch(IOException ex)
        {
            Logger.getLogger(GoogleSvc.class.getName()).log(Level.SEVERE, address, ex);
            throw new GoogleSvcException("Unable to process your request!");
        }
        return null;
    }
    
    /**
     * Get details of a youtube video through its ID
     * @param VID youtube video ID
     * @return Details of the youtube video
     * @throws org.simplelabz.jhcbot.command.svc.GoogleSvc.GoogleSvcException 
     */
    public static JsonObject getYoutubeDetails(String VID) throws GoogleSvcException
    {
        if(VID==null) throw new NullPointerException("Youtube Video ID is null!");
        
        try
        {
            String urlstr = "https://www.googleapis.com/youtube/v3/videos?part=id%2C+snippet%2C+contentDetails%2C+statistics"
                    + "&key="+GOOGLE_KEY
                    + "&id="+VID;
            JsonObject json = requestURLJSON(urlstr);
            if(json.getString("error", null)!=null)
            {
                JsonObject err = json.get("error").asObject();
                throw new GoogleSvcException(err.get("code").asInt()+":  "+err.get("message").asString());
            }
            
            JsonValue get = json.get("items");
            if(get==null || get.asArray().isEmpty())
            {
                throw new GoogleSvcException("No results found for video ID "+VID+" !");
            }
            
            return get.asArray().get(0).asObject();
        }
        catch(IOException ex)
        {
            Logger.getLogger(GoogleSvc.class.getName()).log(Level.SEVERE, VID, ex);
            throw new GoogleSvcException("Unable to process your request!");
        }
    }
    
    private static JsonObject requestURLJSON(String urlstr) throws IOException
    {
        URL url = new URL(urlstr);
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
        return Json.parse(new java.io.InputStreamReader(con.getInputStream())).asObject();
    }
    
    private static boolean isQueryStatusOK(JsonObject json) throws GoogleSvcException
    {
        if(!"OK".equals(json.getString("status", "ERROR")))
        {
            String reason;
            switch(json.getString("status", "ERROR"))
            {
                case "ZERO_RESULTS":
                    reason = "No results found for the given address!";
                    break;
                case "OVER_QUERY_LIMIT":
                    reason = "Todays google search query limit exceeded!";
                    break;
                case "REQUEST_DENIED":
                    reason = "The request was denied!";
                    break;
                case "INVALID_REQUEST":
                    reason = "The query has missing values!";
                    break;
                case "UNKNOWN_ERROR":
                default:
                    reason = "The query failed due to unknown error!";
            }
            throw new GoogleSvcException(reason);
        }
        return true;
    }
    
    /**
     * Thrown when an error occurs while processing any request and it has a user friendly error message
     */
    public static class GoogleSvcException extends Exception
    {
        public GoogleSvcException(String msg)
        {
            super(msg);
        }
    }
    
    /**
     * It holds the longitude and latitude
     */
    public static class Location
    {
        private double lat,lng;
        
        public Location(double latitude, double longitude)
        {
            lat = latitude;
            lng = longitude;
        }
        
        public double latitude()
        {
            return lat;
        }
        public double longitude()
        {
            return lng;
        }
        public void setLocation(double latitude, double longitude)
        {
            lat = latitude;
            lng = longitude;
        }
    }
}
