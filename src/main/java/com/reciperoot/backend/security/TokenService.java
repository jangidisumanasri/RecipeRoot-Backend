package com.reciperoot.backend.security;

import com.reciperoot.backend.model.AppUser;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

    public record SessionUser(Long id, String name, String email, String role) {}

    private final Map<String, SessionUser> tokens = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public String createToken(AppUser user) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder()
                .withoutPadding().encodeToString(bytes);

        tokens.put(token, new SessionUser(
                user.getId(), user.getName(), user.getEmail(), user.getRole()
        ));
        return token;
    }

    public SessionUser findUser(String token) {
        return tokens.get(token);
    }

    public void removeToken(String token) {
        tokens.remove(token);
    }
}