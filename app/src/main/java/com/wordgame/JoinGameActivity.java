package com.wordgame;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wordgame.dialogs.InfoDialog;
import com.wordgame.models.Game;
import com.wordgame.models.Player;
import com.wordgame.utils.ConnectionUtils;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Join game activity.
 *
 * @author Arunova Anastasia
 * @version 1.0
 * @since 1.0
 */
public class JoinGameActivity extends AppCompatActivity {

    private static final String LOG_TAG = JoinGameActivity.class.getSimpleName();

    // Database reference.
    private DatabaseReference databaseReference;

    EditText[] codeEditTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        // Check network connection.
        if (!ConnectionUtils.isNetworkConnect(this)) {
            InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.network_exception));
            infoDialog.showDialog(JoinGameActivity.this, new InfoDialog.OnDialogCloseListener() {
                @Override
                public void onOkClick() {
                    finish();
                }
            });
            return;
        }

        databaseReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://wordgame-ccc6a-default-rtdb.firebaseio.com/");

        ImageButton back = findViewById(R.id.join_game_back_btn);
        back.setOnClickListener(v -> finish());

        setOTPEditTexts();

        Button joinButton = findViewById(R.id.join_game_join_btn);

        joinButton.setOnClickListener(v -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (EditText editText : codeEditTexts) {
                String part = editText.getText().toString();
                if (part.isEmpty() || part.length() > 1 || !part.matches("[A-Z0-9]+")) {
                    editText.setError(getResources().getString(R.string.game_code_exception));
                } else {
                    stringBuilder.append(part);
                }
            }

            if (stringBuilder.length() < 6) {
                return;
            }
            joinGame(stringBuilder.toString());
        });
    }

    /**
     * Set edit texts.
     */
    private void setOTPEditTexts() {
        codeEditTexts = new EditText[6];

        codeEditTexts[0] = findViewById(R.id.join_game_code1_txt);
        codeEditTexts[1] = findViewById(R.id.join_game_code2_txt);
        codeEditTexts[2] = findViewById(R.id.join_game_code3_txt);
        codeEditTexts[3] = findViewById(R.id.join_game_code4_txt);
        codeEditTexts[4] = findViewById(R.id.join_game_code5_txt);
        codeEditTexts[5] = findViewById(R.id.join_game_code6_txt);

        for (int i = 0; i < codeEditTexts.length; i++) {
            final int index = i;
            codeEditTexts[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    codeEditTexts[index].setSelection(codeEditTexts[index].getText().length());
                    String text = s.toString();
                    if (!text.isEmpty()) {
                        if (text.length() > 1) {
                            text = String.valueOf(text.charAt(0));
                            codeEditTexts[index].setText(text);
                        }

                        if (!text.toUpperCase().equals(text)) {
                            text = text.toUpperCase();
                            codeEditTexts[index].setText(text);
                        }

                        if (!text.matches("[A-Z0-9]+")) {
                            codeEditTexts[index].setError(getResources().getString(R.string.game_code_exception));
                            return;
                        }

                        if (index < 5) {
                            codeEditTexts[index + 1].setText("");
                            codeEditTexts[index + 1].requestFocus();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    /**
     * Join game.
     *
     * @param code game code
     */
    private void joinGame(String code) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.user_null));
            infoDialog.showDialog(JoinGameActivity.this, new InfoDialog.OnDialogCloseListener() {
                @Override
                public void onOkClick() {
                    finish();
                }
            });
            return;
        }

        final String userID = firebaseUser.getUid();

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").document(userID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String nickname = documentSnapshot.getString("nickname");
                    if (nickname == null || nickname.isEmpty()) {
                        nickname = firebaseUser.getEmail();
                    }

                    Player player = new Player(firebaseUser.getUid(), nickname, firebaseUser.getEmail());

                    enterLobby(code, player);
                })
                .addOnFailureListener(e -> {
                    InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.user_null));
                    infoDialog.showDialog(JoinGameActivity.this, new InfoDialog.OnDialogCloseListener() {
                        @Override
                        public void onOkClick() {
                            finish();
                        }
                    });
                    Log.d(LOG_TAG, "FAIL: " + e.getMessage());
                });
    }

    /**
     * Enter game.
     *
     * @param code   game code
     * @param player player
     */
    private void enterLobby(String code, Player player) {
        databaseReference.orderByChild("code").equalTo(code).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            InfoDialog infoDialog = new InfoDialog(getResources()
                                    .getString(R.string.game_with_code_does_not_exist));
                            infoDialog.showDialog(JoinGameActivity.this);
                        } else {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Game game = child.getValue(Game.class);

                                if (game.getPlayers().size() >= game.getMaxPlayersNumber()) {
                                    InfoDialog infoDialog = new InfoDialog(getResources()
                                            .getString(R.string.max_players_number_exceeded));
                                    infoDialog.showDialog(JoinGameActivity.this);
                                } else if (game.isGameStarted()) {
                                    InfoDialog infoDialog = new InfoDialog(getResources()
                                            .getString(R.string.game_started));
                                    infoDialog.showDialog(JoinGameActivity.this);
                                } else {
                                    game.getPlayers().put(player.getUserID(), player);

                                    databaseReference.child(child.getKey()).child("players")
                                            .setValue(game.getPlayers());

                                    startLobbyActivity(child.getKey(), player);
                                }
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOG_TAG, "onCancelled: " + databaseError.getMessage());
                    }
                });
    }

    /**
     * Start lobby activity.
     *
     * @param gameID game ID.
     * @param player player
     */
    private void startLobbyActivity(String gameID, Player player) {
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra("IS_GAME_CREATOR", false);
        intent.putExtra("GAME_ID", gameID);
        intent.putExtra("PLAYER_ID", player.getUserID());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
