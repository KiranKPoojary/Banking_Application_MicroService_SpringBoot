package com.example.apigateway.filter;


import com.example.apigateway.util.JwtUtil;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        System.out.println(request.getURI());

        // Allow login/register endpoints without JWT
         final List<String> PUBLIC_PATTERNS = java.util.List.of(
                 "/**/auth/login/**",
                 "/user-service/api/v0/auth/register/user",
                 "/**/auth/register/**",
                 "/actuator/**",  // Health check endpoints
                 "/swagger-ui/**", // API documentation
                 "/v3/api-docs/**" // OpenAPI docs
        );

        String requestPath = request.getURI().getPath();

        System.out.println("requestPath: " + requestPath);

        // Check if the request path matches any public pattern
        boolean isPublicUrl = PUBLIC_PATTERNS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestPath));

        if (isPublicUrl) {
            System.out.printf("PUBLIC URL: %s%n", requestPath);
            return chain.filter(exchange);
        }
        // Get token from header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Validate Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return this.onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        // Validate token with exception handling
        try {
            if (!jwtUtil.isTokenValid(token)) {
                System.out.println(token + " is invalid");
                return this.onError(exchange, "Invalid JWT Token", HttpStatus.UNAUTHORIZED);
            }
            if (jwtUtil.isTokenExpired(token)) {
                return this.onError(exchange, "JWT Token has expired", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return this.onError(exchange, "JWT validation error: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        // Extract roles and forward them in headers (downstream services can use)
        String role = jwtUtil.extractRole(token);
        String username = jwtUtil.extractUsername(token);

        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .headers(httpHeaders -> {
                    httpHeaders.remove("X-Auth-User");
                    httpHeaders.remove("X-Auth-Role");
                })
                .header("X-Auth-User", username)
                .header("X-Auth-Role", role)
                .build();

        System.out.println(modifiedRequest.getHeaders());
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }


    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        byte[] bytes = err.getBytes();
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
        );
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
