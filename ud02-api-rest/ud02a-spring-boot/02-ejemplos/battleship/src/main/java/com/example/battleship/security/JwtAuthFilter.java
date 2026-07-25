package com.example.battleship.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SecurityErrorWriter errorWriter;

    public JwtAuthFilter(JwtService jwtService, SecurityErrorWriter errorWriter) {
        this.jwtService = jwtService;
        this.errorWriter = errorWriter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtService.parseClaims(token);

                if (jwtService.isRefreshToken(claims)) {
                    errorWriter.write(response, 401, "UNAUTHORIZED", "Use access token, not refresh token");
                    return;
                }

                Object rawRoles = claims.get("roles");
                if (!(rawRoles instanceof Collection<?> roles)
                        || roles.isEmpty()
                        || roles.stream().anyMatch(role -> !(role instanceof String value)
                                || !value.startsWith("ROLE_"))) {
                    errorWriter.write(response, 401, "UNAUTHORIZED", "Invalid access token claims");
                    return;
                }
                var authorities = roles.stream()
                        .map(String.class::cast)
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                var auth = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(), null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (JwtException | ClassCastException ex) {
                SecurityContextHolder.clearContext();
                errorWriter.write(response, 401, "UNAUTHORIZED", "Invalid or expired token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
