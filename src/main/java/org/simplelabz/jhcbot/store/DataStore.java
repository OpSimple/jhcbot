package org.simplelabz.jhcbot.store;

import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simplelabz.jhcbot.Conf;
import org.simplelabz.jhcbot.Utils;

/**
 * 
 * @author simple
 */
public abstract class DataStore
{
    public static String DB_NAME;
    
    static
    {
        String host = "";
        try{
            host = new java.net.URI(Conf.URI).getHost();
        } catch (URISyntaxException ex) {
            Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        DB_NAME = "jhcbot"+host.replaceAll("\\.", "");
    }
    
    /**
     * Store this entry to the database
     * @param entry 
     */
    abstract public void store(DataEntry entry);
    
    /**
     * get entries matching this key
     * @param key
     * @return 
     */
    abstract public DataEntry[] get(String key);
    
    /**
     * get all keys
     * @return 
     */
    abstract public String[] getKeys();
    
    /**
     * Delete an entry by its id
     * @param id 
     */
    abstract public void delete(int id);
    
    /**
     * If the database contains this key
     * @param key
     * @return 
     */
    public boolean containsKey(String key)
    {
        return get(key).length!=0;
    }
    
    /**
     * Store this key = value pair into the database
     * @param key
     * @param value 
     */
    public void store(String key, String value)
    {
        store(new DataEntry(key, value));
    }
    
    /**
     * Get the best available data store
     * @param name
     * @param chan
     * @return 
     */
    public static DataStore getDataStore(String name, String chan)
    {
        if(Utils.classExists("org.postgresql.Driver"))
            return new PostgresStore(name, chan);
        else if (Utils.classExists("org.sqlite.JDBC"))
            return new SqliteStore(name, chan);
        else
            return new XMLStore(name, chan);
    }
    
    /**
     * Entries to be stored in the store/database
     */
    public static class DataEntry
    {
        private int id;
        private String key;
        private String value;

        public DataEntry()
        {
            this(-1, null, null);
        }
        public DataEntry(String key, String value)
        {
            this(-1, key, value);
        }
        public DataEntry(int id, String key, String value)
        {
            this.id = id;
            this.key = key;
            this.value = value;
        }

        public void setID(int id)
        {
            this.id = id;
        }
        public void setKey(String key)
        {
            this.key = key;
        }
        public void setValue(String value)
        {
            this.value = value;
        }

        public int getID()
        {
            return this.id;
        }
        public String getKey()
        {
            return this.key;
        }
        public String getValue()
        {
            return this.value;
        }
    }
}
