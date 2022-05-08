package com.wordgame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wordgame.adapters.CreateGameViewAdapter;
import com.wordgame.dialogs.InfoDialog;
import com.wordgame.interfaces.OnTaskCompleteListener;
import com.wordgame.models.Game;
import com.wordgame.models.GameCode;
import com.wordgame.models.Language;
import com.wordgame.models.Player;
import com.wordgame.pagetransformers.CreateGamePageTransformer;
import com.wordgame.utils.ConnectionUtils;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Create game activity.
 *
 * @author Arunova Margarita and Anastasia
 * @version 1.0
 * @since 1.0
 */
public class CreateGameActivity extends AppCompatActivity {

    // Written by Arunova Anastasia.
    private static final String LOG_TAG = CreateGameActivity.class.getSimpleName();

    // Written by Arunova Anastasia.
    // Variable that {@code true} if the game was created, otherwise, it {@code false}.
    private volatile boolean gamePrepared = false;

    // Written by Arunova Anastasia.
    private OnTaskCompleteListener onGameCodeGenerationCompleteListener;

    // Written by Arunova Anastasia.
    private ProgressBar progressBar;

    // Written by Arunova Anastasia.
    // Database reference.
    private DatabaseReference databaseReference;

    // Written by Arunova Margarita.
    private ViewPager2 createGameViewpager;

    // Written by Arunova Margarita.
    private CreateGameViewAdapter createGameAdapter;

    // Written by Arunova Margarita.
    private LinearLayout indicator;

    // Written by Arunova Margarita.
    private Button pageControlButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        findViewById(R.id.create_game_back_btn).setOnClickListener(view -> finish());

        // Written by Arunova Anastasia.
        // Check network connection.
        if (!ConnectionUtils.isNetworkConnect(this)) {
            InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.network_exception));
            infoDialog.showDialog(CreateGameActivity.this, new InfoDialog.OnDialogCloseListener() {
                @Override
                public void onOkClick() {
                    finish();
                }
            });
            return;
        }

        databaseReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://wordgame-ccc6a-default-rtdb.firebaseio.com/");

        progressBar = findViewById(R.id.create_game_progress_bar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);


        // Written by Arunova Margarita.
        createGameAdapter = new CreateGameViewAdapter();
        createGameViewpager = findViewById(R.id.create_game_viewpager);
        createGameViewpager.setAdapter(createGameAdapter);
        createGameViewpager.setPageTransformer(new CreateGamePageTransformer());
        createGameViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                changeIndicatorState(position);
            }
        });
        createGameViewpager.setOffscreenPageLimit(4);

        indicator = findViewById(R.id.create_game_indicator);
        pageControlButton = findViewById(R.id.create_game_indicator_button);
        pageControlButton.setOnClickListener(v -> {
            if (createGameViewpager.getCurrentItem() + 1 < createGameAdapter.getItemCount()) {
                createGameViewpager.setCurrentItem(createGameViewpager.getCurrentItem() + 1);
            } else {
                pageControlButton.setEnabled(false);

                ArrayList<Integer> parameters = createGameAdapter.getParameters();
                Log.d(LOG_TAG, parameters.get(0) + " " + parameters.get(1) + " "
                        + parameters.get(2) + " " + parameters.get(3));

                createGame(parameters);
            }
        });

        initIndicatorState();
        changeIndicatorState(0);
    }

    // Written by Arunova Margarita.
    private void initIndicatorState() {
        ImageView[] dots = new ImageView[createGameAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 20, 0);
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(getApplicationContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(), R.drawable.dot_inactive));
            dots[i].setLayoutParams(layoutParams);
            indicator.addView(dots[i]);
        }
    }

    // Written by Arunova Margarita.
    private void changeIndicatorState(int index) {
        if (index == createGameAdapter.getItemCount() - 1) {
            pageControlButton.setText(R.string.start_button);
        } else {
            pageControlButton.setText(R.string.next_button);
        }
        int itemCount = indicator.getChildCount();
        for (int i = 0; i < itemCount; i++) {
            if (i == index) {
                ((ImageView) indicator.getChildAt(i)).setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.dot_active));
            } else {
                ((ImageView) indicator.getChildAt(i)).setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.dot_inactive));
            }
        }
    }


    // Written by Arunova Anastasia

    /**
     * Creates the game.
     *
     * @param gameParameters game parameters
     */
    private void createGame(ArrayList<Integer> gameParameters) {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.user_null));
            infoDialog.showDialog(CreateGameActivity.this, new InfoDialog.OnDialogCloseListener() {
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

                    Player creator = new Player(firebaseUser.getUid(), nickname, firebaseUser.getEmail());

                    insertGameIntoFirebaseDatabase(creator, gameParameters);
                })
                .addOnFailureListener(e -> {
                    InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.user_null));
                    infoDialog.showDialog(CreateGameActivity.this, new InfoDialog.OnDialogCloseListener() {
                        @Override
                        public void onOkClick() {
                            finish();
                        }
                    });
                    Log.d(LOG_TAG, "FAIL: " + e.getMessage());
                });
    }

    // Written by Arunova Anastasia

    /**
     * Inserts the game into the database.
     *
     * @param creator        game creator
     * @param gameParameters game parameters
     */
    private void insertGameIntoFirebaseDatabase(Player creator, ArrayList<Integer> gameParameters) {
        final String[] code = {GameCode.getCode()};

        // Variable that is {@code true} if code generation stopped, otherwise, it {@code false}.
        AtomicBoolean stop = new AtomicBoolean(false);

        onGameCodeGenerationCompleteListener = response -> {
            if (!response) {
                if (!stop.get()) {
                    code[0] = GameCode.getCode();
                    checkCode(code[0]);
                } else {
                    showFailedGameCreationDialog();
                }
            } else {
                Game game = new Game(creator, gameParameters.get(2),
                        gameParameters.get(0), gameParameters.get(1),
                        gameParameters.get(3) == 0 ? Language.ENGLISH : Language.RUSSIAN, code[0],
                        getApplicationContext());

                // Add to database.
                String id = databaseReference.push().getKey();
                databaseReference.child(id).setValue(game);

                Log.d(LOG_TAG, "Add game with id: " + id);

                gamePrepared = true;

                startLobbyActivity(id, creator);
            }
        };

        checkCode(code[0]);

        // Executor that notes about 30 seconds during which the game must be created.
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        AtomicInteger attempts = new AtomicInteger(0);

        // Task that checks if the game was created.
        Runnable checkGameCreation = () -> {
            // After 30 attempts shut down task.
            if (attempts.incrementAndGet() >= 30) {
                executor.shutdown();
                stop.set(true);
            }
            if (gamePrepared) {
                executor.shutdown();
            }
        };

        executor.scheduleAtFixedRate(checkGameCreation, 0, 1, TimeUnit.SECONDS);
    }

    // Written by Arunova Anastasia.

    /**
     * Checks if the game code contains in the database.
     *
     * @param code game code
     */
    private void checkCode(String code) {
        databaseReference.orderByChild("code").equalTo(code).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        onGameCodeGenerationCompleteListener.onTaskCompleted(!dataSnapshot.exists());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(LOG_TAG, "onCancelled: " + databaseError.getMessage());
                        onGameCodeGenerationCompleteListener.onTaskCompleted(false);
                    }
                });
    }

    // Written by Arunova Anastasia.

    /**
     * Starts lobby activity.
     */
    private void startLobbyActivity(String gameID, Player player) {
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra("GAME_ID", gameID);
        intent.putExtra("IS_GAME_CREATOR", true);
        intent.putExtra("PLAYER_ID", player.getUserID());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // Written by Arunova Anastasia.

    /**
     * Shows dialog with information about the game creation failure.
     */
    private void showFailedGameCreationDialog() {
        InfoDialog infoDialog = new InfoDialog(getResources().getString(R.string.code_generation_exception));
        infoDialog.showDialog(CreateGameActivity.this);
        progressBar.setVisibility(View.GONE);
    }
}