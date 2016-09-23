<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<table border="0">
    <tr>
        <th>顧客序號：</th>
        <td>${customer.serialnumber}</td>
    </tr>
    <tr>
        <th>名字：</th>
        <td>${customer.firstname}</td>
    </tr>
    <tr>
        <th>姓氏：</th>
        <td>${customer.lastname}</td>
    </tr>
    <tr>
        <th>性別：</th>
        <td>${customer.gender == 1 ? "先生" : "小姐"}</td>
    </tr>
    <tr>
        <th>生日：</th>
    <td><fmt:formatDate pattern="yyyy-MM-dd" var="date" value="${customer.birthday}" type="date"/>${date}</td>
    </tr>
    <tr>
        <th>電話：</th>
        <td>${customer.phonenumber}</td>
    </tr>
    <tr>
        <th>地址：</th>
        <td>${customer.address}</td>
    </tr>
</table>
    <button id="smallbutton" onclick="window.location.href='/RCSMS_Web/customer/Add.jsp'">繼續新增</button>
<!--    <form action="/customer/Add.jsp" method="POST">
        <input id="smallbutton" type="submit" value="新增下一筆"/>
    </form>-->