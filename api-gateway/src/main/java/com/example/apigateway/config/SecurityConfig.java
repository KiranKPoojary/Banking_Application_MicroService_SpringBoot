package com.example.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }
}

//@Configuration
//public class SecurityConfig {
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//    }
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
//                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);
//
//        // Add JWT filter at the AUTHENTICATION position
//        http.addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION);
//
//        http.authorizeExchange(exchanges -> exchanges
//                .pathMatchers("/api/v0/auth/**").permitAll()  // public routes
//                .anyExchange().authenticated()               // all others require JWT
//        );
//
//        return http.build();
//    }
//}
