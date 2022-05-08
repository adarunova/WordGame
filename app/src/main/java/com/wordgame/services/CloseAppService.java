package com.wordgame.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.wordgame.interfaces.OnTaskCompleteListener;

/**
 * Service for closing the application.
 */
public class CloseAppService extends Service {

    // Listener for closing the application event.
    public static OnTaskCompleteListener onTaskCompleteListener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        onTaskCompleteListener.onTaskCompleted(true);

        stopSelf();
    }
}
