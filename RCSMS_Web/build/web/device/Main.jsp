<%@page contentType="text/html" pageEncoding="UTF-8" %>
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
                        <jsp:param name="subtitle" value="裝置管理主頁面"/>
                    </jsp:include>
                </td>
            </tr>
            <tr>
                <td width="25%">
                    <jsp:include page="/WEB-INF/views/devicefunction.jsp"/>
                </td>
                <td width="75%" align="center"><img height="240px" width="700px" src="/RCSMS_Web/images/welcome.png"/></td>
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
