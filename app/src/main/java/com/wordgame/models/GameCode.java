package com.wordgame.models;

import java.util.Random;

/**
 * Game code generator.
 *
 * @author Arunova Anastasia
 * @version 1.0
 * @since 1.0
 */
public class GameCode {

    private static final char[] SYMBOLS = {'1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static String getCode() {
        Random random = new Random();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            stringBuilder.append(SYMBOLS[random.nextInt(SYMBOLS.length)]);
        }

        return stringBuilder.toString();
    }
}
