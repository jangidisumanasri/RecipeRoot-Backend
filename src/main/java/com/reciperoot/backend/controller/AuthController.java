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

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        if (request.name() == null
                || request.name().isBlank()
                || request.email() == null
                || request.email().isBlank()
                || request.password() == null
                || request.password().length() < 8) {

            return ResponseEntity.badRequest().body(
                Map.of(
                    "message",
                    "Provide a name, email, and password of at least 8 characters."
                )
            );
        }

        String email = request.email().trim().toLowerCase();

        if (users.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                    "message",
                    "An account with this email already exists."
                )
            );
        }

        AppUser user = new AppUser();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(request.password()));

        // Public registration must not allow users to choose a role.
        user.setRole("USER");

        AppUser saved = users.save(user);
        String token = tokens.createToken(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of(
                "token", token,
                "id", saved.getId(),
                "name", saved.getName(),
                "email", saved.getEmail(),
                "role", saved.getRole()
            )
        );
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        if (request.email() == null
                || request.password() == null) {

            return ResponseEntity.badRequest().body(
                Map.of(
                    "message",
                    "Email and password are required."
                )
            );
        }

        String email = request.email().trim().toLowerCase();

        AppUser user = users.findByEmail(email).orElse(null);

        if (user == null
                || !encoder.matches(
                    request.password(),
                    user.getPasswordHash())) {

            return ResponseEntity.status(
                HttpStatus.UNAUTHORIZED
            ).body(
                Map.of("message", "Invalid email or password.")
            );
        }

        String token = tokens.createToken(user);

        return ResponseEntity.ok(
            Map.of(
                "token", token,
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole()
            )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(
        @RequestHeader(
            value = "Authorization",
            required = false
        ) String authorization) {

    if (authorization == null
            || !authorization.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            Map.of("message", "Authentication token is missing.")
        );
    }

    String token = authorization.substring(7);

    TokenService.SessionUser user = tokens.findUser(token);
    if (user == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            Map.of("message", "Invalid or expired token.")
        );
    }

    return ResponseEntity.ok(Map.of(
        "id", user.id(),
        "name", user.name(),
        "email", user.email(),
        "role", user.role()
    ));
} 
    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(
                value = "Authorization",
                required = false
            ) String authorization) {

        if (authorization != null
                && authorization.startsWith("Bearer ")) {

            tokens.removeToken(authorization.substring(7));
        }

        return ResponseEntity.ok(
            Map.of("message", "Logged out.")
        );
    }

    // REQUEST MODELS
    public record RegisterRequest(
            String name,
            String email,
            String password) {}

    public record LoginRequest(
            String email,
            String password) {}
}