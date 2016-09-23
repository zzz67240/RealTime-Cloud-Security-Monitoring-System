<%@page import="java.util.List"%>
<%@page import="net.rcsms.domain.Record"%>
<%@page import="net.rcsms.dao.RecordJdbcDao"%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>即時雲端安全監控系統 Real-Time Cloud Security Monitoring System</title>
        <link rel="stylesheet" href="/RCSMS_Web/style.css" type="text/css">
    </head>
    <body>
        <table cellpadding="0" cellspacing="0" width="80%">
            <tr>
                <td width="25%" align="center"><a href="/RCSMS_Web/index.jsp"/><img id="logo" src="/RCSMS_Web/images/logo.png"/></td>
                <td width="75%" align="center">
                    <jsp:include page="/WEB-INF/views/banner.jsp">
                        <jsp:param name="subtitle" value="異常記錄查詢"/>
                    </jsp:include>
                </td>
            </tr>
            <tr>
                <td width="25%">
                    <jsp:include page="/WEB-INF/views/recordfunction.jsp"/>
                </td>
                <td width="75%" align="center">
                    <table border='1'>
                        <tr>
                            <th>序號</th>
                            <th>記錄時間</th>
                            <th>裝置序號</th>
                            <th>溫度</th>
                            <th>濕度</th>
                            <th>瓦斯</th>
                            <th>煙霧</th>
                        </tr>
                        <c:forEach var="rec" items="${recordJdbcDao.usq}">
                            <tr>
                                <td>${rec.serialnumber}</td>
                                <td>${rec.datetime}</td>
                                <td>${rec.deviceserialnumber}</td>
                                <td>${rec.temperature}</td>
                                <td>${rec.humidity}</td>
                                <td>${rec.gas}</td>
                                <td>${rec.smoke}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </td>
            </tr>
            <tr>
                <td width="25%"></td>
                <td width="75%" align="center">
                    <jsp:include page="/WEB-INF/views/contact.jsp"/>
                </td>
            </tr>
        </table>
    </body>
</html>
