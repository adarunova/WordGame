package com.wordgame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.wordgame.dialogs.InfoDialog;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Reset password activity.
 *
 * @author Arunova Anastasia
 * @version 1.0
 * @since 1.0
 */
public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ImageButton back = findViewById(R.id.verification_back_btn);
        back.setOnClickListener(v -> finish());

        final EditText editText = findViewById(R.id.reset_password_txt);

        Button button = findViewById(R.id.reset_password_btn);
        button.setOnClickListener(v -> {
            final String email = String.valueOf(editText.getText());
            if (email == null || email.isEmpty()) {
                editText.setError(getResources().getString(R.string.email_empty_exception));
                return;
            }
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.reset_password_email_sent) + " " + email);
                    infoDialog.showDialog(ResetPasswordActivity.this, new InfoDialog.OnDialogCloseListener() {
                        @Override
                        public void onOkClick() {
                            finish();
                        }
                    });
                } else {
                    InfoDialog infoDialog = new InfoDialog(task.getException() != null &&
                            task.getException().getMessage() != null ?
                            task.getException().getMessage() :
                            getResources().getString(R.string.reset_password_email_sent_failed));
                    infoDialog.showDialog(ResetPasswordActivity.this);
                }
            });
        });
    }
}
