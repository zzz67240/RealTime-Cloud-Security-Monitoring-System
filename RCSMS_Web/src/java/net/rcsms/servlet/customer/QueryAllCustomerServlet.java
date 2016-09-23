package net.rcsms.servlet.customer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.rcsms.dao.CustomerJdbcDao;
import net.rcsms.domain.Customer;

public class QueryAllCustomerServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        CustomerJdbcDao cd = (CustomerJdbcDao)context.getAttribute("customerJdbcDao");
        List<Customer> customerlist  = new ArrayList<Customer>();
        try{
            customerlist = cd.getAll();
            request.setAttribute("customerlist", customerlist);
            RequestDispatcher rd = request.getRequestDispatcher("/customer/QueryAll.jsp");
            rd.forward(request, response);
        }
        catch(SQLException e){
            log(e.getMessage());
        }
    }
}
