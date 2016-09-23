<%@page import="net.rcsms.domain.Record"%>
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
                        <jsp:param name="subtitle" value="以日期查詢"/>
                    </jsp:include>
                </td>
            </tr>
            <tr>
                <td width="25%">
                    <jsp:include page="/WEB-INF/views/recordfunction.jsp"/>
                </td>
                    <td width="75%" align="center">
                        <c:if test="${result == null}">
                            <jsp:include page="/WEB-INF/subpages/record/querybydate.jsp"/>
                        </c:if>
                        <c:if test="${result != null}">
                            <jsp:include page="/WEB-INF/subpages/record/querybydateresult.jsp"/>
                        </c:if>
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
