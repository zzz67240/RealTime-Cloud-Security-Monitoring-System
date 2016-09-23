<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="java.util.List"%>
${errorMessage}
<form action="/RCSMS_Web/device/query.do" method="POST">
    <table border="0">
        <tr>
            <th>顧客序號：</th>
            <td><input name="customerserialnumber" type="text" value="${param.customerserialnumber}"/></td>
        </tr>
        <tr>
            <td><input id="smallbutton" type="submit" value="送出"/></td>
        </tr>
    </table>
</form>
<form action="/RCSMS_Web/device/queryall.do" method="POST">
    <input id="smallbutton" type="submit" value="查詢所有裝置資料"/>
</form>
        <br/>