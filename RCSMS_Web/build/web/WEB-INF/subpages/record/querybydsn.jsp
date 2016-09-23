<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="java.util.List"%>
請直接輸入序號的數字部分即可。如裝置序號為 D0001，輸入 1 即可。
<br/><br/>
${errorMessage}
<br/><br/>
<form action="/RCSMS_Web/record/querybydsn.do" method="POST">
    <table border="0">
        <tr>
            <th>裝置序號：</th>
            <td><input name="deviceserialnumber" type="text" value="${param.deviceserialnumber}"/></td>
        </tr>
        <tr>
            <td><input id="smallbutton" type="submit" value="送出"/></td>
        </tr>
    </table>
</form>
        <br/>