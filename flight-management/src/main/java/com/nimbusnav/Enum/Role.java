package com.nimbusnav.Enum;

import java.util.Optional;

public enum Role {
    ADMIN,
    USER;

    public static Role fromString(String role) {
        return Optional.ofNullable(role)
                .map(String::trim)
                .filter(r -> !r.isEmpty())
                .map(String::toUpperCase)
                .map(Role::valueOf)
                .orElse(USER);
    }
}
