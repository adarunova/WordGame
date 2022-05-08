package com.wordgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wordgame.adapters.ResultsListAdapter;
import com.wordgame.dialogs.InfoDialog;
import com.wordgame.interfaces.OnTaskCompleteListener;
import com.wordgame.models.Game;
import com.wordgame.models.Player;
import com.wordgame.utils.ConnectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Game activity.
 *
 * @author Arunova Anastasia and Margarita
 * @version 1.0
 * @since 1.0
 */
public class GameResultsActivity extends AppCompatActivity {

    // Player id (written by Arunova Anastasia).
    private String playerID;

    // Game id (written by Arunova Anastasia).
    private String gameID;

    // Game (written by Arunova Anastasia).
    private Game game;

    // Database reference (written by Arunova Anastasia).
    private DatabaseReference databaseReference;

    // Written by Arunova Anastasia.
    ProgressBar progressBar;

    // Written by Arunova Anastasia.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_results);

        // Check network connection.
        if (!ConnectionUtils.isNetworkConnect(this)) {
            InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.network_exception));
            infoDialog.showDialog(GameResultsActivity.this, new InfoDialog.OnDialogCloseListener() {
                @Override
                public void onOkClick() {
                    finish();
                }
            });
            return;
        }

        progressBar = findViewById(R.id.game_results_progress_bar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        databaseReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://wordgame-ccc6a-default-rtdb.firebaseio.com/");

        playerID = getIntent().getExtras().getString("PLAYER_ID");
        gameID = getIntent().getExtras().getString("GAME_ID");

        loadGame(response -> {
            progressBar.setVisibility(View.GONE);
            if (response && game != null) {
                setViews();
                databaseReference.child(gameID).child("completeGamePlayersNumber")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot != null) {
                                    Long number = (Long) dataSnapshot.getValue();
                                    if (number != null && game.getPlayers().size() <= number) {
                                        databaseReference.child(gameID).removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            } else {
                InfoDialog infoDialog = new InfoDialog("Failed to load results");
                infoDialog.showDialog(GameResultsActivity.this, new InfoDialog.OnDialogCloseListener() {
                    @Override
                    public void onOkClick() {

                    }
                });
            }
        });
    }


    // Written by Arunova Anastasia.

    /**
     * Loads the game.
     *
     * @param onTaskCompleteListener task end listener
     */
    private void loadGame(OnTaskCompleteListener onTaskCompleteListener) {
        databaseReference.child(gameID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                game = dataSnapshot.getValue(Game.class);
                dataSnapshot.child("players").child(playerID).child("completeWatchingResults")
                        .getRef().setValue(true);
                int playersCountComplete = 0;
                if (game != null) {
                    for (Player player : game.getPlayers().values()) {
                        if (player.isCompleteWatchingResults()) {
                            playersCountComplete++;
                        }
                    }
                }
                dataSnapshot.child("completeGamePlayersNumber").getRef()
                        .setValue(playersCountComplete);
                onTaskCompleteListener.onTaskCompleted(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onTaskCompleteListener.onTaskCompleted(false);
            }
        });
    }

    // Written by Arunova Margarita.
    private void setViews() {
        ImageButton quit = findViewById(R.id.results_quit_btn);
        quit.setOnClickListener(view -> quitGame());

        List<Player> players = new ArrayList<>(game.getPlayers().values());
        Collections.sort(players, (player1, player2) -> Integer.compare(player2.getPoints(), player1.getPoints()));

        List<Pair<Player, Integer>> ranking = new ArrayList<>();
        int place = 1;
        for (int i = 0; i < players.size(); i++) {
            if (i > 0 && players.get(i - 1).getPoints() > players.get(i).getPoints()) {
                ++place;
            }
            ranking.add(new Pair<>(players.get(i), place));
        }

        ListView resultsList = findViewById(R.id.results_list);
        ResultsListAdapter adapter = new ResultsListAdapter(this, R.layout.results_list_item, ranking);
        resultsList.setAdapter(adapter);

        TextView winnerName = findViewById(R.id.winner_name);
        winnerName.setText(players.get(0).getNickname());
    }

    // Written by Arunova Margarita.
    private void quitGame() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
