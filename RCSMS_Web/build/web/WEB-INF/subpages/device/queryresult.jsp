<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="java.util.List"%>
<%@page import="net.rcsms.domain.Device"%>
<%
    out.println("<table border='1'>");
    out.println("<tr><th>序號</th><th>型號</th><th>生產日期</th>"
            + "<th>購買日期</th><th>保固日期</th></tr>");
    List<Device> devicelist = (List<Device>) request.getAttribute("devicelist");
    if (devicelist != null) {
        for (Device device : devicelist) {
            out.println("<tr><td>" + device.getSerialnumber()+ "</td><td>" + device.getSdevicetype()
                    + "</td><td>" + device.getProductiondate() + "</td><td>" + device.getPurchasedate()
                    + "</td><td>" + device.getWarrantydate() + "</td></tr>");
        }
        out.println("</table>");
    } 
%>
<br/>
<button id="smallbutton" onclick="window.location.href='/RCSMS_Web/device/Query.jsp'">繼續查詢</button>
<!--<form action="QueryMember.jsp" method="POST">
    <input id="smallbutton" type="submit" value="Query Another"/>
</form>-->