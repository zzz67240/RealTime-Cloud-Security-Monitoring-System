package net.rcsms.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import net.rcsms.domain.DeviceType;

public class DeviceTypeJdbcDao implements DeviceTypeDao, Serializable {

    private Connection con;

    public DeviceTypeJdbcDao() throws SQLException {
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
    public List<DeviceType> getAll() throws SQLException {
        List<DeviceType> result = new ArrayList<>();

        String RECORD_SQL = "SELECT * FROM deviceType";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(RECORD_SQL);

        while (rs.next()) {
            int serialNumber = rs.getInt("SerialNumber");
            String type = rs.getString("Type");

            DeviceType deviceType = new DeviceType(serialNumber, type);
            result.add(deviceType);
        }
        return result;
    }
}
