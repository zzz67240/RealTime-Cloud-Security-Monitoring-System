package net.rcsms.dao;

import java.sql.SQLException;
import java.util.List;

public interface LinkDao extends AutoCloseable {
    public boolean add(int customerSerialNumber, String deviceSerialNumber) throws SQLException;
    public List<String> query (int customerSerialNumber) throws SQLException;
}
