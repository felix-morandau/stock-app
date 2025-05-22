package com.felix_morandau.stock_app.controller;

import com.felix_morandau.stock_app.dto.login.LoginRequest;
import com.felix_morandau.stock_app.dto.login.LoginResponse;
import com.felix_morandau.stock_app.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin
public class AuthController {
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {

        LoginResponse res = userService.login(req);

        if (!res.valid()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(res);
        }

        return ResponseEntity.ok(res);
    }

}
