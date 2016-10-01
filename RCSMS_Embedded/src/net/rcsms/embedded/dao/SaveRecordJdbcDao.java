package net.rcsms.embedded.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SaveRecordJdbcDao implements Serializable, AutoCloseable {
    
    private Connection con;

    public SaveRecordJdbcDao(String host, String port, String db, String user, 
            String password) throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            
            String datasource = new StringBuilder("jdbc:mysql://" + host + ":" + port + "/" 
                    + db + "?user=" + user + "&password=" + password + "&characterEncoding=utf-8" ).toString();
            
            con = DriverManager.getConnection(datasource);
        } catch (ClassNotFoundException e) {
        }
    }

    @Override
    public void close() throws Exception {
        con.close();
    }
    
    public boolean saveRecord(String datetime, String devicenumber, int temperature, 
            int humidity, int gas, int smoke) throws SQLException{
        boolean result = false;
        String SAVE_SQL = "INSERT INTO record VALUES(0, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(SAVE_SQL);
        ps.setString(1, datetime);
        ps.setString(2, devicenumber);
        ps.setInt(3, temperature);
        ps.setInt(4, humidity);
        ps.setInt(5, gas);
        ps.setInt(6, smoke);
        if(ps.executeUpdate() > 0){
            result = true;
        }
        return result;
    }
}
