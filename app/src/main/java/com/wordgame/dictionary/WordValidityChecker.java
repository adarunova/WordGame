package com.wordgame.dictionary;

import android.content.Context;

import com.wordgame.models.Language;

/**
 * Word spell checker.
 *
 * @author Arunova Margarita
 * @version 1.0
 * @since 1.0
 */
public class WordValidityChecker {
    private final Context context;
    public Dictionary ruDictionary, enDictionary;

    private Language currentLanguage;

    public WordValidityChecker(Context context) {
        this.context = context;
    }

    public boolean checkWord(String word) {
        String match_word = null;
        if (currentLanguage == Language.ENGLISH) {
            if (enDictionary == null) {
                enDictionary = new Dictionary(context, Language.ENGLISH);
            }
            match_word = enDictionary.getWordMatches(word);
        } else if (currentLanguage == Language.RUSSIAN) {
            if (ruDictionary == null) {
                ruDictionary = new Dictionary(context, Language.RUSSIAN);
            }
            match_word = ruDictionary.getWordMatches(word);
        }
        return word.equals(match_word);
    }

    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    public void setCurrentLanguage(Language currentLanguage) {
        this.currentLanguage = currentLanguage;
    }
}
