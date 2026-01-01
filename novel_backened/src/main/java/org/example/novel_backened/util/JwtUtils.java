package org.example.novel_backened.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.novel_backened.config.JwtProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {
    private final JwtProperties jwtProperties;

    public JwtUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private SecretKey getKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            keyBytes = digest.digest(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not supported", e);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        long now = System.currentTimeMillis();
        long exp = now + jwtProperties.getExpireSeconds() * 1000;
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(exp))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
