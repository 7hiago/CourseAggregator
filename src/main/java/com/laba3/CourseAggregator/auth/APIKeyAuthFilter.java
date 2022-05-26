package com.laba3.CourseAggregator.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class APIKeyAuthFilter extends GenericFilterBean {
    private static final Logger logger = LoggerFactory.getLogger(APIKeyAuthFilter.class);

    @Value("${Api-key}")
    private String apiKey;

    public APIKeyAuthFilter() { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();

        if(!path.startsWith("/api")){
            chain.doFilter(request, response);
            return;
        }

        String key = req.getHeader("Api-key") == null ? "" : req.getHeader("Api-key");
        logger.debug("Trying key: " + key);

        if(key.equals(apiKey)){
            chain.doFilter(request, response);
        }else{
            HttpServletResponse resp = (HttpServletResponse) response;
            String error = "Invalid API KEY";
            logger.debug("Invalid API KEY");
            resp.reset();
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentLength(error .length());
            response.getWriter().write(error);
        }

    }


}