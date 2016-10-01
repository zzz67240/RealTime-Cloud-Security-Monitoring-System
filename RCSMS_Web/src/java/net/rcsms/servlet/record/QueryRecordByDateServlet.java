package net.rcsms.servlet.record;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.rcsms.dao.RecordJdbcDao;
import net.rcsms.domain.Record;

public class QueryRecordByDateServlet extends HttpServlet {
    
    private Date dateParse(String datestring){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        
        try{
            sdf.setLenient(false);
            date = sdf.parse(datestring);
        }
        catch(ParseException e){
            log(e.getMessage());
        }
        return date;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String result = null;

        ServletContext context = request.getServletContext();
        RecordJdbcDao rjd = (RecordJdbcDao) context.getAttribute("recordJdbcDao");
        List<String> errorMessages = new ArrayList<>();
        String sdate1 = request.getParameter("date1");
        String sdate2 = request.getParameter("date2");
        
        Date date1 = dateParse(sdate1);
        int hyphencount1 = sdate1.length() - sdate1.replace("-", "").length();
        if(sdate1.length() == 0){
            errorMessages.add("請輸入起始日期。 (例：2016-01-01) 個位數請補 0。");
        }else if(sdate1.length() == hyphencount1 || date1 == null || sdate1.length() != 10){
            errorMessages.add("起始日期格式錯誤。 (例：2016-01-01) 個位數請補 0。");
        }else if(date1.after(Calendar.getInstance().getTime())){
            errorMessages.add("起始日期不得晚於今日。 (例：2016-01-01) 個位數請補 0。");
        }
        
        Date date2 = dateParse(sdate2);
        int hyphencount2 = sdate2.length() - sdate2.replace("-", "").length();
        if(sdate2.length() == 0){
            errorMessages.add("請輸入結束日期。 (例：2016-01-01) 個位數請補 0。");
        }else if(sdate2.length() == hyphencount2 || date2 == null || sdate2.length() != 10){
            errorMessages.add("結束日期格式錯誤。 (例：2016-01-01) 個位數請補 0。");
        }
        
        if(date1 != null & date2 != null && date1.after(date2)){
            errorMessages.add("起始日期不得晚於結束日期。 (例：2016-01-01) 個位數請補 0。");
        }
        
        if(errorMessages.isEmpty()){
            try{
                List<Record> recordlist = rjd.QueryByDate(sdate1, sdate2);
                if(!recordlist.isEmpty()){
                    result = "true";
                    request.setAttribute("result", result);
                    request.setAttribute("recordlist", recordlist);
                    RequestDispatcher rd = request.getRequestDispatcher("/record/QueryByDate.jsp");
                    rd.forward(request, response);
                }
                else{
                    errorMessages.add("沒有搜尋到記錄資料。");
                    request.setAttribute("result", result);
                    request.setAttribute("errorMessages", errorMessages);
                    RequestDispatcher rd = request.getRequestDispatcher("/record/QueryByDate.jsp");
                    rd.forward(request, response);
                }
            }
            catch(SQLException e){
                log(e.getMessage());
                errorMessages.add("查詢失敗。");
                request.setAttribute("errorMessages", errorMessages);
                RequestDispatcher rd = request.getRequestDispatcher("/record/QueryByDate.jsp");
                rd.forward(request, response);
            }
        }
        else{
            request.setAttribute("errorMessages", errorMessages);
            RequestDispatcher rd = request.getRequestDispatcher("/record/QueryByDate.jsp");
            rd.forward(request, response);
        }
    }
}