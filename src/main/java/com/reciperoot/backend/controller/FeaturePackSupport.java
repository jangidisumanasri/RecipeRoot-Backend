package com.reciperoot.backend.featurepack.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

final class FeaturePackSupport {

    private FeaturePackSupport() {}

    static Long userId(HttpSession session) {
        Object id = session.getAttribute("userId");

        if (!(id instanceof Long)) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Please log in"
            );
        }

        return (Long) id;
    }

    static void admin(HttpSession session) {
        userId(session);

        if (!"ADMIN".equals(session.getAttribute("userRole"))) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Admin access required"
            );
        }
    }
}