package com.felix_morandau.stock_app.entity.transactional;

import com.felix_morandau.stock_app.entity.enums.CurrencyType;
import com.felix_morandau.stock_app.entity.enums.Language;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Currency;
import java.util.UUID;

@Entity
@Table(name = "settings")
@Data
public class UserPreferredSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Language language;

    @Column(name = "theme", nullable = false)
    private String theme;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private CurrencyType currency;

    public UserPreferredSettings() {
        this.language = Language.ENGLISH;
        this.theme = "LIGHT";
        this.currency = CurrencyType.EUR;
    }
}
