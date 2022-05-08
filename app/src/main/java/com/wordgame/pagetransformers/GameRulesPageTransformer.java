package com.wordgame.pagetransformers;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Game rules page transformer.
 *
 * @author Arunova Margarita
 * @version 1.0
 * @since 1.0
 */
public class GameRulesPageTransformer implements ViewPager2.PageTransformer {
    private static final float SCALE_LOWER_BOUND = 0.7f;

    public void transformPage(@NonNull View view, float position) {
        if (position < -1 || position > 1) {
            view.setAlpha(0f);
            return;
        }

        if (position <= 0) {
            view.setScaleX(1f);
            view.setScaleY(1f);
            view.setAlpha(1f);
            view.setTranslationX(0f);
            view.setTranslationZ(0f);
            return;
        }

        float scaleFactor = SCALE_LOWER_BOUND
                + (1 - SCALE_LOWER_BOUND) * (1 - Math.abs(position));
        view.setScaleX(scaleFactor);
        view.setScaleY(scaleFactor);
        view.setAlpha(1 - position);
        view.setTranslationX(view.getWidth() * -position);
        view.setTranslationZ(-1f);
    }
}
