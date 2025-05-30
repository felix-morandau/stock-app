package com.felix_morandau.stock_app.service;

import com.felix_morandau.stock_app.config.UserNotFoundException;
import com.felix_morandau.stock_app.dto.user.UserSettingsDTO;
import com.felix_morandau.stock_app.entity.transactional.UserPreferredSettings;
import com.felix_morandau.stock_app.repository.SettingsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class SettingsService {
    public SettingsRepository settingsRepository;

    public UserPreferredSettings getUserSettings(UUID userId) {

        return settingsRepository.getByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public UserPreferredSettings updateUserSettings(UserSettingsDTO dto, UUID userId) {
        UserPreferredSettings settings = settingsRepository.getByUserId(userId)
                        .orElseThrow(() -> new UserNotFoundException(userId));

        if (dto.getCurrency() != null) {
            settings.setCurrency(dto.getCurrency());
        }
        if (dto.getLanguage() != null) {
            settings.setLanguage(dto.getLanguage());
        }
        if (dto.getTheme() != null) {
            settings.setTheme(dto.getTheme());
        }

        return settingsRepository.save(settings);
    }
}
