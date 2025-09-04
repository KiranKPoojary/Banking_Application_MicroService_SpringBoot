package com.example.userservice.util;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtil {

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getUserAgent(HttpServletRequest request) {
        if(request.getHeader("User-Agent")!=null){
            return "Not Found";
        }
        return request.getHeader("User-Agent");

    }
}

