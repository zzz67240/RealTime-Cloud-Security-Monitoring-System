<%@page import="net.rcsms.domain.Device"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<html>
    <head>
        <title>即時雲端安全監控系統 Real-Time Cloud Security Monitoring System</title>
        <link rel="stylesheet" href="/RCSMS_Web/style.css" type="text/css">
    </head>
    <body>
        <table cellpadding="0" cellspacing="0" width="80%">
            <tr>
                <td width="25%" align="center"><a href="/RCSMS_Web/index.jsp"/><img id="logo" src="/RCSMS_Web/images/logo.png"/></td>
                <td width="75%" align="center">
                    <jsp:include page="/WEB-INF/views/banner.jsp">
                        <jsp:param name="subtitle" value="查詢所有裝置資料"/>
                    </jsp:include>
                </td>
            </tr>
            <tr>
                <td width="25%">
                    <jsp:include page="/WEB-INF/views/devicefunction.jsp"/>
                </td>
                <td width="75%" align="center">
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
                </td>
            </tr>
            <tr>
                <td width="25%"></td>
                <td width="75%" align="center">
                    <jsp:include page="/WEB-INF/views/contact.jsp"/>
                </td>
            </tr>
        </table>
    </body>
</html>
