package net.rcsms.servlet.record;

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
import net.rcsms.dao.RecordJdbcDao;
import net.rcsms.domain.Record;

public class QueryRecordByDSNServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int serialNumber = 0;
        String result = null;

        ServletContext context = request.getServletContext();
        RecordJdbcDao rjd = (RecordJdbcDao) context.getAttribute("recordJdbcDao");
        String errorMessage = null;
        String sdeviceserialnumber = request.getParameter("deviceserialnumber");
        
        Pattern dsnp = Pattern.compile("[^0-9]");
        Matcher dsnm = dsnp.matcher(sdeviceserialnumber);
        
        if(sdeviceserialnumber.length() == 0){
            errorMessage = "請輸入裝置序號。";
        } else if(dsnm.find()){
            errorMessage = "裝置序號格式錯誤。";
        } else{
            serialNumber = Integer.parseInt(sdeviceserialnumber);
            if(serialNumber < 1 || serialNumber > 999){
                errorMessage = "目前設定裝置序號不能小於 1 或者大於 999。";
            }
        }
        
        if(errorMessage == null){
            try{
                List<Record> recordlist = rjd.QueryByDSN(serialNumber);
                if(!recordlist.isEmpty()){
                    result = "true";
                    request.setAttribute("result", result);
                    request.setAttribute("recordlist", recordlist);
                    RequestDispatcher rd = request.getRequestDispatcher("/record/QueryByDSN.jsp");
                    rd.forward(request, response);
                }
                else{
                    errorMessage = "沒有搜尋到記錄資料。";
                    request.setAttribute("result", result);
                    request.setAttribute("errorMessage", errorMessage);
                    RequestDispatcher rd = request.getRequestDispatcher("/record/QueryByDSN.jsp");
                    rd.forward(request, response);
                }
            }
            catch(SQLException e){
                log(e.getMessage());
                errorMessage = "查詢失敗。";
                request.setAttribute("errorMessage", errorMessage);
                RequestDispatcher rd = request.getRequestDispatcher("/record/QueryByDSN.jsp");
                rd.forward(request, response);
            }
        }
        else{
            request.setAttribute("errorMessage", errorMessage);
            RequestDispatcher rd = request.getRequestDispatcher("/record/QueryByDSN.jsp");
            rd.forward(request, response);
        }
    }
}