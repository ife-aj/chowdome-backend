package com.ife.chowdome.services;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ife.chowdome.model.Users;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secretKey;
    // Ideally, the secret key should be loaded from a secure location or environment variable

        

    public String generateToken( Users user) {
        Map<String, Object> claims = new HashMap<>();
        
        claims.put("role", user.getRole());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
                .setExpiration(new java.util.Date(System.currentTimeMillis() +1000 * 60 * 60 * 10)) // 10 hours expiration
                .signWith(getKey())
                .compact();
        
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
        
    }

    public String extractRole(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public String extractEmail(String token) {
    return Jwts
            .parserBuilder()
            .setSigningKey(getKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
}

public boolean validateToken(String token, Users userDetails) {
    final String email = extractEmail(token);
    final String role = extractRole(token);
    return (email.equals(userDetails.getEmail()) && role.equals(userDetails.getRole()) && !isTokenExpired(token));
}

private boolean isTokenExpired(String token) {
    Date expiration = Jwts
            .parserBuilder()
            .setSigningKey(getKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();

    return expiration.before(new Date());
}

    

}
