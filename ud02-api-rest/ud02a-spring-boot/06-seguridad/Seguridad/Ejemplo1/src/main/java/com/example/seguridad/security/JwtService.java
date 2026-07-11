package com.example.seguridad.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

  private final Key signingKey;
  private final long expirationMillis;

  public JwtService(
      @Value("${app.security.jwt.secret}") String secret,
      @Value("${app.security.jwt.expiration}") long expirationMillis) {
    this.signingKey = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(secret));
    this.expirationMillis = expirationMillis;
  }

  public String generateToken(String username, Collection<String> roles) {
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(username)
        .claim("roles", List.copyOf(roles))
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusMillis(expirationMillis)))
        .signWith(signingKey, Jwts.SIG.HS256)
        .compact();
  }

  public Claims parseClaims(String token) {
    return Jwts.parser()
        .verifyWith(signingKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
