package net.rcsms.servlet.customer;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class QueryCustomerServlet extends HttpServlet {
    
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
        
        int serialnumber = 0;
        int gender = 0;
        String result = null;

        ServletContext context = request.getServletContext();
        CustomerJdbcDao cd = (CustomerJdbcDao) context.getAttribute("customerJdbcDao");
        List<String> errorMessages = new ArrayList<>();
        String sserialnumber = request.getParameter("serialnumber");
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String sgender = request.getParameter("gender");
        String sbirthday = request.getParameter("birthday");
        String phonenumber = request.getParameter("phonenumber");
        String address = request.getParameter("address");
        if(sserialnumber.equals("") && firstname.equals("") && lastname.equals("") && sgender == null
                && sbirthday.equals("") && phonenumber.equals("") && address.equals("")){
            errorMessages.add("請輸入至少一項搜尋條件。");
        }
        
        Pattern snp = Pattern.compile("[^0-9]");
        Matcher snm = snp.matcher(sserialnumber);
        
        if(!sserialnumber.equals("")){
            if(snm.find()){
                errorMessages.add("序號格式錯誤。");
            }
            else{
                serialnumber = Integer.parseInt(sserialnumber);
                if(serialnumber == 0){
                    errorMessages.add("序號不能為 0。");
                }
            }
        }
        
        Pattern np = Pattern.compile("[0-9%!?&+'\"?<>/\\\\*$=#-@_,.\\s]");
        Matcher fnm = np.matcher(firstname);
        Matcher lnm = np.matcher(lastname);
        
        if(fnm.find()){
            errorMessages.add("\"名字\"請勿包含數字或符號。");
        }
        if(lnm.find()){
            errorMessages.add("\"姓氏\"請勿包含數字或符號。");
        }
        
        if(sgender != null){
            gender = Integer.parseInt(sgender);
        }
        
        Pattern sbip = Pattern.compile("[^0-9\\>\\<\\=\\-]");
        Matcher sbim = sbip.matcher(sbirthday);
        int hyphencount = sbirthday.length() - sbirthday.replace("-", "").length();
        if(sbirthday.length() != 0){
            if(sbim.find()){
                errorMessages.add("\" " + sbim.group()+ " \" 不應出現在生日欄位。");
            }
            if(hyphencount > 2){
                errorMessages.add("\"-\"不應多於兩個。");
            }
            if(sbirthday.length() == hyphencount || dateParse(sbirthday) == null){
                errorMessages.add("日期格式錯誤。");
            }
        }
        else if(sbirthday.length() == 0){
            sbirthday = "="; //為了方便判斷。
        }
        
        Pattern pnp = Pattern.compile("[^0-9\\+-]");
        Matcher pnm = pnp.matcher(phonenumber);
        
        if(pnm.find() && phonenumber.length() != 0){
            errorMessages.add("電話號碼格式錯誤。");
        }
        
        Pattern ap = Pattern.compile("[%!?'\"?<>/\\\\*$=#@_\\s]");
        Matcher am = ap.matcher(address);
        
        if(am.find() && address.length() != 0){
            errorMessages.add("地址格式錯誤。");
        }
        
        if(errorMessages.isEmpty()){
            try{
                List<Customer> customerlist = cd.query(serialnumber, firstname, lastname, gender, sbirthday, phonenumber, address);
                if(!customerlist.isEmpty()){
                    result = "true";
                    request.setAttribute("result", result);
                    request.setAttribute("customerlist", customerlist);
                    RequestDispatcher rd = request.getRequestDispatcher("/customer/Query.jsp");
                    rd.forward(request, response);
                }
                else{
                    errorMessages.add("沒有搜尋到顧客資料。");
                    request.setAttribute("result", result);
                    request.setAttribute("errorMessages", errorMessages);
                    RequestDispatcher rd = request.getRequestDispatcher("/customer/Query.jsp");
                    rd.forward(request, response);
                }
            }
            catch(SQLException e){
                log(e.getMessage());
                errorMessages.add("查詢失敗。");
                request.setAttribute("errorMessages", errorMessages);
                RequestDispatcher rd = request.getRequestDispatcher("/customer/Query.jsp");
                rd.forward(request, response);
            }
        }
        else{
            request.setAttribute("errorMessages", errorMessages);
            RequestDispatcher rd = request.getRequestDispatcher("/customer/Query.jsp");
            rd.forward(request, response);
        }
    }
}