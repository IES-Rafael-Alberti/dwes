package com.example.battleship.web;

import com.example.battleship.dto.AuthRequest;
import com.example.battleship.dto.ErrorPayload;
import com.example.battleship.dto.TokenRefreshRequest;
import com.example.battleship.dto.TokenResponse;
import com.example.battleship.security.JwtService;
import com.example.battleship.service.AuthService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest request) {
        authService.register(request.username(), request.password(), "ROLE_PLAYER");
        return ResponseEntity.status(201).body("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody AuthRequest request) {
        var tokens = authService.login(request.username(), request.password());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        Claims claims = jwtService.parseClaims(request.refreshToken());
        if (!jwtService.isRefreshToken(claims)) {
            return ResponseEntity.status(401)
                    .body(new ErrorPayload("UNAUTHORIZED", "A refresh token is required"));
        }
        var tokens = authService.refresh(claims.getSubject());
        return ResponseEntity.ok(tokens);
    }
}
