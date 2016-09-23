package net.rcsms.dao;

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
import net.rcsms.domain.Device;

public class DeviceJdbcDao implements DeviceDao, Serializable {

    private Connection con;

    public DeviceJdbcDao() throws SQLException {
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
    public boolean add(Device device) throws SQLException {
        boolean result = false;
        String QUERY_INSERT = "INSERT INTO device VALUES(?, ?, ? ,? ,?)";
        PreparedStatement ps = con.prepareStatement(QUERY_INSERT);
        ps.setString(1, device.getSerialnumber());
        ps.setInt(2, device.getDevicetype());
        ps.setDate(3, new java.sql.Date(device.getProductiondate().getTime()));
        ps.setDate(4, new java.sql.Date(device.getPurchasedate().getTime()));
        ps.setDate(5, new java.sql.Date(device.getWarrantydate().getTime()));
        
        if (ps.executeUpdate() > 0) {
            result = true;
        }
        return result;
    }

    @Override
    public Device get(String serialnumber) throws SQLException {
        Device result = null;

        String GET_SQL = "SELECT * FROM device JOIN deviceType ON device.DeviceType = DeviceType.SerialNumber "
                + "WHERE device.SerialNumber = ?";
        PreparedStatement ps = con.prepareStatement(GET_SQL);
        ps.setString(1, serialnumber);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String dsn = rs.getString("SerialNumber");
            String sdevicetype = rs.getString("Type");
            Date productiondate = rs.getDate("ProductionDate");
            Date purchasedate = rs.getDate("PurchaseDate");
            Date warrantydate = rs.getDate("WarrantyDate");

            result = new Device(dsn, sdevicetype, productiondate, purchasedate, warrantydate);
        }
        return result;
    }
    
    @Override
    public List<Device> query(int customerserialnumber) throws SQLException{
        List<Device> devicelist = new ArrayList<>();
        
        String QUERY_SQL = "SELECT device.*, devicetype.Type FROM link "
                + "JOIN device ON DeviceSerialNumber = device.SerialNumber "
                + "JOIN devicetype ON device.DeviceType = devicetype.SerialNumber "
                + "JOIN customer ON CustomerSerialNumber = customer.SerialNumber "
                + "WHERE CustomerSerialNumber = ?";
        
        PreparedStatement ps = con.prepareStatement(QUERY_SQL);
        ps.setInt(1, customerserialnumber);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            devicelist.add(new Device(rs.getString("SerialNumber"), rs.getString("Type"), 
                    rs.getDate("ProductionDate"), rs.getDate("PurchaseDate"), rs.getDate("WarrantyDate")));
        }
        return devicelist;
    }
    
    @Override
    //也可以在Device JAVABEAN裡設定一個 typename Attribute，在查詢要GET時直接對應名稱並回傳。
    public String getDevicetype(int devicetypenumber) throws SQLException{
        String devicetype = null;
        String QUERY_TYPE = "SELECT Type FROM devicetype WHERE ? = devicetype.SerialNumber";
        PreparedStatement ps = con.prepareStatement(QUERY_TYPE);
        ps.setInt(1, devicetypenumber);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            devicetype = rs.getString("Type");            
        }
        return devicetype;
    }

    @Override
    public List<Device> getAll() throws SQLException {
        List<Device> result = new ArrayList<>();

        String DEVICE_SQL = "SELECT * FROM device JOIN deviceType ON device.DeviceType = DeviceType.SerialNumber";
//        可以查詢裝置及對應的擁有者，SELECT device.*, devicetype.Type, customer.SerialNumber, customer.FirstName, customer.LastName FROM link JOIN device ON DeviceSerialNumber = device.SerialNumber JOIN devicetype ON device.DeviceType = devicetype.SerialNumber JOIN customer ON CustomerSerialNumber = customer.SerialNumber 
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(DEVICE_SQL);

        while (rs.next()) {
            String dsn = rs.getString("SerialNumber");
            String ddevicetype = rs.getString("Type");
            Date dproductiondate = rs.getDate("ProductionDate");
            Date dpurchasedate = rs.getDate("PurchaseDate");
            Date dwarrantydate = rs.getDate("WarrantyDate");

            Device device = new Device(dsn, ddevicetype, dproductiondate, dpurchasedate, dwarrantydate);
            result.add(device);
        }
        return result;
    }
    
//    @Override
//    public List<Device> query(String serialnumber, int devicetype, String sproductiondate,
//            String spurchasedate, String swarrantydate) throws SQLException {
//        List<Device> result = new ArrayList<>();
//        String a = "";
//        String b = "";
//        String c = "";
//        String d = "";
//        String e = "";
//        String f = "";
//        String g = "";
//        String h = "";
//
//        int index = 0;
//        Date productiondate = null;
//        Date purchasedate = null;
//        Date warrantydate = null;
//
//        Pattern sp = Pattern.compile("[\\>\\<\\=]");
//        Matcher sprodm = sp.matcher(sproductiondate);
//        Matcher spurdm = sp.matcher(spurchasedate);
//        Matcher swardm = sp.matcher(swarrantydate);
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        if (!sprodm.find()) {
//            try {
//                sdf.setLenient(false);
//                java.util.Date utilproductiondate = sdf.parse(sproductiondate);
//                productiondate = new java.sql.Date(utilproductiondate.getTime());
//                sproductiondate = null;
//            } catch (NumberFormatException | ParseException e2) {
//                System.out.println(e2.getMessage());
//            }
//        }
//        if (!spurdm.find()) {
//            try {
//                sdf.setLenient(false);
//                java.util.Date utilpurchasedate = sdf.parse(spurchasedate);
//                purchasedate = new java.sql.Date(utilpurchasedate.getTime());
//                spurchasedate = null;
//            } catch (NumberFormatException | ParseException e2) {
//                System.out.println(e2.getMessage());
//            }
//        }
//        if (!swardm.find()) {
//            try {
//                sdf.setLenient(false);
//                java.util.Date utilwarrantydate = sdf.parse(swarrantydate);
//                warrantydate = new java.sql.Date(utilwarrantydate.getTime());
//                swarrantydate = null;
//            } catch (NumberFormatException | ParseException e2) {
//                System.out.println(e2.getMessage());
//            }
//        }
//
//        if (serialnumber != null && serialnumber.length() != 0) {
//            a = " SerialNumber = ?";
//            if (devicetype != 0) {
//                b = " AND DeviceType = ?";
//            }
//        } else if (devicetype != 0) {
//            b = " DeviceType = ?";
//        }
//
//        if ((!a.equals("") || !b.equals(""))) {
//            if (productiondate != null) {
//                c = " AND ProductionDate = ?";
//            } else if (sproductiondate != null && !sproductiondate.equals("=")) {
//                if ((sproductiondate.startsWith(">=") || sproductiondate.startsWith("<="))) {
//                    d = " AND ProductionDate " + sproductiondate.substring(0, 2) + "\"" + sproductiondate.substring(2) + "\"";
//                } else if ((sproductiondate.startsWith(">") || sproductiondate.startsWith("<") || sproductiondate.startsWith("="))) {
//                    d = " AND ProductionDate " + sproductiondate.substring(0, 1) + "\"" + sproductiondate.substring(1) + "\"";
//                }
//            }
//        } else if (a.equals("") && b.equals("")) {
//            if (productiondate != null) {
//                c = " ProductionDate = ?";
//            } else if (sproductiondate != null && !sproductiondate.equals("=")) {
//                if ((sproductiondate.startsWith(">=") || sproductiondate.startsWith("<="))) {
//                    d = " ProductionDate " + sproductiondate.substring(0, 2) + "\"" + sproductiondate.substring(2) + "\"";
//                } else if ((sproductiondate.startsWith(">") || sproductiondate.startsWith("<") || sproductiondate.startsWith("="))) {
//                    d = " ProductionDate " + sproductiondate.substring(0, 1) + "\"" + sproductiondate.substring(1) + "\"";
//                }
//            }
//        }
//        if ((!a.equals("") || !b.equals("") || !c.equals("") || !d.equals(""))) {
//            if (purchasedate != null) {
//                e = " AND PurchaseDate = ?";
//            } else if (spurchasedate != null && !spurchasedate.equals("=")) {
//                if ((spurchasedate.startsWith(">=") || spurchasedate.startsWith("<="))) {
//                    f = " AND PurchaseDate " + spurchasedate.substring(0, 2) + "\"" + spurchasedate.substring(2) + "\"";
//                } else if ((spurchasedate.startsWith(">") || spurchasedate.startsWith("<") || spurchasedate.startsWith("="))) {
//                    f = " AND PurchaseDate " + spurchasedate.substring(0, 1) + "\"" + spurchasedate.substring(1) + "\"";
//                }
//            }
//        } else if (a.equals("") && b.equals("") && c.equals("") && d.equals("")) {
//            if (purchasedate != null) {
//                e = " PurchaseDate = ?";
//            } else if (spurchasedate != null && !spurchasedate.equals("=")) {
//                if ((spurchasedate.startsWith(">=") || spurchasedate.startsWith("<="))) {
//                    f = " PurchaseDate " + spurchasedate.substring(0, 2) + "\"" + spurchasedate.substring(2) + "\"";
//                } else if ((spurchasedate.startsWith(">") || spurchasedate.startsWith("<") || spurchasedate.startsWith("="))) {
//                    f = " PurchaseDate " + spurchasedate.substring(0, 1) + "\"" + spurchasedate.substring(1) + "\"";
//                }
//            }
//        }
//        if ((!a.equals("") || !b.equals("") || !c.equals(""))) {
//            if (warrantydate != null) {
//                g = " AND WarrantyDate = ?";
//            } else if (swarrantydate != null && !swarrantydate.equals("=")) {
//                if ((swarrantydate.startsWith(">=") || swarrantydate.startsWith("<="))) {
//                    h = " AND WarrantyDate " + swarrantydate.substring(0, 2) + "\"" + swarrantydate.substring(2) + "\"";
//                } else if ((swarrantydate.startsWith(">") || swarrantydate.startsWith("<") || swarrantydate.startsWith("="))) {
//                    h = " AND WarrantyDate " + swarrantydate.substring(0, 1) + "\"" + swarrantydate.substring(1) + "\"";
//                }
//            }
//        } else if (a.equals("") && b.equals("") && c.equals("")) {
//            if (warrantydate != null) {
//                g = " WarrantyDate = ?";
//            } else if (swarrantydate != null && !swarrantydate.equals("=")) {
//                if ((swarrantydate.startsWith(">=") || swarrantydate.startsWith("<="))) {
//                    h = " WarrantyDate " + swarrantydate.substring(0, 2) + "\"" + swarrantydate.substring(2) + "\"";
//                } else if ((swarrantydate.startsWith(">") || swarrantydate.startsWith("<") || swarrantydate.startsWith("="))) {
//                    h = " WarrantyDate " + swarrantydate.substring(0, 1) + "\"" + swarrantydate.substring(1) + "\"";
//                }
//            }
//        }
//
//        String QUERY_SQL = "SELECT * FROM device WHERE" + a + b + c + d + e + f + g + h;
//        PreparedStatement ps = con.prepareStatement(QUERY_SQL);
//
//        if (!a.equals("")) {
//            ps.setString(++index, serialnumber);
//        }
//        if (!c.equals("")) {
//            ps.setDate(++index, productiondate);
//        }
//        if (!e.equals("")) {
//            ps.setDate(++index, purchasedate);
//        }
//        if (!g.equals("")) {
//            ps.setDate(++index, warrantydate);
//        }
//
//        ResultSet rs = ps.executeQuery();
//        while (rs.next()) {
//            String dsn = rs.getString("SerialNumber");
//            int ddevicetype = rs.getInt("DeviceType");
//            Date dproductiondate = rs.getDate("ProductionDate");
//            Date dpurchasedate = rs.getDate("PurchaseDate");
//            Date dwarrantydate = rs.getDate("WarrantyDate");
//
//            result.add(new Device(dsn, ddevicetype, dproductiondate, dpurchasedate, dwarrantydate));
//        }
//        return result;
//    }
}
