package com.semmtech.laces.fetch.security.rest.filters;


import com.semmtech.laces.fetch.security.rest.headers.RelaticsAddOnAllowFromStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.header.writers.frameoptions.AllowFromStrategy;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class AddOnRelaticsProjectFilter extends GenericFilterBean {

    private final AllowFromStrategy allowFromStrategy;

    public AddOnRelaticsProjectFilter(AllowFromStrategy relaticsAddOnAllowFromStrategy) {
        this.allowFromStrategy = relaticsAddOnAllowFromStrategy;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (!(StringUtils.contains(request.getHeader("referer"), "relaticsonline.com")
                && StringUtils.equals(allowFromStrategy.getAllowFromValue(request), RelaticsAddOnAllowFromStrategy.DENY))) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            servletResponse.getWriter().println("Relatics environment, workspace or url does not match for configuration "+ servletRequest.getParameter("configurationId")+".");
        }

    }
}

