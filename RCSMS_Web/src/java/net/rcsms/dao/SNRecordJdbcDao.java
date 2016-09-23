package net.rcsms.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class SNRecordJdbcDao implements SNRecordDao, Serializable {

    private Connection con;

    public SNRecordJdbcDao() throws SQLException {
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
    public int getSN(String tablename) throws SQLException {
        int sn = 0;
        String GETSN_SQL = "SELECT SN FROM snrecord WHERE TableName = ?";
        PreparedStatement ps = con.prepareStatement(GETSN_SQL);
        ps.setString(1, tablename);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            sn = rs.getInt("SN");
        }
        return sn;
    }

    @Override
    public boolean setSN(String tablename) throws SQLException {
        boolean result = false;
        String SETSN_SQL = "UPDATE snrecord SET SN = SN + 1 WHERE TableName = ?";
        PreparedStatement ps = con.prepareStatement(SETSN_SQL);
        ps.setString(1, tablename);
        if(ps.execute()){
            result = true;
        }
        return result;
    }
}
