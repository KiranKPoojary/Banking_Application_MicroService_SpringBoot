package com.example.userservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private String SECRET_KEY = "@#1&ABackiran#@123";// can be anything which is secured

    private final long EXPIRATION_TIME = 1000 * 60 * 60;

//    public String generateUserToken(String username, String role) {
//        return Jwts.builder()
//                .setSubject(username)
//                .claim("role", role)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
//                .compact();
//    }
//
//    public String generateEmployeeToken(Long EmployeeId,String username, String role) {
//
//        Map<String,Object> claims = new HashMap<>();
//
//                claims.put("empId",EmployeeId);
//                claims.put("username",username);
//                claims.put("role",role);
//
//        return Jwts.builder()
//                .setSubject(username)
//                .setClaims(claims)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
//                .compact();
//    }
//
//    public Claims extractAllClaims(String token) {
//        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
//    }
//
//    public String extractUsername(String token) {
//        return extractAllClaims(token).get("username", String.class);
//    }
//
//    public String extractEmpId(String token) {
//        return extractAllClaims(token).get("empId", String.class);
//    }
//
//    public String extractRole(String token) {
//        return extractAllClaims(token).get("role", String.class);
//    }
//
//    public boolean validateToken(String token, String username) {
//        final String extractedUsername = extractUsername(token);
//        return (extractedUsername.equals(username) && !isTokenExpired(token));
//    }
//
//    private boolean isTokenExpired(String token) {
//        return extractAllClaims(token).getExpiration().before(new Date());
//    }

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
