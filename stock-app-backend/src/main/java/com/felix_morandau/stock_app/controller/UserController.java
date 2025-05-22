package com.felix_morandau.stock_app.controller;

import com.felix_morandau.stock_app.dto.user.CreateUserDTO;
import com.felix_morandau.stock_app.dto.user.UpdateUserDTO;
import com.felix_morandau.stock_app.entity.enums.UserType;
import com.felix_morandau.stock_app.entity.transactional.User;
import com.felix_morandau.stock_app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<User>> getUsers(
            @RequestParam(required = false) UserType type) {

        List<User> users = userService.getUsers(type);
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<User> addUser(
            @Valid @RequestBody CreateUserDTO dto) {

        User created = userService.addUser(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserDTO dto) {

        User updated = userService.updateUser(dto, userId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID userId) {

        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
