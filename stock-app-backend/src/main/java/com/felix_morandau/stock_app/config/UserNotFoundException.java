package com.felix_morandau.stock_app.config;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(UUID id) {
        super("User with id: " + id + " was not found");
    }
}
