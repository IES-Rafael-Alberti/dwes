package com.example.seguridad.controller;

import com.example.seguridad.domain.User;
import com.example.seguridad.dto.AuthRequest;
import com.example.seguridad.dto.TokenResponse;
import com.example.seguridad.security.JwtService;
import com.example.seguridad.service.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final CustomUserDetailsService userService;

  public AuthController(AuthenticationManager authenticationManager,
                        JwtService jwtService,
                        CustomUserDetailsService userService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<User> register(@RequestBody AuthRequest request) {
    User created = userService.register(request.username(), request.password(), "ROLE_USER");
    created.setPassword(null); // evitar exponer hash
    return ResponseEntity.ok(created);
  }

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@RequestBody AuthRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password()));
    UserDetails principal = (UserDetails) authentication.getPrincipal();
    String token = jwtService.generateToken(
        principal.getUsername(),
        principal.getAuthorities().stream().map(a -> a.getAuthority()).toList());
    return ResponseEntity.ok(new TokenResponse(token));
  }
}
