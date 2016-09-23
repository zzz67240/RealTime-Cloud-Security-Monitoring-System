package net.rcsms.dao;

import java.sql.SQLException;
import java.util.List;
import net.rcsms.domain.Device;

public interface DeviceDao extends AutoCloseable {
    public boolean add(Device device) throws SQLException;
    public Device get(String serialnumber) throws SQLException;
    public String getDevicetype(int devicetypenumber) throws SQLException;
    public List<Device> query(int customerserialnumber) throws SQLException;
    public List<Device> getAll() throws SQLException;
}
