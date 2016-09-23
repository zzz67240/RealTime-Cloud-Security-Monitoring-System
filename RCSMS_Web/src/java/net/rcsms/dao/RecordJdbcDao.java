package net.rcsms.dao;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import net.rcsms.domain.Record;

public class RecordJdbcDao implements RecordDao, Serializable {

    private Connection con;

    public RecordJdbcDao() throws SQLException {
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
    public Record get(int serialnumber) throws SQLException {
        Record result = null;

        String GET_SQL = "SELECT * FROM record WHERE SerialNumber = ?";
        PreparedStatement ps = con.prepareStatement(GET_SQL);
        ps.setInt(1, serialnumber);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int rsn = rs.getInt("SerialNumber");
            Date datetime = rs.getDate("DateTime");
            String deviceserialnumber = rs.getString("DeviceSerialNumber");
            int temperature = rs.getInt("Temperature");
            int humidity = rs.getInt("Humidity");
            int gas = rs.getInt("Gas");
            int smoke = rs.getInt("Smoke");

            result = new Record(rsn, datetime, deviceserialnumber, temperature, humidity, gas, smoke);
        }
        return result;
    }

    @Override
    public List<Record> getAll() throws SQLException {
        List<Record> recordlist = new ArrayList<>();

        String RECORD_SQL = "SELECT * FROM record";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(RECORD_SQL);

        while (rs.next()) {
            int rsn = rs.getInt("SerialNumber");
            Date datetime = rs.getDate("DateTime");
            String deviceserialnumber = rs.getString("DeviceSerialNumber");
            int temperature = rs.getInt("Temperature");
            int humidity = rs.getInt("Humidity");
            int gas = rs.getInt("Gas");
            int smoke = rs.getInt("Smoke");

            Record record = new Record(rsn, datetime, deviceserialnumber, temperature, humidity, gas, smoke);
            recordlist.add(record);
        }
        return recordlist;
    }
    
    @Override
    public List<Record> QueryByDSN(int deviceserialnumberint) throws SQLException{
        List<Record> result = new ArrayList<>();
        String deviceserialnumber = String.format("D%04d", deviceserialnumberint);
        String QUERYBYDSN_SQL = "SELECT * FROM record WHERE DeviceSerialNumber = ?";
        PreparedStatement ps = con.prepareStatement(QUERYBYDSN_SQL);
        ps.setString(1, deviceserialnumber);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int rsn = rs.getInt("SerialNumber");
            Date datetime = rs.getDate("DateTime");
            String sdeviceserialnumber = rs.getString("DeviceSerialNumber");
            int temperature = rs.getInt("Temperature");
            int humidity = rs.getInt("Humidity");
            int gas = rs.getInt("Gas");
            int smoke = rs.getInt("Smoke");
            result.add(new Record(rsn, datetime, sdeviceserialnumber, temperature, humidity, gas, smoke));
        }
        return result;
    }

    @Override
    public List<Record> QueryByDate(java.util.Date date1, java.util.Date date2) throws SQLException {
        List<Record> result = new ArrayList<>();

        String QUERYBYDATE_SQL = "SELECT * FROM record WHERE DateTime BETWEEN ? AND ?";
        PreparedStatement ps = con.prepareStatement(QUERYBYDATE_SQL);
        ps.setDate(1, new java.sql.Date(date1.getTime()));
        ps.setDate(2, new java.sql.Date(date2.getTime()));
        
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int rsn = rs.getInt("SerialNumber");
            Date datetime = rs.getDate("DateTime");
            String deviceserialnumber = rs.getString("DeviceSerialNumber");
            int temperature = rs.getInt("Temperature");
            int humidity = rs.getInt("Humidity");
            int gas = rs.getInt("Gas");
            int smoke = rs.getInt("Smoke");
            result.add(new Record(rsn, datetime, deviceserialnumber, temperature, humidity, gas, smoke));
        }
        return result;
    }
    
    public List<Record> getUsq(){
        List<Record> result = null; 
        try {
            result = UnusualStateQuery(75, 0, 80, 30, 1, 1);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }
    
    @Override
    public List<Record> UnusualStateQuery(int maxtemp, int mintemp, int maxhumi,
            int minhumi, int maxgas, int maxsmok) throws SQLException {
        List<Record> result = new ArrayList<>();

        String UNUSUALSTATEQUERY_SQL = "SELECT * FROM record WHERE Temperature > ? "
                + "OR Temperature < ? OR Humidity > ? OR Humidity < ? OR Gas > ? OR Smoke > ?";
        PreparedStatement ps = con.prepareStatement(UNUSUALSTATEQUERY_SQL);

        ps.setInt(1, maxtemp);
        ps.setInt(2, mintemp);
        ps.setInt(3, maxhumi);
        ps.setInt(4, minhumi);
        ps.setInt(5, maxgas);
        ps.setInt(6, maxsmok);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int rsn = rs.getInt("SerialNumber");
            Date datetime = rs.getDate("DateTime");
            String deviceserialnumber = rs.getString("DeviceSerialNumber");
            int temperature = rs.getInt("Temperature");
            int humidity = rs.getInt("Humidity");
            int gas = rs.getInt("Gas");
            int smoke = rs.getInt("Smoke");
            result.add(new Record(rsn, datetime, deviceserialnumber, temperature, humidity, gas, smoke));
        }
        return result;
    }
}
