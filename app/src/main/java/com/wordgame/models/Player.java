package com.wordgame.models;

/**
 * The player model.
 *
 * @author Arunova Anastasia
 * @version 1.0
 * @since 1.0
 */
public class Player {

    private String userID;

    // Nickname.
    private String nickname;

    // Email.
    private String email;

    // Points in the game.
    private int points;

    // Field that is {@code true} if the player has completed game, otherwise {@code false}.
    private boolean completeGame;

    // Field that is {@code true} if the player has completed game, otherwise {@code false}.
    private boolean completeWatchingResults;

    public Player() {
    }

    /**
     * Constructor.
     *
     * @param userID   user ID
     * @param nickname nickname
     * @param email    email
     */
    public Player(String userID, String nickname, String email) {
        this.userID = userID;
        this.nickname = nickname;
        this.email = email;

        points = 0;
        completeGame = false;
        completeWatchingResults = false;
    }

    /**
     * Returns user ID.
     *
     * @return user ID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Returns nickname.
     *
     * @return nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Returns email.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns points.
     *
     * @return points
     */
    public int getPoints() {
        return points;
    }

    public boolean isCompleteGame() {
        return completeGame;
    }

    public boolean isCompleteWatchingResults() {
        return completeWatchingResults;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setCompleteGame(boolean completeGame) {
        this.completeGame = completeGame;
    }

    public void setCompleteWatchingResults(boolean completeWatchingResults) {
        this.completeWatchingResults = completeWatchingResults;
    }
}
