package com.wordgame.models;

/**
 * Language.
 *
 * @author Arunova Anastasia and Margarita
 * @version 1.0
 * @since 1.0
 */
public enum Language {
    RUSSIAN("RU"),
    ENGLISH("EN");

    private final String languageCode;

    Language(String code) {
        this.languageCode = code;
    }

    public String getLanguageCode() {
        return languageCode;
    }
}
