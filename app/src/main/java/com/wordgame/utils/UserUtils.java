package com.wordgame.utils;

import android.content.Context;

import com.wordgame.R;

/**
 * User utils.
 *
 * @author Arunova Anastasia
 * @version 1.0
 * @since 1.0
 */
public final class UserUtils {

    private UserUtils() {
    }

    /**
     * Checks if the nickname is correct.
     *
     * @param context context
     * @param nickname nickname
     * @throws UserException if the nickname is incorrect
     */
    public static void checkNickname(Context context, String nickname) throws UserException {
        if (nickname == null || nickname.isEmpty()) {
            throw new UserException(context.getResources().getString(R.string.nickname_empty_exception));
        }
        if (nickname.length() > 20) {
            throw new UserException(context.getResources().getString(R.string.nickname_long_exception));
        }
        if (!nickname.matches("[a-zA-Z0-9]+")) {
            throw new UserException(context.getResources().getString(R.string.symbols_exception));
        }
    }

    /**
     * Checks if the email is correct.
     *
     * @param context context
     * @param email email
     * @throws UserException if the email is incorrect
     */
    public static void checkEmail(Context context, String email) throws UserException {
        if (email == null || email.isEmpty()) {
            throw new UserException(context.getResources().getString(R.string.email_empty_exception));
        }
    }

    /**
     * Checks if the password is correct.
     *
     * @param context context
     * @param password password
     * @throws UserException if the password is incorrect
     */
    public static void checkPassword(Context context, String password) throws UserException {
        if (password == null || password.isEmpty()) {
            throw new UserException(context.getResources().getString(R.string.password_empty_exception));
        }
        if (password.length() < 6) {
            throw new UserException(context.getResources().getString(R.string.password_short_exception));
        }
        if (password.length() > 20) {
            throw new UserException(context.getResources().getString(R.string.password_long_exception));
        }
        if (!password.matches("[a-zA-Z0-9]+")) {
            throw new UserException(context.getResources().getString(R.string.symbols_exception));
        }
    }
}
