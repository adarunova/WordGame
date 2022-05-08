package com.wordgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


/**
 * Email verification activity.
 *
 * @author Arunova Anastasia
 * @version 1.0
 * @since 1.0
 */
public class EmailVerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        // TextView with user's email.
        final TextView emailTextView = findViewById(R.id.verification_email_tv);
        String email = getIntent().getExtras().get("EMAIL").toString();
        emailTextView.setText(email);

        // Button for leaving this activity.
        final Button doneButton = findViewById(R.id.verification_done_btn);
        doneButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Back button.
        final ImageButton backButton = findViewById(R.id.verification_back_btn);
        backButton.setOnClickListener(v -> finish());
    }
}
