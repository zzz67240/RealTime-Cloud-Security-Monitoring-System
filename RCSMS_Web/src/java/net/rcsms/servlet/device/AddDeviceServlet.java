package net.rcsms.servlet.device;

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
import net.rcsms.dao.DeviceJdbcDao;
import net.rcsms.dao.LinkJdbcDao;
import net.rcsms.dao.SNRecordJdbcDao;
import net.rcsms.domain.Device;

public class AddDeviceServlet extends HttpServlet {
    
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
        DeviceJdbcDao djd = (DeviceJdbcDao) context.getAttribute("deviceJdbcDao");
        CustomerJdbcDao cd = (CustomerJdbcDao) context.getAttribute("customerJdbcDao");
        LinkJdbcDao ld = (LinkJdbcDao) context.getAttribute("linkJdbcDao");
        SNRecordJdbcDao snjd = (SNRecordJdbcDao) context.getAttribute("sNRecordJdbcDao");
        String scustomerserialnumber = request.getParameter("customerserialnumber");
        String sdevicetype = request.getParameter("devicetype");
        String sproductiondate = request.getParameter("productiondate");
        String spurchasedate = request.getParameter("purchasedate");
        String swarrantydate = request.getParameter("warrantydate");
        
        String result = null;
        int customerserialnumber = 0;
        
        List<String> errorMessages = new ArrayList<>();
        
        int devicetype = Integer.parseInt(sdevicetype);
        Date productiondate = dateParse(sproductiondate);
        int sproductiondatehyphencount = sproductiondate.length() - sproductiondate.replace("-", "").length();
        Date purchasedate = dateParse(spurchasedate);
        int spurchasedatehyphencount = spurchasedate.length() - spurchasedate.replace("-", "").length();
        Date warrantydate = dateParse(swarrantydate);
        int swarrantydatehyphencount = swarrantydate.length() - swarrantydate.replace("-", "").length();
        
        try{
            Pattern snp = Pattern.compile("[^0-9]");
            Matcher snm = snp.matcher(scustomerserialnumber);
            
            if(scustomerserialnumber.equals("")){
                errorMessages.add("請輸入顧客序號。");
            } else if(snm.find()){
                errorMessages.add("顧客序號格式只能為純數字。");
            } else {
                customerserialnumber = Integer.parseInt(scustomerserialnumber);
            } 
            if(cd.get(customerserialnumber) == null){
                errorMessages.add("該顧客序號不存在。");
            }
        }
        catch(SQLException e){
            log(e.getMessage());
        }
        catch(NumberFormatException e){
            errorMessages.add("顧客序號格式只能為純數字。");
            log(e.getMessage());
        }
        
        if(sproductiondate.length() == 0){
            errorMessages.add("請輸入生產日期。 (年-月-日)");
        } else if(sproductiondate.length() == sproductiondatehyphencount || productiondate == null){
            errorMessages.add("生產日期錯誤。 (年-月-日)");
        } else if((purchasedate != null && productiondate.after(purchasedate))){
            errorMessages.add("生產日期不得晚於購買日期。 (年-月-日)");
        }
        
        if(spurchasedate.length() == 0){
            errorMessages.add("請輸入購買日期。 (年-月-日)");
        } else if(spurchasedate.length() == spurchasedatehyphencount || purchasedate == null){
            errorMessages.add("購買日期錯誤。 (年-月-日)");
        }
        
        if(swarrantydate.length() == 0){
            errorMessages.add("請輸入保固日期。 (年-月-日)");
        } else if(swarrantydate.length() == swarrantydatehyphencount || warrantydate == null){
            errorMessages.add("保固日期錯誤。 (年-月-日)");
        } else if(warrantydate.before(Calendar.getInstance().getTime())){
            errorMessages.add("保固日期不得早於今日。 (年-月-日)");
        } else if((purchasedate != null && warrantydate.before(purchasedate))){
            errorMessages.add("保固日期不得早於購買日期。 (年-月-日)");
        }
        
        if(errorMessages.isEmpty()){
            try{
                String deviceSN = String.format("D%04d", snjd.getSN("device"));
                Device device = new Device(deviceSN, devicetype, productiondate, purchasedate, warrantydate);
                if(djd.add(device)){
                    snjd.setSN("device");
                    ld.add(customerserialnumber, deviceSN);
                }
                result = "true";
                request.setAttribute("result", result);
                request.setAttribute("customerserialnumber", customerserialnumber);
                request.setAttribute("device", device);
                request.setAttribute("devicetype", djd.getDevicetype(devicetype));
                RequestDispatcher rd = request.getRequestDispatcher("/device/Add.jsp");
                rd.forward(request, response);
            }
            catch(SQLException e){
                log(e.getMessage());
            }
        }   
        else{
            request.setAttribute("result", result);
            request.setAttribute("errorMessages", errorMessages);
            RequestDispatcher rd = request.getRequestDispatcher("/device/Add.jsp");
            rd.forward(request, response);
        }
    }
}