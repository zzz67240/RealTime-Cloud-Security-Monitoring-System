package net.rcsms.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import net.rcsms.domain.Customer;

public class CustomerJdbcDao implements CustomerDao, Serializable {

    private Connection con;

    public CustomerJdbcDao() throws SQLException {
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
    public int add(Customer customer) throws SQLException {
        int result = -1;
        String QUERY_INSERT = "INSERT INTO customer VALUES(0, ?, ? ,? ,? ,? ,?)";
        PreparedStatement ps = con.prepareStatement(QUERY_INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, customer.getFirstname());
        ps.setString(2, customer.getLastname());
        ps.setInt(3, customer.getGender());
        ps.setDate(4, new java.sql.Date(customer.getBirthday().getTime()));
        ps.setString(5, customer.getPhonenumber());
        ps.setString(6, customer.getAddress());

        int rc = ps.executeUpdate();
        
        if (rc > 0) {
            ResultSet rs = ps.getGeneratedKeys();
            
            if (rs.next()) {
                result = rs.getInt(1);
            }
        }

        return result;
    }

    @Override
    public boolean update(Customer customer) throws SQLException {
        boolean result = false;
        String UPDATE_SQL = "UPDATE customer SET Firstname = ?, Lastname = ?, Gender = ?, "
                + "BirthDay = ?, Phonenumber = ?, Address = ? WHERE Serialnumber = ?";
        PreparedStatement ps = con.prepareStatement(UPDATE_SQL);
        ps.setString(1, customer.getFirstname());
        ps.setString(2, customer.getLastname());
        ps.setInt(3, customer.getGender());
        ps.setDate(4, new java.sql.Date(customer.getBirthday().getTime()));
        ps.setString(5, customer.getPhonenumber());
        ps.setString(6, customer.getAddress());
        ps.setInt(7, customer.getSerialnumber());

        if (ps.executeUpdate() == 1) {
            result = true;
        }
        return result;
    }

    @Override
    public boolean delete(int serialnumber) throws SQLException {
        boolean result = false;
        String DELETE_SQL = "DELETE FROM customer WHERE Serialnumber = ?";
        PreparedStatement ps = con.prepareStatement(DELETE_SQL);
        ps.setInt(1, serialnumber);

        if (ps.executeUpdate() == 1) {
            result = true;
        }
        return result;
    }

    @Override
    public Customer get(int serialnumber) throws SQLException {
        Customer result = null;

        String GET_SQL = "SELECT * FROM customer WHERE Serialnumber =?";
        PreparedStatement ps = con.prepareStatement(GET_SQL);
        ps.setInt(1, serialnumber);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int csn = rs.getInt("SerialNumber");
            String firstname = rs.getString("FirstName");
            String lastname = rs.getString("LastName");
            int gender = rs.getInt("Gender");
            Date birthday = rs.getDate("Birthday");
            String phonenumber = rs.getString("PhoneNumber");
            String address = rs.getString("Address");

            result = new Customer(csn, firstname, lastname, gender, birthday, phonenumber, address);
        }
        return result;
    }
    
    @Override
    public List<Customer> query(int serialnumber, String firstname, String lastname,
            int gender, String sbirthday, String phonenumber, String address) throws SQLException {
        List<Customer> result = new ArrayList<>();
        String a = "";
        String b = "";
        String c = "";
        String d = "";
        String e = "";
        String f = "";
        String g = "";
        String h = "";

        int index = 0;
        Date birthday = null;

        Pattern sp = Pattern.compile("[\\>\\<\\=]");
        Matcher sbm = sp.matcher(sbirthday);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (!sbm.find()) {
            try {
                sdf.setLenient(false);
                java.util.Date utilbirthday = sdf.parse(sbirthday);
                birthday = new java.sql.Date(utilbirthday.getTime());
                sbirthday = null;
            } catch (NumberFormatException | ParseException e2) {
                System.out.println(e2.getMessage());
            }
        }

        if (serialnumber != 0) {
            a = " Serialnumber = ?";
            if (firstname.length() != 0) {
                b = " AND Firstname LIKE ?";
            }
        } else if (firstname.length() != 0) {
            b = " Firstname LIKE ?";
        }
        if ((!a.equals("") || !b.equals("")) && lastname.length() != 0) {
            c = " AND Lastname LIKE ?";
        } else if (a.equals("") && b.equals("") && lastname.length() != 0) {
            c = " Lastname LIKE ?";
        }
        if ((!a.equals("") || !b.equals("") || !c.equals("")) && gender != 0) {
            d = " AND Gender = ?";
        } else if (a.equals("") && b.equals("") && c.equals("") && gender != 0) {
            d = " Gender = ?";
        }
        if ((!a.equals("") || !b.equals("") || !c.equals("") || !d.equals(""))) {
            if (birthday != null) {
                e = " AND Birthday = ?";
            } else if (sbirthday != null && !sbirthday.equals("=")) {
                if ((sbirthday.startsWith(">=") || sbirthday.startsWith("<="))) {
                    f = " AND Birthday " + sbirthday.substring(0, 2) + "\"" + sbirthday.substring(2) + "\"";
                } else if ((sbirthday.startsWith(">") || sbirthday.startsWith("<") || sbirthday.startsWith("="))) {
                    f = " AND Birthday " + sbirthday.substring(0, 1) + "\"" + sbirthday.substring(1) + "\"";
                }
            }
        } else if (a.equals("") && b.equals("") && c.equals("") && d.equals("")) {
            if (birthday != null) {
                e = " Birthday = ?";
            } else if (sbirthday != null && !sbirthday.equals("=")) {
                if ((sbirthday.startsWith(">=") || sbirthday.startsWith("<="))) {
                    f = " Birthday " + sbirthday.substring(0, 2) + "\"" + sbirthday.substring(2) + "\"";
                } else if ((sbirthday.startsWith(">") || sbirthday.startsWith("<") || sbirthday.startsWith("="))) {
                    f = " Birthday " + sbirthday.substring(0, 1) + "\"" + sbirthday.substring(1) + "\"";
                }
            }
        }
        if ((!a.equals("") || !b.equals("") || !c.equals("") || !d.equals("") || !e.equals("")) && phonenumber.length() != 0) {
            g = " AND Phonenumber LIKE ?";
        } else if (a.equals("") && b.equals("") && c.equals("") && d.equals("") && e.equals("") && phonenumber.length() != 0) {
            g = " Phonenumber LIKE ?";
        }
        if ((!a.equals("") || !b.equals("") || !c.equals("") || !d.equals("") || !e.equals("") || !f.equals("")) && address.length() != 0) {
            h = " AND Address LIKE ?";
        } else if (a.equals("") && b.equals("") && c.equals("") && d.equals("") && e.equals("") && f.equals("") && address.length() != 0) {
            h = " Address LIKE ?";
        }

        String QUERY_SQL = "SELECT * FROM customer WHERE" + a + b + c + d + e + f + g + h;
        PreparedStatement ps = con.prepareStatement(QUERY_SQL);

        if (!a.equals("")) {
            ps.setInt(++index, serialnumber);
        }
        if (!b.equals("")) {
            ps.setString(++index, "%" + firstname + "%");
        }
        if (!c.equals("")) {
            ps.setString(++index, "%" + lastname + "%");
        }
        if (!d.equals("")) {
            ps.setInt(++index, gender);
        }
        if (!e.equals("")) {
            ps.setDate(++index, birthday);
        }
        if (!g.equals("")) {
            ps.setString(++index, "%" + phonenumber + "%");
        }
        if (!h.equals("")) {
            ps.setString(++index, "%" + address + "%");
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int csn = rs.getInt("Serialnumber");
            String cfirstname = rs.getString("Firstname");
            String clastname = rs.getString("Lastname");
            int cgender = rs.getInt("Gender");
            Date cbirthday = rs.getDate("BirthDay");
            String cphonenumber = rs.getString("Phonenumber");
            String caddress = rs.getString("Address");

            result.add(new Customer(csn, cfirstname, clastname, cgender, cbirthday, cphonenumber, caddress));
        }
        return result;
    }

    @Override
    public List<Customer> getAll() throws SQLException {
        List<Customer> result = new ArrayList<>();

        String CUSTOMER_SQL = "SELECT * FROM customer";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(CUSTOMER_SQL);

        while (rs.next()) {
            int csn = rs.getInt("Serialnumber");
            String firstname = rs.getString("Firstname");
            String lastname = rs.getString("Lastname");
            int gender = rs.getInt("Gender");
            Date birthday = rs.getDate("BirthDay");
            String phonenumber = rs.getString("Phonenumber");
            String address = rs.getString("Address");

            Customer customer = new Customer(csn, firstname, lastname, gender, birthday, phonenumber, address);
            result.add(customer);
        }
        return result;
    }
}
