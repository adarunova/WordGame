package com.wordgame;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wordgame.dialogs.InfoDialog;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Feedback activity.
 *
 * @author Arunova Anastasia
 * @version 1.0
 * @since 1.0
 */
public class FeedbackActivity extends AppCompatActivity {

    // Log tag.
    private static final String LOG_TAG = FeedbackActivity.class.getSimpleName();

    // Feedback key - name of field in firestore.
    private static final String FEEDBACK_KEY = "feedback";

    // Feedback collection name - collection name in firestore.
    private static final String FEEDBACK_COLLECTION_NAME = "feedback";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        ImageButton back = findViewById(R.id.feedback_back_btn);
        back.setOnClickListener(v -> finish());

        final EditText editText = findViewById(R.id.feedback_txt);

        Button button = findViewById(R.id.send_btn);
        button.setOnClickListener(v -> {
            if (editText.getText() == null || editText.getText().toString().trim().isEmpty()) {
                InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.feedback_empty_exception));
                infoDialog.showDialog(FeedbackActivity.this);
                return;
            }

            sendFeedback(editText.getText().toString().trim());
        });
    }

    /**
     * Sends feedback.
     *
     * @param feedback feedback
     */
    private void sendFeedback(String feedback) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.feedback_unable_to_sent_exception));
            infoDialog.showDialog(FeedbackActivity.this);
            return;
        }

        String userID = firebaseAuth.getCurrentUser().getUid();

        Map<String, Object> user = new HashMap<>();
        user.put(FEEDBACK_KEY, feedback);

        firebaseFirestore.collection(FEEDBACK_COLLECTION_NAME).document(userID).set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(LOG_TAG, "onSuccess: feedback added with ID " + userID);

                    InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.feedback_has_been_sent));
                    infoDialog.showDialog(FeedbackActivity.this, new InfoDialog.OnDialogCloseListener() {
                        @Override
                        public void onOkClick() {
                            finish();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.d(LOG_TAG, "onFailure: " + e.toString());

                    InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.feedback_unable_to_sent_exception));
                    infoDialog.showDialog(FeedbackActivity.this);
                });
    }
}
