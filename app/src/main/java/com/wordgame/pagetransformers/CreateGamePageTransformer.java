package com.wordgame.pagetransformers;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Game creation page transformer.
 *
 * @author Arunova Margarita
 * @version 1.0
 * @since 1.0
 */
public class CreateGamePageTransformer implements ViewPager2.PageTransformer {
    private static final float SCALE_LOWER_BOUND = 0.85f;
    private static final float ALPHA_LOWER_BOUND = 0.5f;

    @Override
    public void transformPage(@NonNull View view, float position) {
        if (position < -1 || position > 1) {
            view.setAlpha(0f);
            return;
        }

        float scale = Math.max(SCALE_LOWER_BOUND, 1 - Math.abs(position));
        float alpha = (scale - SCALE_LOWER_BOUND) / (1 - SCALE_LOWER_BOUND) *
                (1 - ALPHA_LOWER_BOUND) + ALPHA_LOWER_BOUND;

        view.setTranslationX(getTransition(view, position, scale));
        view.setScaleX(scale);
        view.setScaleY(scale);
        view.setAlpha(alpha);
    }

    private float getTransition(View view, float position, float scale) {
        float transition = (1.0f - scale) / 2.0f * (view.getHeight() - view.getWidth() / 2.0f);

        if (position > 0) {
            transition *= -1;
        }
        return transition;
    }
}
