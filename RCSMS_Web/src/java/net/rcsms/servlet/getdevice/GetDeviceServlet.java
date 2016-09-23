package net.rcsms.servlet.getdevice;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.rcsms.dao.LinkJdbcDao;

public class GetDeviceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        ServletContext context = request.getServletContext();
        PrintWriter out = response.getWriter();
        LinkJdbcDao ld = (LinkJdbcDao) context.getAttribute("linkJdbcDao");
        int customerserialnumber = Integer.parseInt(request.getParameter("customerserialnumber"));
        StringBuffer result = new StringBuffer();
        try {
            List<String> snl = ld.deviceSerialNumberQuery(customerserialnumber);
            if(snl.isEmpty()){
                result.append("NA");
                out.print(result.toString());
            } else if(snl.size() == 1) {
                result.append(snl.get(0));
                out.print(result.toString());
            } else{
                for(int i = 0; i < snl.size(); i++){
                    result.append(snl.get(i));
                    if(i < (snl.size()-1)){
                        result.append(",");
                    }
                }
                out.print(result.toString());
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
    
}
