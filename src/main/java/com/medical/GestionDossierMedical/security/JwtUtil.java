package com.medical.GestionDossierMedical.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    // Clef secrete
    private final SecretKey jwtSecret ;
    private final long jwtExpirationMs = 86400000 ; // 24 heures
    public JwtUtil() {
        String secret = System.getenv("JWT_SECRET") != null ? System.getenv("JWT_SECRET") : generateSecretKey();
        this.jwtSecret = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    private String generateSecretKey()
    {
        SecureRandom random = new SecureRandom() ;
        byte[] keyBytes = new byte[32] ; // on met 32 octet pour un meilleurs securite
        random.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes) ;
    }
    public String generateToken(String username)
    {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(jwtSecret)
                .compact();
    }
  /**  private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.);
    }**/
    public boolean validateToken(String token)
    {
        try
        {
            Jwts.parser()
                    .verifyWith( jwtSecret)
                    .build()
                    .parseSignedClaims(token);
            return true ;
        } catch (JwtException | IllegalArgumentException e)
        {
            return false;
        }
    }
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(jwtSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

}
