package com.wordgame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wordgame.dialogs.InfoDialog;
import com.wordgame.utils.UserException;
import com.wordgame.utils.UserUtils;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

/**
 * A registration activity.
 *
 * @author Arunova Anastasia
 * @version 1.0
 * @since 1.0
 */
public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    // Log tag.
    private static final String LOG_TAG = RegistrationActivity.class.getSimpleName();

    // Registration keys - names of fields in firestore.
    private static final String REGISTRATION_NICKNAME_KEY = "nickname";

    // Users collection name - collection name in firestore.
    private static final String USERS_COLLECTION_NAME = "users";


    // Nickname EditText.
    private EditText nicknameEditText;

    // Email EditText.
    private EditText emailEditText;

    // Password EditText.
    private EditText passwordEditText;

    // Sign up button.
    private Button signUpButton;

    // TextView with a question about the existence of an account.
    private TextView existAccountTextView;


    // Firebase authentication.
    private FirebaseAuth firebaseAuth;

    // Firebase store for registered users.
    private FirebaseFirestore firebaseFirestore;

    // User id.
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        nicknameEditText = findViewById(R.id.registration_nickname_txt);
        emailEditText = findViewById(R.id.registration_email_txt);
        passwordEditText = findViewById(R.id.registration_password_txt);

        signUpButton = findViewById(R.id.login_registration_btn);
        signUpButton.setOnClickListener(this);

        existAccountTextView = findViewById(R.id.register_exist_account_tv);
        existAccountTextView.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == signUpButton.getId()) {
            createUser();
        } else if (v.getId() == existAccountTextView.getId()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }


    /**
     * Creates the user using "Fierbase Auth".
     */
    private void createUser() {
        final String nickname = String.valueOf(nicknameEditText.getText()).trim();
        final String email = String.valueOf(emailEditText.getText()).trim();
        final String password = String.valueOf(passwordEditText.getText()).trim();

        if (!isCorrectData(nickname, email, password)) {
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Not null firebase user.
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                // Send email.
                sendEmailAndAddToFirestore(nickname, firebaseUser);
            } else {
                InfoDialog infoDialog = new InfoDialog(task.getException() != null &&
                        task.getException().getMessage() != null ?
                        task.getException().getMessage() : "Unable to register");
                infoDialog.showDialog(RegistrationActivity.this);
            }
        });
    }


    /**
     * Sends email to the user email address and adds the user to a "Firestore".
     *
     * @param nickname     nickname
     * @param firebaseUser user
     */
    private void sendEmailAndAddToFirestore(final String nickname, final FirebaseUser firebaseUser) {
        firebaseUser.sendEmailVerification().addOnCompleteListener(task -> {
            InfoDialog infoDialog;
            if (task.isSuccessful()) {
                infoDialog = new InfoDialog(getResources().getString(R.string.verification_email_sent) +
                        " " + firebaseUser.getEmail());
            } else {
                infoDialog = new InfoDialog(task.getException() != null &&
                        task.getException().getMessage() != null ?
                        task.getException().getMessage() : "Unable to send email");
            }
            infoDialog.showDialog(RegistrationActivity.this);
        });

        userID = firebaseAuth.getCurrentUser().getUid();

        Map<String, Object> user = new HashMap<>();
        user.put(REGISTRATION_NICKNAME_KEY, nickname);

        firebaseFirestore.collection(USERS_COLLECTION_NAME).document(userID).set(user)
                .addOnSuccessListener(aVoid -> Log.d(LOG_TAG, "onSuccess: user added with ID " + userID))
                .addOnFailureListener(e -> Log.d(LOG_TAG, "onFailure: " + e.toString()));

        Intent intent = new Intent(this, EmailVerificationActivity.class);
        intent.putExtra("EMAIL", firebaseUser.getEmail());
        startActivity(intent);
    }


    /**
     * Returns {@code true} if entered data is correct, otherwise, {@code false}.
     *
     * @return {@code true} if entered data is correct, otherwise, {@code false}
     */
    boolean isCorrectData(String nickname, String email, String password) {
        boolean isCorrectData = true;

        try {
            UserUtils.checkNickname(this, nickname);
        } catch (UserException e) {
            isCorrectData = false;
            nicknameEditText.setError(e.getMessage());
        }

        try {
            UserUtils.checkEmail(this, email);
        } catch (UserException e) {
            isCorrectData = false;
            emailEditText.setError(e.getMessage());
        }

        try {
            UserUtils.checkPassword(this, password);
        } catch (UserException e) {
            isCorrectData = false;
            passwordEditText.setError(e.getMessage());
        }
        return isCorrectData;
    }
}
