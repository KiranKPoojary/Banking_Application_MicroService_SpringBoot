package com.example.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;


@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    public Key getSecret() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecret())   // USE key used in user service generator
                .build()
                .parseClaimsJws(token)       // throws exception if signature invalid
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
    public Long extractId(String token) {
        return extractAllClaims(token).get("Id", Long.class);
    }


    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token) {
        try {
            if(isTokenExpired(token)) {
                return false;
            }
            extractAllClaims(token); // if parsing succeeds, token is valid
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}

