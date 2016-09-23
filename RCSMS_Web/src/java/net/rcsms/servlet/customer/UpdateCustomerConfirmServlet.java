package net.rcsms.servlet.customer;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

public class UpdateCustomerConfirmServlet extends HttpServlet {
    
    private Date dateParse(String datestring){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date birthday = null;
        
        try{
            sdf.setLenient(false);
            birthday = sdf.parse(datestring);
        }
        catch(ParseException e){
            log(e.getMessage());
        }
        return birthday;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        ServletContext context = request.getServletContext();
        CustomerJdbcDao md = (CustomerJdbcDao) context.getAttribute("customerJdbcDao");
        List<String> errorMessages = new ArrayList<>();
        int result = 1;
        
        String sserialnumber = request.getParameter("serialnumber");
        int serialnumber = Integer.parseInt(sserialnumber);
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String sgender = request.getParameter("gender");
        int gender = Integer.parseInt(sgender);
        String sbirthday = request.getParameter("birthday");
        String phonenumber = request.getParameter("phonenumber");
        String address = request.getParameter("address");
        
        Pattern np = Pattern.compile("[0-9%!?&+'\"?<>/\\\\*$=#-@_,.\\s]");
        Matcher fnm = np.matcher(firstname);
        Matcher lnm = np.matcher(lastname);
        
        if(firstname.length() == 0){
            errorMessages.add("請輸入\"名字\"。");
        } else if(fnm.find()){
            errorMessages.add("\"名字\"請勿包含數字或符號。");
        }
        if(lastname.length() == 0){
            errorMessages.add("請輸入\"姓氏\"。");
        } else if(lnm.find()){
            errorMessages.add("\"姓氏\"請勿包含數字或符號。");
        }
        
        Date birthday = dateParse(sbirthday);
        if(sbirthday.length() == 0){
            errorMessages.add("請輸入生日。 (年-月-日)");
        }else if(birthday == null || birthday.after(Calendar.getInstance().getTime())){
            errorMessages.add("日期格式錯誤。 (年-月-日)");
        }
        
        Pattern pnp = Pattern.compile("[^0-9\\+-]");
        Matcher pnm = pnp.matcher(phonenumber);
        
        if(phonenumber.length() == 0){
            errorMessages.add("請輸入電話號碼。");
        } else if(pnm.find()){
            errorMessages.add("電話號碼格式錯誤。");
        }
        
        Pattern ap = Pattern.compile("[%!?'\"?<>/\\\\*$=#@_\\s]");
        Matcher am = ap.matcher(address);
        
        if(address.length() == 0){
            errorMessages.add("請輸入地址。");
        } else if(am.find()){
            errorMessages.add("地址格式錯誤。");
        }
        
        if(errorMessages.isEmpty()){
            Customer customer = new Customer(serialnumber, firstname, lastname, gender, birthday, phonenumber, address);
            try{
                md.update(customer);
                result = 2;
                request.setAttribute("result", result);
                request.setAttribute("customer", customer);
                RequestDispatcher rd = request.getRequestDispatcher("/customer/Update.jsp");
                rd.forward(request, response);
            }
            catch(SQLException e){
                log(e.getMessage());
            }
        }   
        else{
            request.setAttribute("serialnumber", serialnumber);
            request.setAttribute("result", result);
            request.setAttribute("errorMessages", errorMessages);
            RequestDispatcher rd = request.getRequestDispatcher("/customer/Update.jsp");
            rd.forward(request, response);
        }
    }
}
