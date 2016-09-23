package net.rcsms.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class LinkJdbcDao implements LinkDao, Serializable {

    private Connection con;

    public LinkJdbcDao() throws SQLException {
        try {
            Context context = new InitialContext();

            if (context == null) {
                throw new SQLException("InitialContext error");
            }

            DataSource dataSource = (DataSource) context.lookup(
                    "java:comp/env/jdbc/ocejwcdDB");
            con = dataSource.getConnection();
        } catch (NamingException e) {
            throw new SQLException("InitialContext error", e);
        }
    }

    @Override
    public void close() throws Exception {
        con.close();
    }

    @Override
    public boolean add(int customerSerialNumber, String deviceSerialNumber) throws SQLException {
        boolean result = false;
        String QUERY_INSERT = "INSERT INTO link VALUES(0, ?, ?)";
        PreparedStatement ps = con.prepareStatement(QUERY_INSERT);
        ps.setInt(1, customerSerialNumber);
        ps.setString(2, deviceSerialNumber);
        if(ps.execute()){
            result = true;
        }
        return result;
    }

    @Override
    public List<String> query(int customerSerialNumber) throws SQLException {
        List<String> deviceSerialNumberList = new ArrayList<>();
        String QUERY = "SELECT * FROM link WHERE CustomerSerialNumber = ?";
        PreparedStatement ps = con.prepareStatement(QUERY);
        ps.setInt(1, customerSerialNumber);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            deviceSerialNumberList.add(rs.getString("DeviceSerialNumber"));
        }
        return deviceSerialNumberList;
    }
}
