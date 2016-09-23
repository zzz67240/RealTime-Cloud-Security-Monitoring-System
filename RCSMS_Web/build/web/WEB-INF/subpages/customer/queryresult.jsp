<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="java.util.List"%>
<%@page import="net.rcsms.domain.Customer"%>
<%
    out.println("<table border='1'>");
    out.println("<tr><th>顧客序號</th><th>名字</th><th>姓氏</th><th>性別</th>"
            + "<th>生日</th><th>電話</th><th>地址</th></tr>");
    List<Customer> customerlist = (List<Customer>) request.getAttribute("customerlist");
    if (customerlist != null) {
        for (Customer customer : customerlist) {
            out.println("<tr><td>" + customer.getSerialnumber()+ "</td><td>" + customer.getFirstname() 
                    + "</td><td>" + customer.getLastname() + "</td><td>" + (customer.getGender() == 1 ? "先生" : "小姐")
                    + "</td><td>" + customer.getBirthday() + "</td><td>" + customer.getPhonenumber()
                    + "</td><td>" + customer.getAddress() + "</td></tr>");
        }
        out.println("</table>");
    } 
%>
<br/>
<button id="smallbutton" onclick="window.location.href='/RCSMS_Web/customer/Query.jsp'">繼續查詢</button>
<!--<form action="QueryMember.jsp" method="POST">
    <input id="smallbutton" type="submit" value="Query Another"/>
</form>-->