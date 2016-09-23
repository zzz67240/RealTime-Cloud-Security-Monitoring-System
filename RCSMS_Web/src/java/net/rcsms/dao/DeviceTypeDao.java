package net.rcsms.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import net.rcsms.domain.DeviceType;

public interface DeviceTypeDao extends AutoCloseable {
    public List<DeviceType> getAll() throws IOException, SQLException;
}
