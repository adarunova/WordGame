package com.wordgame.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Connection utils.
 *
 * @author Arunova Anastasia
 * @version 1.0
 * @since 1.0
 */
public final class ConnectionUtils {

    /**
     * Check network connection.
     * @param context context
     * @return {@code true} if network is connected, otherwise {@code false}
     */
    public static boolean isNetworkConnect(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
}
