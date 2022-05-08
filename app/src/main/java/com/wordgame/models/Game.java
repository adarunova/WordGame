package com.wordgame.models;

import android.content.Context;
import android.util.Log;

import com.wordgame.dictionary.Dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The game model.
 *
 * @author Arunova Anastasia and Margarita
 * @version 1.0
 * @since 1.0
 */
public class Game {

    // Application context to generate words.
    private Context context;

    // Player id.
    private String creatorID;

    // Players.
    private Map<String, Player> players;

    // The number of players who has completed the game.
    private int completeGamePlayersNumber;

    // The maximum number of players.
    private int maxPlayersNumber;

    // The number of rounds.
    private int roundsNumber;

    // The number of seconds.
    private int secondsNumber;

    // The language.
    private Language language;

    // The game code.
    private String code;

    // The list of letter sequences.
    private List<String> letterSequences;

    // Variable that is {@code
    private boolean gameStarted;

    // Default constructor.
    public Game() {
    }

    /**
     * Constructor.
     *
     * @param creator          creator
     * @param maxPlayersNumber the number of maximum players
     * @param roundsNumber     the number of rounds
     * @param secondsNumber    the number of seconds
     * @param language         the language
     * @param code             the game code
     */
    public Game(Player creator, int maxPlayersNumber, int roundsNumber, int secondsNumber, Language language, String code, Context context) {
        this.creatorID = creator.getUserID();
        this.maxPlayersNumber = maxPlayersNumber;
        this.roundsNumber = roundsNumber;
        this.secondsNumber = secondsNumber;
        this.language = language;
        this.code = code;
        this.context = context;

        gameStarted = false;
        completeGamePlayersNumber = 0;

        players = new HashMap<>();
        players.put(creatorID, creator);

        letterSequences = new ArrayList<>();
        fillLetterSequences();
    }

    public String getCreatorID() {
        return creatorID;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public int getMaxPlayersNumber() {
        return maxPlayersNumber;
    }

    public int getRoundsNumber() {
        return roundsNumber;
    }

    public int getSecondsNumber() {
        return secondsNumber;
    }

    public Language getLanguage() {
        return language;
    }

    public String getCode() {
        return code;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public List<String> getLetterSequences() {
        return letterSequences;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setPlayers(Map<String, Player> players) {
        this.players = players;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    // Written by Arunova Margarita
    private void fillLetterSequences() {
        Dictionary dictionary = new Dictionary(context, language);
        String[] words = dictionary.generateWords(roundsNumber);

        Integer[] indices;
        for (int i = 0; i < roundsNumber; i++) {
            indices = generateRandomIndices(words[i].length());
            letterSequences.add("" +
                    words[i].charAt(indices[0]) +
                    words[i].charAt(indices[1]) +
                    words[i].charAt(indices[2]));
        }
    }

    // Written by Arunova Margarita
    private Integer[] generateRandomIndices(int length) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        return new Integer[]{list.get(0), list.get(1), list.get(2)};
    }
}
