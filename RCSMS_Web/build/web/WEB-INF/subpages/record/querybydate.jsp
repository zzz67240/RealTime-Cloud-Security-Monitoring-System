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
<form action="/RCSMS_Web/record/querybydate.do" method="POST">
    <table border="0">
        <tr>
            <th>起始日期：</th>
            <td><input name="date1" type="text" value="${param.date1}"/></td>
        </tr>
        <tr>
            <th>結束日期：</th>
            <td><input name="date2" type="text" value="${param.date2}"/></td>
        </tr>
        <tr>
            <td><input id="smallbutton" type="submit" value="送出"/></td>
        </tr>
    </table>
</form>
        <br/>