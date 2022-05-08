package com.wordgame.models;

/**
 * The scoring model.
 *
 * @author Arunova Margarita
 * @version 1.0
 * @since 1.0
 */
public class Scoring {
    public static int getPointsAmount(String letters, String word) {
        int firstLetterInd = -1, secondLetterInd = -1,
                thirdLetterInd = -1, thirdLetterLastInd = -1;
        for (int i = 0; i < word.length(); ++i) {
            if (firstLetterInd < 0 && word.charAt(i) == letters.charAt(0)) {
                firstLetterInd = i;
                continue;
            }
            if (firstLetterInd >= 0 && secondLetterInd < 0 && word.charAt(i) == letters.charAt(1)) {
                secondLetterInd = i;
                continue;
            }
            if (secondLetterInd >= 0 && word.charAt(i) == letters.charAt(2)) {
                thirdLetterLastInd = i;
                if (thirdLetterInd < 0) {
                    thirdLetterInd = i;
                }
            }
        }
        if (thirdLetterInd < 0) {
            return 0;
        }
        int points = 3;
        if (firstLetterInd + 1 < secondLetterInd) {
            points += 1;
        } else {
            for (int i = secondLetterInd + 1; i < thirdLetterLastInd; ++i) {
                if (word.charAt(i) == letters.charAt(1)) {
                    points += 1;
                    break;
                }
            }
        }
        if (secondLetterInd + 1 < thirdLetterLastInd) {
            points += 1;
        }
        return points;
    }
}
