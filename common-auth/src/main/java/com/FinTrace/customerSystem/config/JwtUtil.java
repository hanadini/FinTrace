package com.FinTrace.customerSystem.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class JwtUtil {

    private static final String SECRET = "TGuNLkJF4mM1yoNpsKVBnuEjKQbP+SxBZyI8WlS6ms8="; // Replace with your actual secret key
    private static final Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    public static String generateToken(String username) {
        return io.jsonwebtoken.Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public static String validateToken(String token) {
        return io.jsonwebtoken.Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

    }
}
