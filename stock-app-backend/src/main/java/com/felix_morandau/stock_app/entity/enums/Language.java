package com.felix_morandau.stock_app.entity.enums;

public enum Language {

    ENGLISH("en"),
    FRENCH("fr"),
    GERMAN("de"),
    SPANISH("es"),
    ROMANIAN("ro");

    private final String isoCode;

    Language(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getIsoCode() {
        return isoCode;
    }
}
