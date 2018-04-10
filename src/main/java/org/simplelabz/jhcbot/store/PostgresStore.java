package org.simplelabz.jhcbot.store;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author simple
 */
public class PostgresStore extends DataStore
{
    private String dburl;
    private String username;
    private String password;
    private String name;
    private String chan;
    
    public PostgresStore(String name, String chan)
    {
        this.name = name;
        this.chan = chan;
        
        String url = System.getenv("DATABASE_URL");
        if(url!=null || !url.trim().isEmpty())
        {
            java.net.URI uri = null;
            try {
                uri = new java.net.URI(url);
                username = uri.getUserInfo().split(":")[0];
                password = uri.getUserInfo().split(":")[1];
                dburl = "jdbc:postgresql://"+uri.getHost()+':'+uri.getPort()+uri.getPath();
            } catch (URISyntaxException ex) {
                Logger.getLogger(PostgresStore.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        else
        {
            throw new RuntimeException("Error while gathering data for postgres uri");
        }
        
        String sql = "CREATE TABLE IF NOT EXISTS "+name+" (\n"
                + "   ID SERIAL PRIMARY KEY,\n"
                + "   CHAN CHAR(30) NOT NULL,\n"
                + "   KEY TEXT NOT NULL,\n"
                + "   VALUE TEXT \n"
                + "   );";

        try(Connection con = connect(); Statement stmt = con.createStatement())
        {
            stmt.execute(sql);
        }
        catch(SQLException ex)
        {
            Logger.getLogger(PostgresStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void store(DataStore.DataEntry entry)
    {
        String sql = "INSERT INTO "+name+" (CHAN,KEY,VALUE) VALUES (?,?,?) ";

        try(Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, chan);
            pstmt.setString(2, entry.getKey());
            pstmt.setString(3, entry.getValue());
            pstmt.executeUpdate();
        }
        catch(SQLException ex)
        {
            Logger.getLogger(PostgresStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String[] getKeys()
    {
        ArrayList<String> values = new ArrayList<>();
        String sql = "SELECT KEY FROM "+name+" WHERE CHAN = ?";
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
            Logger.getLogger(PostgresStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        return values.toArray(new String[values.size()]);
    }

    @Override
    public DataStore.DataEntry[] get(String key)
    {
        ArrayList<DataStore.DataEntry> values = new ArrayList<>();
        String sql = "SELECT ID, VALUE FROM "+name+" WHERE CHAN = ? AND KEY = ?";

        try(Connection conn = this.connect(); PreparedStatement pstmt  = conn.prepareStatement(sql))
        {
            pstmt.setString(1, chan);
            pstmt.setString(2, key);
            ResultSet res = pstmt.executeQuery();

            while(res.next())
                values.add(new DataStore.DataEntry(res.getInt("id"),key,res.getString("value")));
        }
        catch(SQLException ex)
        {
            Logger.getLogger(PostgresStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        return values.toArray(new DataStore.DataEntry[values.size()]);
    }

    @Override
    public void delete(int key)
    {
        String sql = "DELETE FROM "+name+" WHERE ID = ?";

        try(Connection conn = this.connect(); PreparedStatement pstmt  = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, key);
            pstmt.executeUpdate();
        }
        catch(SQLException ex)
        {
            Logger.getLogger(PostgresStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Connection connect()
    {
        Connection con = null;
        try
        {
            con = DriverManager.getConnection(dburl, username, password);
            //System.out.println("SQL Connection established!");
            //System.out.println("Driver: "+con.getMetaData().getDriverName()+" ("+con.getMetaData().getDriverVersion()+")");
        }
        catch(SQLException ex)
        {
            Logger.getLogger(PostgresStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        return con;
    }
}
