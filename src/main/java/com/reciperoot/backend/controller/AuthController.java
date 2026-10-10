package com.reciperoot.backend.controller;

import com.reciperoot.backend.model.AppUser;
import com.reciperoot.backend.repository.AppUserRepository;
import com.reciperoot.backend.security.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AppUserRepository users;
    private final PasswordEncoder encoder;
    private final TokenService tokens;

    public AuthController(
            AppUserRepository users,
            PasswordEncoder encoder,
            TokenService tokens) {
        this.users = users;
        this.encoder = encoder;
        this.tokens = tokens;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (request.name() == null || request.name().isBlank()
                || request.email() == null || request.email().isBlank()
                || request.password() == null || request.password().length() < 8) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Provide a name, email, and password of at least 8 characters.")
            );
        }

        String email = request.email().trim().toLowerCase();

        if (users.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("message", "An account with this email already exists.")
            );
        }

        AppUser user = new AppUser();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(request.password()));
        user.setRole("USER"); // Never accept a public registration role.

        AppUser saved = users.save(user);
        String token = tokens.createToken(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "token", token,
            "id", saved.getId(),
            "name", saved.getName(),
            "email", saved.getEmail(),
            "role", saved.getRole()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request.email() == null || request.password() == null) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Email and password are required.")
            );
        }

        AppUser user = users.findByEmail(
                request.email().trim().toLowerCase()
            )
            .orElse(null);

        if (user == null || !encoder.matches(
                request.password(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("message", "Invalid email or password.")
            );
        }

        String token = tokens.createToken(user);

        return ResponseEntity.ok(Map.of(
            "token", token,
            "id", user.getId(),
            "name", user.getName(),
            "email", user.getEmail(),
            "role", user.getRole()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {

        if (authorization != null && authorization.startsWith("Bearer ")) {
            tokens.removeToken(authorization.substring(7));
        }

        return ResponseEntity.ok(Map.of("message", "Logged out."));
    }

    public record RegisterRequest(String name, String email, String password) {}
    public record LoginRequest(String email, String password) {}
}