package com.wordgame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wordgame.adapters.LobbyPayersAdapter;
import com.wordgame.dialogs.InfoDialog;
import com.wordgame.dialogs.YesNoDialog;
import com.wordgame.models.Game;
import com.wordgame.models.Player;
import com.wordgame.services.CloseAppService;
import com.wordgame.utils.ConnectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Lobby activity.
 *
 * @author Arunova Anastasia
 * @version 1.0
 * @since 1.0
 */
public class LobbyActivity extends AppCompatActivity {

    private static final String LOG_TAG = LobbyActivity.class.getSimpleName();

    private boolean firstLoad = true;

    private boolean playerQuit = false;

    private boolean isGameCreator;

    // Database reference.
    private DatabaseReference databaseReference;

    private Button startButton;

    private TextView gameCodeTextView;
    private TextView playersCountTextView;

    private LobbyPayersAdapter lobbyPayersAdapter;

    private Map<String, Player> players;

    private String playerID;

    String gameID;

    private Game game;

    private boolean gameStarted;

    private boolean activityChanged;

    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // Check network connection.
        if (!ConnectionUtils.isNetworkConnect(this)) {
            InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.network_exception));
            infoDialog.showDialog(LobbyActivity.this, new InfoDialog.OnDialogCloseListener() {
                @Override
                public void onOkClick() {
                    finish();
                }
            });
            return;
        }

        setCloseAppService();

        ProgressBar progressBar = findViewById(R.id.lobby_progress_bar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        isGameCreator = getIntent().getExtras().getBoolean("IS_GAME_CREATOR");
        gameID = getIntent().getExtras().getString("GAME_ID");
        playerID = getIntent().getExtras().getString("PLAYER_ID");

        databaseReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://wordgame-ccc6a-default-rtdb.firebaseio.com/");


        gameStarted = false;
        activityChanged = false;

        players = new HashMap<>();
        List<Player> list = new ArrayList<>(players.values());

        lobbyPayersAdapter = new LobbyPayersAdapter(this, list);
        ListView playersListView = findViewById(R.id.lobby_players_lv);
        playersListView.setAdapter(lobbyPayersAdapter);

        startButton = findViewById(R.id.lobby_start_btn);
        startButton.setVisibility(isGameCreator ? View.VISIBLE : View.INVISIBLE);
        startButton.setEnabled(isGameCreator);
        startButton.setOnClickListener(v -> startGame());

        gameCodeTextView = findViewById(R.id.lobby_game_code_tv);
        playersCountTextView = findViewById(R.id.lobby_players_count_tv);

        ImageButton back = findViewById(R.id.lobby_back_btn);
        back.setOnClickListener(v -> {
            if (isGameCreator) {
                YesNoDialog alert = new YesNoDialog(getResources().getString(R.string.quit_the_game),
                        getResources().getString(R.string.creator_leaves_game));
                alert.showDialog(LobbyActivity.this, new YesNoDialog.OnDialogCloseListener() {
                    @Override
                    public void onYesClick() {
                        deleteGame(false);
                    }
                });
            } else {
                YesNoDialog alert = new YesNoDialog(getResources().getString(R.string.quit_the_game),
                        getResources().getString(R.string.leave_game));
                alert.showDialog(LobbyActivity.this, new YesNoDialog.OnDialogCloseListener() {
                    @Override
                    public void onYesClick() {
                        deletePlayer(false);
                    }
                });
            }
        });

        loadInfoFromFirebase();
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Set service for closing an application.
     */
    private void setCloseAppService() {
        CloseAppService.onTaskCompleteListener = response -> {
            if (!gameStarted) {
                if (isGameCreator) {
                    deleteGame(true);
                } else {
                    deletePlayer(true);
                }
            }
        };

        Intent intent = new Intent(this, CloseAppService.class);
        startService(intent);
    }

    /**
     * Load information from Firebase DB.
     */
    void loadInfoFromFirebase() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!playerQuit) {
                    game = dataSnapshot.getValue(Game.class);

                    if (game == null) {
                        showGameDeletedDialog();
                        return;
                    }

                    if (!activityChanged && game.isGameStarted()) {
                        activityChanged = true;
                        startGameActivity();
                    }
                    updateViews();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        databaseReference.child(gameID).addValueEventListener(valueEventListener);
    }

    /**
     * Show game delete dialog.
     */
    private void showGameDeletedDialog() {
        playerQuit = true;
        InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.game_deleted));
        infoDialog.showDialog(LobbyActivity.this, new InfoDialog.OnDialogCloseListener() {
            @Override
            public void onOkClick() {
                quit();
            }
        });
    }

    /**
     * Update list view and text views.
     */
    private void updateViews() {
        if (firstLoad && isGameCreator) {
            InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.lobby_code) +
                    System.lineSeparator() + game.getCode());
            infoDialog.showDialog(LobbyActivity.this);
            firstLoad = false;
        }
        gameCodeTextView.setText(game.getCode());
        players = game.getPlayers();
        playersCountTextView.setText(String.valueOf(players.size() + " / " + game.getMaxPlayersNumber()));
        lobbyPayersAdapter.setPlayers(new ArrayList<>(players.values()));
        lobbyPayersAdapter.notifyDataSetChanged();
    }

    /**
     * Delete the game.
     * @param appWasClosed {@code true} if the application was closed, otherwise {@code true}
     */
    private void deleteGame(boolean appWasClosed) {
        playerQuit = true;

        databaseReference.child(gameID).removeValue();

        if (!appWasClosed) {
            quit();
        }
    }

    /**
     * Delete the player.
     * @param appWasClosed {@code true} if the application was closed, otherwise {@code true}
     */
    private void deletePlayer(boolean appWasClosed) {
        databaseReference.child(gameID).child("players").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String key = null;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Log.d(LOG_TAG, child.toString());
                            Player player = child.getValue(Player.class);
                            if (player.getUserID().equals(playerID)) {
                                key = child.getKey();
                                playerQuit = true;
                                break;
                            }
                        }

                        if (key != null) {
                            dataSnapshot.child(key).getRef().removeValue();
                            if (!appWasClosed) {
                                quit();
                            }
                        } else if (!playerQuit) {
                            InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.quit_failed));
                            infoDialog.showDialog(LobbyActivity.this, new InfoDialog.OnDialogCloseListener() {
                                @Override
                                public void onOkClick() {
                                    if (!appWasClosed) {
                                        quit();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    /**
     * Quit the game.
     */
    private void quit() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * Start the game.
     */
    private void startGame() {
        gameStarted = true;

        databaseReference.child(gameID).child("gameStarted").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().setValue(true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    /**
     * Start game activity.
     */
    private void startGameActivity() {
        databaseReference.child(gameID).removeEventListener(valueEventListener);

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("GAME_ID", gameID);
        intent.putExtra("PLAYER_ID", playerID);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
