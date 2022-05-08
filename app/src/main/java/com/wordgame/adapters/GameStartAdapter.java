package com.wordgame.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wordgame.R;
import com.wordgame.interfaces.OnLoadCountDownEndListener;

/**
 * Adapter for game start screen.
 *
 * @author Arunova Margarita
 * @version 1.0
 * @since 1.0
 */
public class GameStartAdapter extends BaseAdapter {
    private static final int COUNT_DOWN_SECONDS = 3;
    private static final String[] countDownNumberColors = {"#7ca6e6", "#a4dbcc", "#f9fa6e64", "#ffffff"};

    private final LayoutInflater inflater;
    private final OnLoadCountDownEndListener listener;

    public GameStartAdapter(Context context, OnLoadCountDownEndListener onCountDownEndListener) {
        inflater = LayoutInflater.from(context);
        listener = onCountDownEndListener;
    }

    @Override
    public int getCount() {
        return COUNT_DOWN_SECONDS + 1;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.game_start_layout, null);

        final AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);

        final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(500);
        fadeOut.setFillAfter(true);
        fadeOut.setStartOffset(fadeIn.getStartOffset() + fadeIn.getDuration());

        final TextView countDownNumber = view.findViewById(R.id.game_start_text);
        countDownNumber.setText(String.valueOf(COUNT_DOWN_SECONDS - i));
        countDownNumber.setTextColor(Color.parseColor(countDownNumberColors[i]));
        countDownNumber.startAnimation(fadeIn);
        countDownNumber.startAnimation(fadeOut);

        if (COUNT_DOWN_SECONDS - i == 0) {
            listener.onLoadCountDownEnd();
        }
        return view;
    }
}
