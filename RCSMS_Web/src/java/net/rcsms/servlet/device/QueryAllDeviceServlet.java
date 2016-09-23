package net.rcsms.servlet.device;

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
import net.rcsms.dao.DeviceJdbcDao;
import net.rcsms.domain.Device;

public class QueryAllDeviceServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        DeviceJdbcDao djd = (DeviceJdbcDao)context.getAttribute("deviceJdbcDao");
        List<Device> devicelist  = new ArrayList<Device>();
        try{
            devicelist = djd.getAll();
            request.setAttribute("devicelist", devicelist);
            RequestDispatcher rd = request.getRequestDispatcher("/device/QueryAll.jsp");
            rd.forward(request, response);
        }
        catch(SQLException e){
            log(e.getMessage());
        }
    }
}
