package net.rcsms.listeners;

import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import net.rcsms.dao.CustomerJdbcDao;
import net.rcsms.dao.DeviceJdbcDao;
import net.rcsms.dao.DeviceTypeJdbcDao;
import net.rcsms.dao.LinkJdbcDao;
import net.rcsms.dao.RecordJdbcDao;
import net.rcsms.dao.SNRecordJdbcDao;

public class RcsmsServletContextListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try{
            CustomerJdbcDao cd = new CustomerJdbcDao();
            DeviceJdbcDao dd = new DeviceJdbcDao();
            DeviceTypeJdbcDao dtd = new DeviceTypeJdbcDao();
            LinkJdbcDao ld = new LinkJdbcDao();
            RecordJdbcDao rd = new RecordJdbcDao();
            SNRecordJdbcDao snrd = new SNRecordJdbcDao();
            
            ServletContext sc = sce.getServletContext();
            sc.setAttribute("customerJdbcDao", cd);
            sc.setAttribute("deviceJdbcDao", dd);
            sc.setAttribute("deviceTypeJdbcDao", dtd);
            sc.setAttribute("linkJdbcDao", ld);
            sc.setAttribute("recordJdbcDao", rd);
            sc.setAttribute("sNRecordJdbcDao", snrd);
            
            sc.log("MyServletContextListener.contextInitialized: customerJdbcDao, "
                    + "deviceJdbcDao, deviceTypeJdbcDao, linkJdbcDao, recordJdbcDao, sNRecordJdbcDao created successful!");
                    }
        catch(SQLException e){
            sce.getServletContext().log("MyServletContextListener.contextInitialized:", e);
        }
        catch(Exception e){
            System.out.println("============================"+e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        
        CustomerJdbcDao md = (CustomerJdbcDao)sc.getAttribute("customerJdbcDao");
        DeviceJdbcDao dd = (DeviceJdbcDao)sc.getAttribute("deviceJdbcDao");
        DeviceTypeJdbcDao dtd = (DeviceTypeJdbcDao)sc.getAttribute("deviceTypeJdbcDao");
        LinkJdbcDao ld = (LinkJdbcDao)sc.getAttribute("linkJdbcDao");
        RecordJdbcDao rd = (RecordJdbcDao)sc.getAttribute("recordJdbcDao");
        SNRecordJdbcDao snrd = (SNRecordJdbcDao)sc.getAttribute("sNRecordJdbcDao");
        sce.getServletContext().log("MyServletContextListener.contextInitialized: customerJdbcDao, "
                    + "deviceJdbcDao, deviceTypeJdbcDao, linkJdbcDao, recordJdbcDao, sNRecordJdbcDao disconnected!");
        try{
            md.close();
            dd.close();
            dtd.close();
            ld.close();
            rd.close();
            snrd.close();
        }
        catch(Exception e){
            sce.getServletContext().log("MyServletContextListener.contextDestroyed:", e);
        }
    }
}
