package com.nimbusnav.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtUtil {

    //  مفتاح آمن تلقائيًا
    private static final String SECRET_KEY = Base64.getEncoder().encodeToString("VGhpcy1pcy1hLXN1cGVyLXNlY3VyZS1rZXktZm9yLUpXVA==".getBytes());

    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 10;

    private final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7;

    @PostConstruct
    public void init() {
        System.out.println(" JwtUtil Bean Initialized!");
    }

    public JwtUtil() {
        System.out.println("SECRET_KEY: " + SECRET_KEY);  // ✅ تأكد أن المفتاح لا يتغير
    }

    //  إنشاء توكن وصول مع Role و UserID
    public String generateACCESSToken(String username, UUID userId, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("userId", userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS384)
                .compact();
    }

    //  إنشاء توكن محدث مع Role و UserID
    public String generateRefreshToken(String username, UUID userId, String role){
        return Jwts.builder()
                .setSubject(username)
                .claim("role",role)
                .claim("userId",userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS384)
                .compact();
    }

    //  استخراج الـ Role من التوكن
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    //  استخراج الـ UserID من التوكن
    public UUID extractUserId(String token) {
        return UUID.fromString(extractClaim(token, claims -> claims.get("userId", String.class)));
    }

    //  استخراج الـ Username من التوكن
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    //  استخراج تاريخ انتهاء الصلاحية
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //  دالة عامة لاستخراج أي Claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    //  التحقق من صلاحية التوكن
    public boolean isTokenValid(String token, String username) {
        return (extractUsername(token).equals(username) && !isTokenExpired(token));
    }

    //  التحقق من انتهاء التوكن
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    //  إرجاع المفتاح السري
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
