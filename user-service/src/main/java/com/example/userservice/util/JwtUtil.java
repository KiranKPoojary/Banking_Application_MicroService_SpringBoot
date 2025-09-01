package com.example.userservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = Base64.getEncoder().encodeToString("nS3+qAxEw0yX7eqsQyoM42X+O6y6qjQK0ZvMNoC2xRU=".getBytes());

    private final long EXPIRATION_TIME = 1000 * 60 * 60;

        public String generateToken(Map<String, Object> claims, String subject) {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)   // can be username or empId
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 1 hr
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                    .compact();
        }

        public Claims extractAllClaims(String token) {
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        }

        public String extractUsername(String token) {
            return extractAllClaims(token).get("username", String.class);
        }

        public String extractEmpId(String token) {
            return extractAllClaims(token).get("empId", String.class);
        }

        public String extractRole(String token) {
            return extractAllClaims(token).get("role", String.class);
        }

        public boolean validateToken(String token, String username) {
            return extractUsername(token).equals(username) && !isTokenExpired(token);
        }

        private boolean isTokenExpired(String token) {
            return extractAllClaims(token).getExpiration().before(new Date());
        }
    }
