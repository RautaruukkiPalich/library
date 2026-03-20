package com.app.core.utils.jwt;

import com.app.core.security.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class JWTImpl implements JWTExtractor, JWTGenerator {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.ttl}")
    private long ttl;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        this.signingKey = getSigningKey();
        validateSecret();
    }

    private void validateSecret() {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException(
                    "JWT secret must be at least 32 bytes. Current length: " + bytes.length
            );
        }
    }

    private SecretKey getSigningKey() {
        byte[] bytes;

        try {
            bytes = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException e) {
            bytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        return Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(String sub) {
        return Jwts.builder()
                .subject(sub)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ttl * 1000))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public String generateToken(String sub, List<Role> roles) {
        return Jwts.builder()
                .subject(sub)
                .claim("roles", Strings.join(roles, ','))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ttl * 1000))
                .signWith(signingKey)
                .compact();
    }

    public String extractSub(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<Role> extractRoles(String token) {
        return extractClaim(token, claims ->
                Arrays.stream(claims.get("roles", String.class).split(",")).map(Role::valueOf).toList());
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
