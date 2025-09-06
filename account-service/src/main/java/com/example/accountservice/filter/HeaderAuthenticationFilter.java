package com.example.accountservice.filter;

import com.example.accountservice.entity.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String userRole = request.getHeader("X-Auth-Role");
        String userId = request.getHeader("X-Auth-Id");
        String username = request.getHeader("X-Auth-User");

        System.out.println("userRole: " + userRole);
        System.out.println("userId: " + userId);
        System.out.println("username: " + username);

        if (userRole != null && userId != null && username != null) {
            CustomUserDetails userDetails = new CustomUserDetails(username, Long.parseLong(userId), userRole);
            System.out.println("Authorities"+userDetails.getAuthorities());
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);
        }else{
            System.out.println("Null in HeaderAuthenticationFilter");
        }

        filterChain.doFilter(request, response);
    }
}

