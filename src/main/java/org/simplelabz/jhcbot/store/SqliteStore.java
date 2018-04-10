package org.simplelabz.jhcbot.store;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simplelabz.jhcbot.Conf;

/**
 *
 * @author simple
 */
public class SqliteStore extends DataStore
{
    private final String dburl;
    private String name;
    private String chan;
    
    public SqliteStore(String name, String chan)
    {
        this.name = name;
        this.chan = chan;
        
        String datadir = "";
        if(Conf.DATADIR!=null && new File(Conf.DATADIR).exists())
            datadir = Conf.DATADIR+System.getProperty("file.separator");
        dburl = "jdbc:sqlite:"+datadir+DataStore.DB_NAME+".db";
        
        String sql = "CREATE TABLE IF NOT EXISTS "+name+" (\n"
                + "   id integer PRIMARY KEY,\n"
                + "   chan TEXT NOT NULL,\n"
                + "   key TEXT NOT NULL,\n"
                + "   value TEXT \n"
                + "   );";

        try(Connection con = connect(); Statement stmt = con.createStatement())
        {
            stmt.execute(sql);
        }
        catch(SQLException ex)
        {
            Logger.getLogger(SqliteStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void store(DataEntry entry)
    {
        String sql = "INSERT INTO "+name+"(chan,key,value) VALUES(?,?,?)";

        try(Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, chan);
            pstmt.setString(2, entry.getKey());
            pstmt.setString(3, entry.getValue());
            pstmt.executeUpdate();
        }
        catch(SQLException ex)
        {
            Logger.getLogger(SqliteStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String[] getKeys()
    {
        ArrayList<String> values = new ArrayList<>();
        String sql = "SELECT key FROM "+name+" WHERE chan = ?";
        try(Connection conn = this.connect(); PreparedStatement pstmt  = conn.prepareStatement(sql))
        {
            pstmt.setString(1, chan);
            ResultSet res = pstmt.executeQuery();
            while(res.next())
            {
                String key = res.getString("key");
                if(!values.contains(key))
                    values.add(key);
            }
        }
        catch(SQLException ex)
        {
            Logger.getLogger(SqliteStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        return values.toArray(new String[values.size()]);
    }

    @Override
    public DataEntry[] get(String key)
    {
        ArrayList<DataEntry> values = new ArrayList<>();
        String sql = "SELECT id, value FROM "+name+" WHERE chan = ? AND key = ?";

        try(Connection conn = this.connect(); PreparedStatement pstmt  = conn.prepareStatement(sql))
        {
            pstmt.setString(1, chan);
            pstmt.setString(2, key);
            ResultSet res = pstmt.executeQuery();

            while(res.next())
                values.add(new DataEntry(res.getInt("id"),key,res.getString("value")));
        }
        catch(SQLException ex)
        {
            Logger.getLogger(SqliteStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        return values.toArray(new DataEntry[values.size()]);
    }

    @Override
    public void delete(int key)
    {
        String sql = "DELETE from "+name+" WHERE id = ?";

        try(Connection conn = this.connect(); PreparedStatement pstmt  = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, key);
            pstmt.executeUpdate();
        }
        catch(SQLException ex)
        {
            Logger.getLogger(SqliteStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Connection connect()
    {
        Connection con = null;
        try
        {
            con = DriverManager.getConnection(dburl);
            //System.out.println("SQL Connection established!");
            //System.out.println("Driver: "+con.getMetaData().getDriverName()+" ("+con.getMetaData().getDriverVersion()+")");
        }
        catch(SQLException ex)
        {
            Logger.getLogger(SqliteStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        return con;
    }
}
