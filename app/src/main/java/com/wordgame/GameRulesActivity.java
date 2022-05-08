package com.wordgame;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.wordgame.adapters.GameRulesViewAdapter;
import com.wordgame.pagetransformers.GameRulesPageTransformer;

/**
 * Game rules activity.
 *
 * @author Arunova Margarita
 * @version 1.0
 * @since 1.0
 */
public class GameRulesActivity extends AppCompatActivity {

    private GameRulesViewAdapter gameRulesAdapter;

    private ViewPager2 gameRulesViewpager;

    private LinearLayout indicator;

    private Button pageControlButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_rules);

        findViewById(R.id.game_rules_back_button).setOnClickListener(view -> finish());

        // Written by Arunova Margarita.
        gameRulesAdapter = new GameRulesViewAdapter();
        gameRulesViewpager = findViewById(R.id.game_rules_viewpager);
        gameRulesViewpager.setAdapter(gameRulesAdapter);
        gameRulesViewpager.setPageTransformer(new GameRulesPageTransformer());
        gameRulesViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                changeIndicatorState(position);
            }
        });
        gameRulesViewpager.setOffscreenPageLimit(4);

        indicator = findViewById(R.id.rules_indicator);
        pageControlButton = findViewById(R.id.rules_indicator_button);
        pageControlButton.setOnClickListener(v -> {
            if (gameRulesViewpager.getCurrentItem() + 1 < gameRulesAdapter.getItemCount()) {
                gameRulesViewpager.setCurrentItem(gameRulesViewpager.getCurrentItem() + 1);
            } else {
                pageControlButton.setEnabled(false);
                finish();
            }
        });

        initIndicatorState();
        changeIndicatorState(0);
        addLettersAnimation(findViewById(R.id.rules_layout));
    }

    private void initIndicatorState() {
        ImageView[] dots = new ImageView[gameRulesAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 20, 0);
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(getApplicationContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(), R.drawable.dot_inactive));
            dots[i].setLayoutParams(layoutParams);
            indicator.addView(dots[i]);
        }
    }


    private void changeIndicatorState(int index) {
        if (index == gameRulesAdapter.getItemCount() - 1) {
            pageControlButton.setText("OK");
        } else {
            pageControlButton.setText(R.string.next_button);
        }
        int itemCount = indicator.getChildCount();
        for (int i = 0; i < itemCount; i++) {
            if (i == index) {
                ((ImageView) indicator.getChildAt(i)).setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.dot_active));
            } else {
                ((ImageView) indicator.getChildAt(i)).setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.dot_inactive));
            }
        }
    }

    private void addLettersAnimation(RelativeLayout layout) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        int moveStep = screenHeight / 10;

        Integer[] drawables = {
                R.drawable.letter_animation_r,
                R.drawable.letter_animation_f,
                R.drawable.letter_animation_b,
        };
        Integer[] coordinatesFirst = {
                -moveStep, screenHeight / 5,
                screenWidth + moveStep, screenHeight * 2 / 3,
                -moveStep, screenHeight
        };
        Integer[] coordinatesSecond = {
                screenWidth / 3, screenHeight / 2,
                screenWidth / 2, screenHeight * 2 / 3 - 2 * moveStep,
                moveStep * 2, screenHeight - 3 * moveStep
        };
        Integer[] coordinatesThird = {
                screenWidth / 2, screenHeight / 2 + 2 * moveStep,
                screenWidth * 2 / 3, screenHeight * 2 / 3 - 4 * moveStep,
                moveStep * 4, screenHeight - moveStep
        };
        Integer[] durations = {18000, 16000, 16000};
        Float[] fraction = {0.3f, 0.4f, 0.7f};

        for (int i = 0; i < 3; i++) {
            Drawable drawable = ContextCompat.getDrawable(this, drawables[i]);
            if (drawable == null) {
                continue;
            }

            ImageView letter = new ImageView(this);
            letter.setImageDrawable(drawable);
            letter.setAdjustViewBounds(true);
            letter.setX(coordinatesFirst[i * 2]);
            letter.setY(coordinatesFirst[i * 2 + 1]);
            letter.setZ(-1f);

            Path path = new Path();
            path.moveTo(coordinatesFirst[i * 2], coordinatesFirst[i * 2 + 1]);
            path.lineTo(coordinatesSecond[i * 2], coordinatesSecond[i * 2 + 1]);
            path.lineTo(coordinatesThird[i * 2], coordinatesThird[i * 2 + 1]);
            path.lineTo(coordinatesFirst[i * 2], coordinatesFirst[i * 2 + 1]);
            path.close();

            ObjectAnimator moveAnimation = ObjectAnimator
                    .ofFloat(letter, View.X, View.Y, path);
            moveAnimation.setDuration(durations[i]);
            moveAnimation.setRepeatCount(ObjectAnimator.INFINITE);
            moveAnimation.setCurrentFraction(fraction[i]);
            moveAnimation.start();

            ObjectAnimator rotateAnimation = ObjectAnimator
                    .ofFloat(letter, "rotation", 0f, 360f);
            rotateAnimation.setDuration(durations[i]);
            rotateAnimation.setRepeatCount(ObjectAnimator.INFINITE);
            rotateAnimation.setCurrentFraction(fraction[i]);
            rotateAnimation.start();

            layout.addView(letter);
        }
    }
}
