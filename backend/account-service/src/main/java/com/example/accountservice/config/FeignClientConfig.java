package com.example.accountservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // Propagate specific headers
            String auth = request.getHeader("Authorization");
            String user = request.getHeader("X-Auth-User");
            String role = request.getHeader("X-Auth-Role");
            String id = request.getHeader("X-Auth-Id");

            if (auth != null) template.header("Authorization", auth);
            if (user != null) template.header("X-Auth-User", user);
            if (role != null) template.header("X-Auth-Role", role);
            if (id != null) template.header("X-Auth-Id", id);
        }
    }
}
