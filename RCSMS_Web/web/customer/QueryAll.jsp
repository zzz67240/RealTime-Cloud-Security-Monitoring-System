<%@page import="net.rcsms.domain.Customer"%>
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
                        <jsp:param name="subtitle" value="查詢所有顧客資料"/>
                    </jsp:include>
                </td>
            </tr>
            <tr>
                <td width="25%">
                    <jsp:include page="/WEB-INF/views/customerfunction.jsp"/>
                </td>
                <td width="75%" align="center">
                    <%
                        out.println("<table border='1'>");
                        out.println("<tr><th>序號</th><th>名字</th><th>姓氏</th><th>性別</th>"
                                + "<th>生日</th><th>電話</th><th>地址</th></tr>");
                        List<Customer> customerlist = (List<Customer>) request.getAttribute("customerlist");
                        if (customerlist != null) {
                            for (Customer customer : customerlist) {
                                out.println("<tr><td>" + customer.getSerialnumber()+ "</td><td>" + customer.getFirstname() 
                                        + "</td><td>" + customer.getLastname() + "</td><td>" + (customer.getGender() == 1 ? "先生" : "小姐")
                                        + "</td><td>" + customer.getBirthday() + "</td><td>" + customer.getPhonenumber()
                                        + "</td><td>" + customer.getAddress() + "</td></tr>");
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
