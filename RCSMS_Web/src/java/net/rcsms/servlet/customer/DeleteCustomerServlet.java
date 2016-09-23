package net.rcsms.servlet.customer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.rcsms.dao.CustomerJdbcDao;
import net.rcsms.domain.Customer;

public class DeleteCustomerServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String sserialnumber = request.getParameter("serialnumber");
        String errorMessage = null;
        String result = null;
        
        ServletContext context = request.getServletContext();
        CustomerJdbcDao cd = (CustomerJdbcDao) context.getAttribute("customerJdbcDao");
        try{
            Pattern snp = Pattern.compile("[^0-9]");
            Matcher snm = snp.matcher(sserialnumber);
            int serialnumber = Integer.parseInt(sserialnumber);
            
            if(snm.find()){
                errorMessage = "序號格式只能為純數字。";
                request.setAttribute("result", result);
                request.setAttribute("errorMessage", errorMessage);
                RequestDispatcher rd = request.getRequestDispatcher("/customer/Delete.jsp");
                rd.forward(request, response);
            }
            else if(cd.get(serialnumber) == null){
                errorMessage = "該序號不存在。";
                request.setAttribute("result", result);
                request.setAttribute("errorMessage", errorMessage);
                RequestDispatcher rd = request.getRequestDispatcher("/customer/Delete.jsp");
                rd.forward(request, response);
            }
            else if(errorMessage == null){
                Customer customer = cd.get(serialnumber);
                result = "true";
                request.setAttribute("result", result);
                request.setAttribute("customer", customer);
                cd.delete(serialnumber);
                RequestDispatcher rd = request.getRequestDispatcher("/customer/Delete.jsp");
                rd.forward(request, response);
            }
        }
        catch(SQLException e){
            log(e.getMessage());
        }
        catch(NumberFormatException e){
            errorMessage = "序號格式只能為純數字。";
            request.setAttribute("result", result);
            request.setAttribute("errorMessage", errorMessage);
            RequestDispatcher rd = request.getRequestDispatcher("/customer/Delete.jsp");
            rd.forward(request, response);
            log(e.getMessage());
        }
    }
}
