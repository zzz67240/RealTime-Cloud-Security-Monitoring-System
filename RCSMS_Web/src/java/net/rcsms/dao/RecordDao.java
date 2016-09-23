package net.rcsms.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import net.rcsms.domain.Record;

public interface RecordDao extends AutoCloseable {
    public Record get(int serialnumber) throws SQLException;
    public List<Record> getAll() throws SQLException;
    public List<Record> QueryByDSN(int deviceserialnumberint) throws SQLException;
    public List<Record> QueryByDate(Date date1, Date date2) throws SQLException;
    public List<Record> UnusualStateQuery(int maxtemp, int mintemp, int maxhumi, 
            int minhumi, int maxgas, int maxsmok) throws SQLException;
}
