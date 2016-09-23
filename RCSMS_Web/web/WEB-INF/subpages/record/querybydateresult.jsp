<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="java.util.List"%>
<%@page import="net.rcsms.domain.Record"%>
<%
    out.println("<table border='1'>");
    out.println("<tr><th>序號</th><th>日期及時間</th><th>裝置序號</th><th>攝氏溫度</th>"
            + "<th>相對濕度</th><th>瓦斯濃度</th><th>煙霧濃度</th></tr>");
    List<Record> recordlist = (List<Record>) request.getAttribute("recordlist");
    if (recordlist != null) {
        for (Record record : recordlist) {
            out.println("<tr><td>" + record.getSerialnumber()+ "</td><td>" + record.getDatetime()
                    + "</td><td>" + record.getDeviceserialnumber() + "</td><td>" + record.getTemperature()
                    + "</td><td>" + record.getHumidity() + "</td><td>" + record.getGas()
                    + "</td><td>" + record.getSmoke() + "</td></tr>");
        }
        out.println("</table>");
    } 
%>
<br/>
<button id="smallbutton" onclick="window.location.href='/RCSMS_Web/record/QueryByDate.jsp'">繼續查詢</button>
<!--<form action="QueryMember.jsp" method="POST">
    <input id="smallbutton" type="submit" value="Query Another"/>
</form>-->