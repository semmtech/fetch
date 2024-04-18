package com.semmtech.laces.fetch.security.rest.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.ForwardedHeaderFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class LoggingForwardedHeaderFilter extends ForwardedHeaderFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Forward header: " + request.getHeader("X-Forwarded-Proto"));
        super.doFilterInternal(request, response, filterChain);
    }
}
