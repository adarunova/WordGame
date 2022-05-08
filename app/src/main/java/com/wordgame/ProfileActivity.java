package com.wordgame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wordgame.dialogs.ChangePasswordDialog;
import com.wordgame.dialogs.EditDialog;
import com.wordgame.dialogs.InfoDialog;
import com.wordgame.dialogs.YesNoDialog;
import com.wordgame.utils.ConnectionUtils;
import com.wordgame.utils.UserException;
import com.wordgame.utils.UserUtils;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Profile activity.
 *
 * @author Arunova Anastasia
 * @version 1.0
 * @since 1.0
 */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FloatingActionButton exitButton;
    FloatingActionButton logoutButton;
    FloatingActionButton deleteAccountButton;

    Animation fabOpen;
    Animation fabClose;
    Animation rotateForward;
    Animation rotateBackward;

    TextView emailTextView;
    TextView nicknameTextView;
    TextView passwordTextView;
    TextView profileIconTextView;

    ImageButton back;

    ProgressBar progressBar;

    boolean isOpenFab = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        progressBar = findViewById(R.id.profile_progress_bar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        back = findViewById(R.id.profile_back_btn);

        emailTextView = findViewById(R.id.profile_email_tv);
        nicknameTextView = findViewById(R.id.profile_nickname_tv);
        passwordTextView = findViewById(R.id.profile_password_tv);
        profileIconTextView = findViewById(R.id.profile_circle_tv);

        setTextViews();

        exitButton = findViewById(R.id.profile_exit_fab);
        logoutButton = findViewById(R.id.profile_logout_fab);
        deleteAccountButton = findViewById(R.id.profile_delete_account_fab);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        back.setOnClickListener(this);
        nicknameTextView.setOnClickListener(this);
        passwordTextView.setOnClickListener(this);
        exitButton.setOnClickListener(this);
        deleteAccountButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

    }

    private void setTextViews() {
        if (firebaseUser == null) {
            InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.user_null));
            infoDialog.showDialog(ProfileActivity.this, new InfoDialog.OnDialogCloseListener() {
                @Override
                public void onOkClick() {
                    finish();
                }
            });
            return;
        }

        String userID = firebaseUser.getUid();

        firebaseFirestore.collection("users").document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String nickname = documentSnapshot.getString("nickname");
                    if (nickname == null || nickname.isEmpty()) {
                        nickname = "Nickname";
                    }
                    emailTextView.setText(firebaseUser.getEmail());
                    nicknameTextView.setText(nickname);
                    profileIconTextView.setText(String.valueOf(nickname.toUpperCase().charAt(0)));

                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);

                    InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.user_null));
                    infoDialog.showDialog(ProfileActivity.this, new InfoDialog.OnDialogCloseListener() {
                        @Override
                        public void onOkClick() {
                            finish();
                        }
                    });
                });

    }

    /**
     * Animates floating buttons.
     */
    private void animateExitButton() {
        if (isOpenFab) {
            exitButton.startAnimation(rotateForward);
            logoutButton.startAnimation(fabClose);
            deleteAccountButton.startAnimation(fabClose);
        } else {
            exitButton.startAnimation(rotateBackward);
            logoutButton.startAnimation(fabOpen);
            deleteAccountButton.startAnimation(fabOpen);
        }

        isOpenFab = !isOpenFab;
        logoutButton.setClickable(isOpenFab);
        deleteAccountButton.setClickable(isOpenFab);
    }

    private void logout() {
        YesNoDialog alert = new YesNoDialog(
                getResources().getString(R.string.logout_title),
                getResources().getString(R.string.logout_question)
        );
        alert.showDialog(ProfileActivity.this, new YesNoDialog.OnDialogCloseListener() {
            @Override
            public void onYesClick() {
                firebaseAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    /**
     * Delete account.
     */
    private void deleteAccount() {
        YesNoDialog alert = new YesNoDialog(
                getResources().getString(R.string.delete_account_title),
                getResources().getString(R.string.delete_account_question)
        );
        alert.showDialog(ProfileActivity.this, new YesNoDialog.OnDialogCloseListener() {
            @Override
            public void onYesClick() {
                if (firebaseUser == null) {
                    // INFO
                    return;
                }

                String userID = firebaseUser.getUid();

                firebaseFirestore.collection("users").document(userID).delete()
                        .addOnSuccessListener(aVoid -> Log.d("DELETE", "Firestore document deleted successfully"))
                        .addOnFailureListener(e -> Log.d("DELETE", "Firestore document wasn't deleted"));

                firebaseUser.delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("DELETE", "Firebase Auth user deleted successfully");

                        InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.user_deleted));
                        infoDialog.showDialog(ProfileActivity.this, new InfoDialog.OnDialogCloseListener() {
                            @Override
                            public void onOkClick() {
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });

                    } else {
                        Log.d("DELETE", "Firebase Auth user wasn't deleted");

                        InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.user_delete_failed));
                        infoDialog.showDialog(ProfileActivity.this);
                    }
                });
            }
        });
    }

    /**
     * Edit nickname.
     */
    private void editNickname() {
        EditDialog alert = new EditDialog(
                getResources().getString(R.string.edit_nickname),
                getResources().getString(R.string.nickname_hint)
        );
        alert.showDialog(ProfileActivity.this, new EditDialog.OnDialogCloseListener() {
            @Override
            public void onYesClick(EditText editText) {
                String text = editText.getText().toString().trim();
                try {
                    UserUtils.checkNickname(getApplicationContext(), text);
                } catch (UserException e) {
                    editText.setError(e.getMessage());
                    return;
                }
                updateNicknameInFirestore(text);
            }
        });
    }

    /**
     * Update the nickname.
     * @param newNickname the new nickname
     */
    private void updateNicknameInFirestore(String newNickname) {
        if (firebaseUser == null) {
            InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.user_update_nickname_failed));
            infoDialog.showDialog(ProfileActivity.this);
            return;
        }

        String userID = firebaseUser.getUid();

        firebaseFirestore.collection("users").document(userID)
                .update("nickname", newNickname).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                nicknameTextView.setText(newNickname);
                profileIconTextView.setText(String.valueOf(newNickname.toUpperCase().charAt(0)));
            }
        });
    }

    /**
     * Change the password.
     */
    private void changePassword() {
        ChangePasswordDialog alert = new ChangePasswordDialog();

        alert.showDialog(ProfileActivity.this, new ChangePasswordDialog.OnDialogCloseListener() {
            @Override
            public void onYesClick(String oldPassword, String newPassword) {
                if (firebaseUser == null) {
                    InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.password_change_failed));
                    infoDialog.showDialog(ProfileActivity.this);
                    return;
                }

                AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), oldPassword);
                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        firebaseUser.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                            InfoDialog infoDialog;
                            if (task1.isSuccessful()) {
                                Log.d("CHANGE_PASSWORD", "Password updated");
                                infoDialog = new InfoDialog("Password updated");
                            } else {
                                Log.d("CHANGE_PASSWORD", "Error: password not updated");
                                infoDialog = new InfoDialog("Error: password not updated");
                            }
                            infoDialog.showDialog(ProfileActivity.this);
                        });
                    } else {
                        Log.d("CHANGE_PASSWORD", "Error auth failed");
                        InfoDialog infoDialog = new InfoDialog(task.getException() != null &&
                                task.getException().getMessage() != null ?
                                task.getException().getMessage() : "Error auth failed");
                        infoDialog.showDialog(ProfileActivity.this);
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (progressBar.getVisibility() == View.VISIBLE) {
            return;
        }

        if (v.getId() == back.getId()) {
            finish();
            return;
        } else if (v.getId() == exitButton.getId()) {
            animateExitButton();
            return;
        } else if (v.getId() == logoutButton.getId()) {
            logout();
            return;
        }

        // Check network connection.
        if (!ConnectionUtils.isNetworkConnect(this)) {
            InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.network_exception));
            infoDialog.showDialog(ProfileActivity.this);
            return;
        }

        if (v.getId() == deleteAccountButton.getId()) {
            deleteAccount();
        } else if (v.getId() == nicknameTextView.getId()) {
            editNickname();
        } else if (v.getId() == passwordTextView.getId()) {
            changePassword();
        }
    }
}
