package com.example.userservice.filter;

import com.example.userservice.entity.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/user-service/api/v0/auth/login/user",
            "/user-service/api/v0/auth/register/user",
            "/actuator",
            "/swagger-ui",
            "/v3/api-docs"
    );

    private boolean isPublicPath (String path){
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {



        String path = request.getRequestURI();
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String userRole = request.getHeader("X-Auth-Role");
        String userId = request.getHeader("X-Auth-Id");
        String username = request.getHeader("X-Auth-User");
        String serviceAuth = request.getHeader("X-Service-Auth");


        System.out.println("userRole: " + userRole);
        System.out.println("userId: " + userId);
        System.out.println("username: " + username);
        System.out.println("serviceHeader" + serviceAuth);

        if(serviceAuth != null && serviceAuth.equals("Kiran@1234_Notification"))
        {
            System.out.println("Service-Service Communication Request");
            CustomUserDetails userDetails=new CustomUserDetails("Notification", Long.parseLong("1"), "SERVICE");
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            System.out.println("Authorities"+userDetails.getAuthorities());

            System.out.println(auth.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);


            filterChain.doFilter(request, response);



            return;
        }

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


