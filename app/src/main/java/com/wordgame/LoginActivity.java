package com.wordgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseNetworkException;
import com.wordgame.dialogs.InfoDialog;
import com.wordgame.dialogs.YesNoDialog;

import androidx.appcompat.app.AppCompatActivity;

/**
 * A login activity.
 *
 * @author Arunova Anastasia
 * @version 1.0
 * @since 1.0
 */
public class LoginActivity extends AppCompatActivity {

    // Firebase authentication.
    private FirebaseAuth firebaseAuth;

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        firebaseAuth = FirebaseAuth.getInstance();

        final Button signUpButton = findViewById(R.id.login_registration_btn);
        signUpButton.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class)));

        emailEditText = findViewById(R.id.login_email_txt);
        passwordEditText = findViewById(R.id.login_password_txt);

        final TextView forgotPasswordTextView = findViewById(R.id.forgot_password);
        forgotPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
            startActivity(intent);
        });

        final ImageButton loginButton = findViewById(R.id.login_next_btn);
        loginButton.setOnClickListener(v -> loginUser(
                String.valueOf(emailEditText.getText()),
                String.valueOf(passwordEditText.getText())
        ));
    }

    /**
     * Login the user.
     *
     * @param email    email
     * @param password password
     */
    private void loginUser(String email, String password) {
        boolean isCorrectInput = true;
        if (email == null || email.isEmpty()) {
            isCorrectInput = false;
            emailEditText.setError(getResources().getString(R.string.email_empty_exception));
        }
        if (password == null || password.isEmpty()) {
            isCorrectInput = false;
            passwordEditText.setError(getResources().getString(R.string.password_empty_exception));
        }

        if (!isCorrectInput) {
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = task.getResult().getUser();
                if (user.isEmailVerified()) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    showDialogForUnverifiedUser(user);
                }
            } else {
                InfoDialog infoDialog;
                if (task.getException() != null &&
                        task.getException().getClass() == FirebaseNetworkException.class) {
                    infoDialog = new InfoDialog(getResources().getString(R.string.network_exception));
                } else {
                    infoDialog = new InfoDialog(getResources().getString(R.string.unsuccessful_sign_in));
                }
                infoDialog.showDialog(LoginActivity.this);
            }
        });
    }


    /**
     * Shows a dialog with the message
     *
     * @param user a user
     */
    private void showDialogForUnverifiedUser(final FirebaseUser user) {
        YesNoDialog alert = new YesNoDialog(
                getResources().getString(R.string.login_failed),
                getResources().getString(R.string.have_not_verify_email)
        );

        alert.showDialog(LoginActivity.this, new YesNoDialog.OnDialogCloseListener() {
            @Override
            public void onYesClick() {
                user.sendEmailVerification().addOnCompleteListener(task -> {
                    InfoDialog infoDialog;
                    if (task.isSuccessful()) {
                        infoDialog = new InfoDialog(getResources().getString(R.string.verification_email_sent) +
                                " " + user.getEmail());
                    } else {
                        infoDialog = new InfoDialog(task.getException() != null &&
                                task.getException().getMessage() != null ?
                                task.getException().getMessage() : "Unable to sent email");
                    }
                    infoDialog.showDialog(LoginActivity.this);
                });
            }
        });
    }
}
