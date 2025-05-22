package com.felix_morandau.stock_app.dto.login;

import com.felix_morandau.stock_app.entity.enums.UserType;

public record LoginResponse(
        boolean valid,
        UserType role,
        String error,
        String token) {
}
