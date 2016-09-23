package net.rcsms.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class EncodingFilter implements Filter{
    
    private String encoding;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.encoding = filterConfig.getInitParameter("encoding");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ServletContext context = request.getServletContext();
        context.log("EncodingFilter.doFilter - before chain.doFilter");
        request.setCharacterEncoding(encoding); //把request編碼
        response.setCharacterEncoding(encoding); //把request編碼
        StringBuilder sb = new StringBuilder();
        sb.append("Encoding: ");
        sb.append(encoding);
        context.log(sb.toString());
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        
    }
}
