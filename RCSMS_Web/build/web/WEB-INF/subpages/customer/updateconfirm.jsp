<%@page contentType="text/html" pageEncoding="UTF-8" %>
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
<form action="/RCSMS_Web/customer/updateconfirm.do" method="POST">
    <table border="0">
        <tr>
            <th>顧客序號：</th>
            <td>${param.serialnumber}<input name="serialnumber" type="hidden" value="${param.serialnumber}"></td>
        </tr>
        <tr>
            <th>名字：</th>
            <td><input name="firstname" type="text" value="${customer.firstname}${param.firstname}"></td>
        </tr>
        <tr>
            <th>姓氏：</th>
            <td><input name="lastname" type="text" value="${customer.lastname}${param.lastname}"></td>
        </tr>
        <tr>
            <th>性別：</th>
            <td><input name="gender" type="radio" value="1" ${customer.gender == 1 ? "checked" : ""} ${param.gender == 1 ? "checked" : ""}>先生
                <input name="gender" type="radio" value="2" ${customer.gender == 2 ? "checked" : ""} ${param.gender == 2 ? "checked" : ""}>小姐</td>
        </tr>
        <tr>
            <th>生日：</th>
            <td><input name="birthday" type="text" value="${customer.birthday}${param.birthday}"></td>
        </tr>
        <tr>
            <th>電話：</th>
            <td><input name="phonenumber" type="text" value="${customer.phonenumber}${param.phonenumber}"></td>
        </tr>
        <tr>
            <th>地址：</th>
            <td><input name="address" type="text" value="${customer.address}${param.address}"></td>
        </tr>
        <tr>
            <td><input id="smallbutton" type="submit" value="確認變更"/></td>
        </tr>
    </table>
</form>