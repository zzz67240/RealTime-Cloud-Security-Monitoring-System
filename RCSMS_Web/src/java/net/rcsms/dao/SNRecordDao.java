package net.rcsms.dao;

import java.io.IOException;
import java.sql.SQLException;

public interface SNRecordDao extends AutoCloseable {
    public int getSN(String tablename) throws IOException, SQLException;
    public boolean setSN(String tablename) throws IOException, SQLException;
}
