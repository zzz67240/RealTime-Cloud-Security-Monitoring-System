<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.List"%>
<% 
    List<String> errorMessages = (List<String>)
            request.getAttribute("errorMessages");
    if (errorMessages != null) {
        out.println("<ul>");

        for (String message : errorMessages) {
            out.println("<li>" + message + "</li>");
        }

        out.println("</ul>");
    } 
%>
<form action="/RCSMS_Web/device/add.do" method="POST">
    <table border="0">
        <tr>
            <th>顧客序號：</th>
            <td><input name="customerserialnumber" type="text" value="${param.customerserialnumber}"></td>
        </tr>
        <tr>
            <th>裝置型號：</th>
            <td><select name="devicetype">
                <c:forEach var="dt" items="${deviceTypeJdbcDao.all}">
                    <option value="${dt.serialnumber}">${dt.type}</option>
                </c:forEach>
            </select></td>
        </tr>
        <tr>
            <th>生產日期：</th>
            <td><input name="productiondate" type="text" value="${param.productiondate}"></td>
        </tr>
        <tr>
            <th>購買日期：</th>
            <td><input name="purchasedate" type="text" value="${param.purchasedate}"></td>
        </tr>
        <tr>
            <th>保固日期：</th>
            <td><input name="warrantydate" type="text" value="${param.warrantydate}"></td>
        </tr>
        <tr>
            <td><input id="smallbutton" type="submit" value="送出"/></td>
        </tr>
    </table>
</form>