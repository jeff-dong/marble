package com.github.jxdong.marble.global.listener;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ccsa on 23/06/2017.
 */
public class MarbleHiddenHttpMethodFilter extends HiddenHttpMethodFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //如果请求以 resutful开头，跳过拦截
        String servletPath = request.getServletPath();
        if (servletPath != null && servletPath.startsWith("/restful/api/")) {
            filterChain.doFilter(request, response);
        } else {
            super.doFilterInternal(request, response, filterChain);
        }
    }
}
