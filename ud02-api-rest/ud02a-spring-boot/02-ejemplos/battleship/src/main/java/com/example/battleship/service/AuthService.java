package com.example.battleship.service;

import com.example.battleship.domain.User;
import com.example.battleship.dto.TokenResponse;
import com.example.battleship.repository.UserRepository;
import com.example.battleship.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public void register(String username, String password, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        var user = new User(username, passwordEncoder.encode(password), Set.of(role));
        userRepository.save(user);
    }

    public TokenResponse login(String username, String password) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        var roles = auth.getAuthorities().stream()
                .map(a -> a.getAuthority()).toList();
        return new TokenResponse(
                jwtService.generateAccessToken(username, roles),
                jwtService.generateRefreshToken(username),
                900000L
        );
    }

    public TokenResponse refresh(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        var roles = user.getRoles().stream().toList();
        return new TokenResponse(
                jwtService.generateAccessToken(username, roles),
                jwtService.generateRefreshToken(username),
                900000L
        );
    }
}
