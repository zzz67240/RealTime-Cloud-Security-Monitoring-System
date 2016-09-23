<%@page contentType="text/html" pageEncoding="UTF-8" %>
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
        <td>${customer.gender}</td>
    </tr>
    <tr>
        <th>生日：</th>
        <td>${customer.birthday}</td>
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
    <button id="smallbutton" onclick="window.location.href='/RCSMS_Web/customer/Delete.jsp'">繼續刪除</button>
<!--    <form action="DeleteMember.jsp" method="POST">
        <input id="smallbutton" type="submit" value="Delete Another"/>
    </form>-->