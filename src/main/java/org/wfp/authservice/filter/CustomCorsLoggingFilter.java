package org.wfp.authservice.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class CustomCorsLoggingFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(CustomCorsLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        logger.info("Incoming Request: Method = {}, URI = {}, Origin = {}",
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                httpRequest.getHeader("Origin"));
        chain.doFilter(request, response);
    }
}
