package com.wordgame;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wordgame.adapters.GameStartAdapter;
import com.wordgame.dialogs.InfoDialog;
import com.wordgame.dictionary.WordValidityChecker;
import com.wordgame.interfaces.OnLoadCountDownEndListener;
import com.wordgame.interfaces.OnTaskCompleteListener;
import com.wordgame.models.Game;
import com.wordgame.models.Player;
import com.wordgame.models.Scoring;
import com.wordgame.services.CloseAppService;
import com.wordgame.utils.ConnectionUtils;

import java.util.HashSet;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Create game activity.
 *
 * @author Arunova Anastasia and Margarita
 * @version 1.0
 * @since 1.0
 */
public class GameActivity extends AppCompatActivity implements OnLoadCountDownEndListener {

    private static final String LOG_TAG = GameActivity.class.getSimpleName();

    // Database reference (written by Arunova Anastasia).
    private DatabaseReference databaseReference;

    ValueEventListener gameEndValueEventListener;

    ProgressBar progressBar;

    private Game game;

    private String playerID;

    private String gameID;

    private int playersCount;

    private int points;

    // Current round in the game.
    private int currentRound = 1;
    private HashSet<String> inputWords;

    // Main game screen objects.
    private RelativeLayout gameScreen;
    private AdapterViewFlipper startViewFlipper;

    // Game objects.
    private TextView[] letters;
    private TextView time, score, round;
    private EditText playerInput;

    private WordValidityChecker wordValidityChecker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Written by Arunova Anastasia.
        // Check network connection.
        if (!ConnectionUtils.isNetworkConnect(this)) {
            InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.network_exception));
            infoDialog.showDialog(GameActivity.this, new InfoDialog.OnDialogCloseListener() {
                @Override
                public void onOkClick() {
                    finish();
                }
            });
            return;
        }

        progressBar = findViewById(R.id.game_progress_bar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        databaseReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://wordgame-ccc6a-default-rtdb.firebaseio.com/");

        playerID = getIntent().getExtras().getString("PLAYER_ID");
        gameID = getIntent().getExtras().getString("GAME_ID");

        // Written by Arunova Anastasia.
        setCloseAppService();

        loadGame(response -> {
            progressBar.setVisibility(View.GONE);
            if (response && game != null) {
                playersCount = game.getMaxPlayersNumber();
                setListenersOnData();
                startGame();
            } else {
                InfoDialog infoDialog = new InfoDialog("Failed to start the game");
                infoDialog.showDialog(GameActivity.this, new InfoDialog.OnDialogCloseListener() {
                    @Override
                    public void onOkClick() {

                    }
                });
            }
        });
    }

    // Written by Arunova Anastasia.
    private void loadGame(OnTaskCompleteListener onTaskCompleteListener) {
        databaseReference.child(gameID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                game = dataSnapshot.getValue(Game.class);
                onTaskCompleteListener.onTaskCompleted(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onTaskCompleteListener.onTaskCompleted(false);
            }
        });
    }

    private void setListenersOnData() {
        databaseReference.child(gameID).child("players")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Player> map = (Map<String, Player>) dataSnapshot.getValue();
                        if (map != null) {
                            playersCount = map.size();
                            if (playersCount == 0) {
                                databaseReference.child(gameID).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        databaseReference.child(gameID).child("completeGamePlayersNumber").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            Long number = (Long) dataSnapshot.getValue();
                            if (number != null && playersCount <= number) {
                                startGameResultsActivity();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    // Written by Arunova Anastasia.
    private void setCloseAppService() {
        CloseAppService.onTaskCompleteListener = response -> quitGame();

        Intent intent = new Intent(this, CloseAppService.class);
        startService(intent);
    }

    // Written by Arunova Margarita.
    private void startGame() {
        wordValidityChecker = new WordValidityChecker(getApplicationContext());
        wordValidityChecker.setCurrentLanguage(game.getLanguage());

        points = 0;
        inputWords = new HashSet<>();

        gameScreen = findViewById(R.id.game_layout);

        ImageButton quitButton = findViewById(R.id.game_quit_btn);
        quitButton.setOnClickListener(v -> {
            quitGame();
            startMainActivity();
        });

        letters = new TextView[]{
                findViewById(R.id.first_letter),
                findViewById(R.id.second_letter),
                findViewById(R.id.third_letter)
        };

        round = findViewById(R.id.game_round_text);
        time = findViewById(R.id.game_seconds_text);

        score = findViewById(R.id.game_score_text);
        score.setText(getResources().getString(R.string.score) + " " + points);

        playerInput = findViewById(R.id.player_answer_input);
        playerInput.setHint(getResources().getString(R.string.input_text));

        Button checkWordButton = findViewById(R.id.game_check_word_btn);
        checkWordButton.setOnClickListener(view -> {
            checkWord(playerInput.getText().toString());
            playerInput.setText("");
        });

        startViewFlipper = findViewById(R.id.game_start_adapter);
        startViewFlipper.setAdapter(new GameStartAdapter(this, this));
        startViewFlipper.setFlipInterval(1000);

        startRound();
    }

    private void endGame() {
        progressBar.setVisibility(View.VISIBLE);

        // Written by Arunova Ansatsia.
        databaseReference.child(gameID).child("players").child(playerID)
                .child("completeGame").setValue(true);

        checkIfAllPlayersCompleteGame();
    }


    // Written by Arunova Anstasia.

    /**
     * Quit the game.
     */
    private void quitGame() {
        Log.d(LOG_TAG, "quitGame: ");
        if (playersCount <= 1) {
            databaseReference.child(gameID).removeValue();
        } else {
            databaseReference.child(gameID).child("players").child(playerID).removeValue();
        }
    }

    // Written by Arunova Anstasia.

    /**
     * Start main activity.
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // Written by Arunova Anastasia

    /**
     * Check if all players has completed the game.
     */
    private void checkIfAllPlayersCompleteGame() {
        gameEndValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int completeGamePlayersNumber = 0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Player player = child.getValue(Player.class);
                    if (player != null && player.isCompleteGame()) {
                        completeGamePlayersNumber++;
                    }
                }

                databaseReference.child(gameID).child("completeGamePlayersNumber")
                        .setValue(completeGamePlayersNumber);

                if (playersCount == 0) {
                    // If all players quit the game, delete the game from the database.
                    deleteGame();
                } else if (completeGamePlayersNumber >= playersCount) {
                    // If all players completed the game but not quit, show them the results screen.
                    startGameResultsActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        databaseReference.child(gameID).child("players").addValueEventListener(gameEndValueEventListener);
    }

    // Written by Arunova Anastasia.

    /**
     * Delete the game from DB.
     */
    private void deleteGame() {
        databaseReference.child(gameID).removeValue();
    }

    // Written by Arunova Anastasia.

    /**
     * Start game results activity.
     */
    private void startGameResultsActivity() {
        progressBar.setVisibility(View.GONE);
        databaseReference.child(gameID).child("players").removeEventListener(gameEndValueEventListener);

        Intent intent = new Intent(this, GameResultsActivity.class);
        intent.putExtra("GAME_ID", gameID);
        intent.putExtra("PLAYER_ID", playerID);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void checkWord(String word) {
        // Written by Arunova Margarita.
        word = word.trim();
        if (word.length() <= 3) {
            InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.letters_amount_exception));
            infoDialog.showDialog(GameActivity.this);
            return;
        }
        if (!wordValidityChecker.checkWord(word)) {
            InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.non_existent_word_exception));
            infoDialog.showDialog(GameActivity.this);
            return;
        }
        if (inputWords.contains(word)) {
            InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.word_already_input));
            infoDialog.showDialog(GameActivity.this);
            return;
        }
        int wordPoints = Scoring.getPointsAmount(game.getLetterSequences().get(currentRound - 1), word);
        if (wordPoints == 0) {
            InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.incorrect_word_exception));
            infoDialog.showDialog(GameActivity.this);
            return;
        }
        points += wordPoints;
        score.setText(getResources().getString(R.string.score) + " " + points);
        inputWords.add(word);

        // Written by Arunova Aanastasia
        databaseReference.child(gameID).child("players").child(playerID).child("points")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().setValue(points);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    // Written by Arunova Margarita.
    public void startRound() {
        for (int i = 0; i < 3; i++) {
            letters[i].setText(("" + game.getLetterSequences().get(currentRound - 1).charAt(i)).toUpperCase());
        }
        inputWords.clear();

        round.setText(getResources().getString(R.string.round) + " " + currentRound);
        time.setText(game.getSecondsNumber() + " " + getResources().getString(R.string.sec));
        playerInput.setText("");

        // Start game with loading count down.
        gameScreen.setVisibility(View.INVISIBLE);
        startViewFlipper.setVisibility(View.VISIBLE);
        startViewFlipper.startFlipping();
    }

    @Override
    public void onLoadCountDownEnd() {
        startViewFlipper.stopFlipping();
        startViewFlipper.setVisibility(View.GONE);
        gameScreen.setVisibility(View.VISIBLE);

        // Game timer start.
        new CountDownTimer((game.getSecondsNumber() + 1) * 1000, 1000) {
            @Override
            public void onTick(long l) {
                time.setText((l / 1000l) + " " + getResources().getString(R.string.sec));
            }

            @Override
            public void onFinish() {
                endRound();
            }
        }.start();
    }

    public void endRound() {
        if (++currentRound <= game.getRoundsNumber()) {
            startRound();
        } else {
            endGame();
        }
    }
}
