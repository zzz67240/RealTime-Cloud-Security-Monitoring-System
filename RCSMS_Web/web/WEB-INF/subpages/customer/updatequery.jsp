<%@page contentType="text/html" pageEncoding="UTF-8" %>
<ul>
    ${errorMessage}
</ul>
<form action="/RCSMS_Web/customer/updatequery.do" method="POST">
    <table border="0">
        <tr>
            <th>顧客序號：</th>
            <td><input name="serialnumber" type="text" value="${param.serialnumber}"/></td>
        </tr>
        <tr>
            <th>名字：</th>
            <td></td>
        </tr>
        <tr>
            <th>姓氏：</th>
            <td></td>
        </tr>
        <tr>
            <th>性別：</th>
            <td></td>
        </tr>
        <tr>
            <th>生日：</th>
            <td></td>
        </tr>
        <tr>
            <th>電話：</th>
            <td></td>
        </tr>
        <tr>
            <th>地址：</th>
            <td></td>
        </tr>
        <tr>
            <td><input id="smallbutton" type="submit" value="送出"/></td>
        </tr>
    </table>
</form>