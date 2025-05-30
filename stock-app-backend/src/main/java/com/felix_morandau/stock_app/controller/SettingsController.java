package com.felix_morandau.stock_app.controller;

import com.felix_morandau.stock_app.dto.user.UserSettingsDTO;
import com.felix_morandau.stock_app.entity.transactional.UserPreferredSettings;
import com.felix_morandau.stock_app.service.SettingsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/settings")
public class SettingsController {
    SettingsService settingsService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserPreferredSettings> getUserSettings(
            @PathVariable UUID userId
    ) {
        UserPreferredSettings settings = settingsService.getUserSettings(userId);

        return ResponseEntity.ok(settings);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserPreferredSettings> updateUserSettings(
            @PathVariable UUID userId,
            @RequestBody UserSettingsDTO dto
    ) {
        UserPreferredSettings updated = settingsService.updateUserSettings(dto, userId);
        return ResponseEntity.ok(updated);
    }

}
