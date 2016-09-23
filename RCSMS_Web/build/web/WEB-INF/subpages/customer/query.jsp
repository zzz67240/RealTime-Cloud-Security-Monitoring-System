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
<form action="/RCSMS_Web/customer/query.do" method="POST">
    <table border="0">
        <tr>
            <th>顧客序號：</th>
            <td><input name="serialnumber" type="text" value="${param.serialnumber}"/></td>
        </tr>
        <tr>
            <th>名字：</th>
            <td><input name="firstname" type="text" value="${param.firstname}"/></td>
        </tr>
        <tr>
            <th>姓氏：</th>
            <td><input name="lastname" type="text" value="${param.lastname}"/></td>
        </tr>
        <tr>
            <th>性別：</th>
            <td><input name="gender" type="radio" value="1" ${param.gender == 1 ? "checked" : ""}>先生
                <input name="gender" type="radio" value="2" ${param.gender == 2 ? "checked" : ""}>小姐</td>
        </tr>
        <tr>
            <th>生日：</th>
            <td><input name="birthday" type="text" value="${param.birthday}"></td>
        </tr>
        <tr>
            <th>電話：</th>
            <td><input name="phonenumber" type="text" value="${param.phonenumber}"></td>
        </tr>
        <tr>
            <th>地址：</th>
            <td><input name="address" type="text" value="${param.address}"></td>
        </tr>
        <tr>
            <td><input id="smallbutton" type="submit" value="送出"/></td>
        </tr>
    </table>
</form>
<form action="/RCSMS_Web/customer/queryall.do" method="POST">
    <input id="smallbutton" type="submit" value="查詢所有顧客資料"/>
</form>
        <br/>