package com.wordgame;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * Main activity.
 *
 * @author Arunova Margarita
 * @version 1.0
 * @since 1.0
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addLettersAnimation(findViewById(R.id.main_screen_layout));

        findViewById(R.id.profile_btn).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.feedback_btn).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
            startActivity(intent);
        });


        findViewById(R.id.join_game_btn).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), JoinGameActivity.class);
            startActivity(intent);
        });


        findViewById(R.id.create_game_bth).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CreateGameActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.game_rules_btn).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), GameRulesActivity.class);
            startActivity(intent);
        });
    }


    private void addLettersAnimation(RelativeLayout layout) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        int moveStep = screenHeight / 10;

        Integer[] drawables = {
                R.drawable.letter_animation_a,
                R.drawable.letter_animation_s,
                R.drawable.letter_animation_d,
                R.drawable.letter_animation_u
        };
        Integer[] coordinatesFirst = {
                -moveStep, screenHeight / 2,
                screenWidth + moveStep, screenHeight * 2 / 3 - 2 * moveStep,
                -moveStep, screenHeight,
                screenWidth + moveStep, screenHeight + moveStep
        };
        Integer[] coordinatesSecond = {
                moveStep, screenHeight / 2 + moveStep,
                screenWidth - 2 * moveStep, screenHeight * 2 / 3 + moveStep,
                moveStep * 2, screenHeight - 3 * moveStep,
                screenWidth / 2, screenHeight - 2 * moveStep
        };
        Integer[] coordinatesThird = {
                2 * moveStep, screenHeight / 2 - 2 * moveStep,
                screenWidth - 3 * moveStep, screenHeight * 2 / 3,
                moveStep, screenHeight - moveStep,
                screenWidth - moveStep, screenHeight - moveStep
        };
        Integer[] durations = {18000, 16000, 14000, 15000};
        Float[] fraction = {0.3f, 0.4f, 0.7f, 0.3f};

        for (int i = 0; i < 4; i++) {
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
