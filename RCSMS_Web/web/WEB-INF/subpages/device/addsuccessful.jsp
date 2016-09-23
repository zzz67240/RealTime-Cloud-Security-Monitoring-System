<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<table border="0">
    <tr>
        <th>裝置序號：</th>
        <td>${device.serialnumber}</td>
    </tr>
    <tr>
        <th>購買人序號：</th>
        <td>${customerserialnumber}</td>
    </tr>
    <tr>
        <th>裝置型號：</th>
        <td>${devicetype}</td>
    </tr>
    <tr>
        <th>生產日期：：</th>
        <td><fmt:formatDate pattern="yyyy-MM-dd" var="date" value="${device.productiondate}" type="date"/>${date}</td>
    </tr>
    <tr>
        <th>購買日期：</th>
        <td><fmt:formatDate pattern="yyyy-MM-dd" var="date" value="${device.purchasedate}" type="date"/>${date}</td>
    </tr>
    <tr>
        <th>保固日期：</th>
        <td><fmt:formatDate pattern="yyyy-MM-dd" var="date" value="${device.warrantydate}" type="date"/>${date}</td>
    </tr>
</table>
    <button id="smallbutton" onclick="window.location.href='/RCSMS_Web/device/Add.jsp'">繼續新增</button>
<!--    <form action="/device/Add.jsp" method="POST">
        <input id="smallbutton" type="submit" value="新增下一筆"/>
    </form>-->