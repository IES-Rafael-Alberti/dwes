package com.example.battleship.security;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final Set<String> LEGACY_ROLES = Set.of("PLAYER", "ADMIN");

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final long accessExpiration;
    private final long refreshExpiration;
    private final String issuer;
    private final String audience;

    public JwtService(
            PrivateKey privateKey,
            PublicKey publicKey,
            @Value("${app.security.jwt.access-token-expiration}") long accessExpiration,
            @Value("${app.security.jwt.refresh-token-expiration}") long refreshExpiration,
            @Value("${app.security.jwt.issuer}") String issuer,
            @Value("${app.security.jwt.audience}") String audience) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.issuer = issuer;
        this.audience = audience;
    }

    public String generateAccessToken(String username, Collection<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuer(issuer)
                .audience().add(audience).and()
                .claim("roles", normalizeRoles(roles))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(accessExpiration)))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    private List<String> normalizeRoles(Collection<String> roles) {
        return roles.stream()
                .filter(role -> role.startsWith(ROLE_PREFIX) || LEGACY_ROLES.contains(role))
                .map(role -> role.startsWith(ROLE_PREFIX) ? role : ROLE_PREFIX + role)
                .toList();
    }

    public String generateRefreshToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuer(issuer)
                .audience().add(audience).and()
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(refreshExpiration)))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .requireIssuer(issuer)
                .requireAudience(audience)
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isRefreshToken(Claims claims) {
        return "refresh".equals(claims.get("type"));
    }
}
