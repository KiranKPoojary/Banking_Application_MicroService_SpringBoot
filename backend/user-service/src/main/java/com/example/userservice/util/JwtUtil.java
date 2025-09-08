package com.example.userservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET = "S2lyYW5LUG9vamFyeTEyM05lZXJqZWRkdTk2S2VlcnRoaQ=="; // must be at least 32 chars
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));


    private final long EXPIRATION_TIME = 1000 * 60 * 60;

        public String generateToken(Map<String, Object> claims, String subject) {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)   // can be username or empId
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 1 hr
                    .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                    .compact();
        }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
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
