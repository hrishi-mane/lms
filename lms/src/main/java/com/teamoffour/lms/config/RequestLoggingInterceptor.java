package com.teamoffour.lms.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {



    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        log.info("→ Incoming Request: {} {} from IP: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());

        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;

        log.info("← Response: {} | Status: {} | Duration: {} ms",
                request.getRequestURI(),
                response.getStatus(),
                duration);

    }
}