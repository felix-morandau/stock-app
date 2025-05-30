package com.felix_morandau.stock_app.dto.user;

import com.felix_morandau.stock_app.entity.enums.CurrencyType;
import com.felix_morandau.stock_app.entity.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSettingsDTO {
    private CurrencyType currency;
    private Language language;
    private String theme;
}
