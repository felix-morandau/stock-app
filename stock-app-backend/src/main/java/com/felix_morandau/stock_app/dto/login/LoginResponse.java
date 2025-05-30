package com.felix_morandau.stock_app.dto.login;

import com.felix_morandau.stock_app.entity.enums.UserType;

import java.util.UUID;

public record LoginResponse(
        boolean valid,
        UUID userId,
        UserType role,
        String error,
        String token) {
}
