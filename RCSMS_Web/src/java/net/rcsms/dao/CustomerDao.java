package net.rcsms.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import net.rcsms.domain.Customer;

public interface CustomerDao extends AutoCloseable {
    public int add(Customer customer) throws IOException, SQLException;
    public boolean update(Customer customer) throws IOException, SQLException;
    public boolean delete(int serialnumber) throws IOException, SQLException;
    public Customer get(int serialnumber) throws IOException, SQLException;
    public List<Customer> query(int serialnumber, String firstName, String lastName, 
            int gender, String sbirthday, String phonenumber, String address) throws SQLException;
    public List<Customer> getAll() throws IOException, SQLException;
}
