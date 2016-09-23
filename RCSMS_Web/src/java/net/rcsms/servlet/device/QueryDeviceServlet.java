package net.rcsms.servlet.device;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.rcsms.dao.DeviceJdbcDao;
import net.rcsms.domain.Device;

public class QueryDeviceServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int customerserialnumber = 0;
        String result = null;

        ServletContext context = request.getServletContext();
        DeviceJdbcDao djd = (DeviceJdbcDao) context.getAttribute("deviceJdbcDao");
        String scustomerserialnumber = request.getParameter("customerserialnumber");
        String errorMessage = null;
        
        Pattern snp = Pattern.compile("[^0-9]");
        Matcher snm = snp.matcher(scustomerserialnumber);
        
        if(scustomerserialnumber.equals("")){
            errorMessage = "請輸入顧客序號。";
        } else if(snm.find()){
            errorMessage = "顧客序號格式錯誤。";
        } else{
            customerserialnumber = Integer.parseInt(scustomerserialnumber);
            if(customerserialnumber == 0){
                errorMessage = "顧客序號不能為 0。";
            }
        }
        
        
        if(errorMessage == null){
            try{
                List<Device> devicelist = djd.query(customerserialnumber);
                if(!devicelist.isEmpty()){
                    result = "true";
                    request.setAttribute("result", result);
                    request.setAttribute("devicelist", devicelist);
                    RequestDispatcher rd = request.getRequestDispatcher("/device/Query.jsp");
                    rd.forward(request, response);
                }
                else{
                    errorMessage = "沒有搜尋到裝置資料，請確認顧客序號的有效性。";
                    request.setAttribute("result", result);
                    request.setAttribute("errorMessage", errorMessage);
                    RequestDispatcher rd = request.getRequestDispatcher("/device/Query.jsp");
                    rd.forward(request, response);
                }
            }
            catch(SQLException e){
                log(e.getMessage());
                errorMessage = "查詢失敗。";
                request.setAttribute("errorMessage", errorMessage);
                RequestDispatcher rd = request.getRequestDispatcher("/device/Query.jsp");
                rd.forward(request, response);
            }
        }
        else{
            request.setAttribute("errorMessage", errorMessage);
            RequestDispatcher rd = request.getRequestDispatcher("/device/Query.jsp");
            rd.forward(request, response);
        }
    }
}