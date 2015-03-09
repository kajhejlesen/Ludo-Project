/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ludo.project;

import java.util.Arrays;

/**
 * Class to keep track of all game parameters, like the state of the game, current theme etc.
 * @author Kaj Hejlesen
 */
public class State {
    
    int gameState, currPlayer, dieResult, tries, size, delay;
    boolean testMode, trainingMode, starsAndGlobes;
    String layout;
    Player[] players;
    Tile[] tiles;
    PlayerType[] playing;
    Game ludo;
    int[] playerRanking, oldPositions, newPositions;
    boolean[] playerFinished, tokensHasLegalMove;

    public State(Game ludo) {
        this.ludo = ludo;
        gameState = 1;
        dieResult = 0;
        tries = 1;
        size = 20;
        testMode = false;
        trainingMode = false;
        starsAndGlobes = true;
        playing = new PlayerType[] {PlayerType.AI_easy, PlayerType.AI_random, PlayerType.AI_easy, PlayerType.AI_legal};
        playerFinished = new boolean[] {false, false, false, false};
        playerRanking = new int[] {-1, -1, -1, -1};
        oldPositions = new int[] {-1, -1, -1, -1};
        newPositions = new int[] {-1, -1, -1, -1};        
        tokensHasLegalMove = new boolean[] {false, false, false, false}; // tokens of current player who, after a die roll, have a legal move
        layout = "classic";
    }

   /**
    * check if any tokens of current player has legal move
    * @return any tokens have legal move
    */    
    public boolean anyTokenHasLegalMove() {
        boolean result = false;
        for (int i = 0; i < 4; i++) {
            if (tokensHasLegalMove[i])
                result = true;
        }
        return result;
    }

   /**
    * Set which tokens of current player that have legal moves
    * @param tokensHasLegalMove 
    */ 
    public void setTokensHasLegalMove(boolean[] tokensHasLegalMove) {
        this.tokensHasLegalMove = tokensHasLegalMove;
    }
    
   /**
    * Sets all tokens of current player to have no legal move
    */
    public void setNoTokensHasLegalMove() {
        Arrays.fill(tokensHasLegalMove, false);
    }
    
   /**
    * Checks how many tokens of the same player are on the tile of one of the players
    * tokens, as well as which number the input token is.
    * @param player playernumber (0-3)
    * @param token tokennumber (0-3)
    * @return int array of size 2. First number is the amount of tokens on the same tile (input token included)
    * second is the number of the input tile on that location.
    */
    public int[] multipleTokensOnTile(int player, int token) {
         int[] tokenOnTile = new int[2];
         
         for (int i = 0; i < 4; i++) {                              
                    if (players[player].getTokenPos(i) == players[player].getTokenPos(token)) {
                        tokenOnTile[0]++;                 
                        if (i == token) {
                            tokenOnTile[1] = tokenOnTile[0];
                        }
               }
         }
         return tokenOnTile;
    }
    
   /**
    * Finds the next star on the board based on the input tile position
    * @param star position of a tile (not necessarily with a star)
    * @return position of the next star
    */
    public int findNextStar(int star) {
        int nextStar = -1;
        int i = star;
        while (nextStar == -1) {
            i = (i + 1) % 52;
            if (tiles[i].getAtt() == TileType.star)
                nextStar = i;
        }
        return nextStar;
    }

   /**
    * Get the InitGame object setup (for changes to the tiles, restarting the game etc)
    * @return setup
    */
    public Game getLudoGame() {
        return ludo;
    }

   /**
    * Get the array of all tiles
    * @return all Tile objects
    */
    public Tile[] getAllTiles() {
        return tiles;
    }

   /**
    * Get the array of all players
    * @return all Player objects
    */
    public Player[] getAllPlayers() {
        return players;
    }
    
   /**
    * import Tile Objects for the initialization of the game
    * @param tiles 
    */
    public void importTiles(Tile[] tiles) {
        this.tiles = tiles;
    }
   
   /**
    * import Player Objects for the initialization of the game
    * @param players
    */
    public void importPlayers(Player[] players) {
        this.players = players;
    }

   /**
    * Get the current ranking of players in an array (-1 if position is not yet determined)
    * @return player ranking
    */
    public int[] getPlayerRanking() {
        return playerRanking;
    }

   /**
    * Sets the position of a just finished player
    * @param place number the player finished at (0-3)
    * @param player 
    */ 
    public void setPlayerRanking(int place, int player) {
        this.playerRanking[place] = player;
    }
    
   /**
    * Resets player ranking (setting all to -1)
    */ 
    public void resetPlayerRanking() {
        for (int i = 0; i < 4; i++)
            setPlayerRanking(i, -1);
    }
    
   /**
    * Get if a specific player is playing the game and hasn't finished yet
    * @param number playernumber to check (0-3)
    * @return true if playing
    */
    public boolean getPlayerInGame(int number) {
        return playing[number] != PlayerType.none && !playerFinished[number];
    }
    
    /**
     * Getting if a player is in the game (regardless if the player finished or not)
     * @param number playernumber (0-3)
     * @return true if player is in the game
     */
    public boolean getPlayerStarted(int number) {
        return playing[number] != PlayerType.none;
    }
    
    /**
     * Checking if any players are still active in the game (started and not finished)
     * @return true if there is at least one active player
     */
    public boolean isAnyPlayerInGame() {
        for (int i = 0; i < 4; i++) {
            if (getPlayerInGame(i))
                return true;
        }
        return false;
    }
    
   /**
    * Sets a specific player to have finished the game
    * @param player playernumber (0-3)
    */
    public void setPlayerFinished(int player) {
        playerFinished[player] = true;
    }
    
   /**
    * Get amount of players to have finished the game (useful for ending the game and ranking players afterwards)
    * @return 
    */
    public int getPlayersFinished() {
        int count = 0;
        for (boolean player : playerFinished) {
            if (player)
                count++;
         }
        return count;
    }
   
   /**
    * setting all playersFinished to false
    */
    public void resetPlayersFinished() {
        for (int i = 0; i < 4; i++)
             playerFinished[i] = false;
    }    

   /**
    * Get amount of players still playing the game
    * @return amount
    */
    public int getPlayersPlaying() {
        int count = 0;
        for (int i = 0; i < 4; i++) {
            if (getPlayerInGame(i))
                count++;
        }
        return count;
    }    
    
    public int getCurrPlayer() {
        return this.currPlayer;
    }
    
    public void setCurrPlayer(int player) {
        this.currPlayer = player;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }    
    
    public boolean[] getTokensHasLegalMove() {
        return tokensHasLegalMove;
    }
    
    public int getTries() {
        return tries;
    }

    public void setTries(int tries) {
        this.tries = tries;
    }

    public int getDieResult() {
        return dieResult;
    }

    public void setDieResult(int dieResult) {
        this.dieResult = dieResult;
    }

    public int getGameState() {
        return this.gameState;
    }

    public void setGameState(int state) {
        this.gameState = state;
    }
    
    public void setLayout(String layout) {
        this.layout = layout;
    }
 
    public String getLayout() {
        return layout;
    }

    public int[] getOldPositions() {
        return oldPositions;
    }

    /**
     * Sets the old position of a specific token
     * @param token 0-3
     * @param oldPosition old (or current) position of the token
     */
    public void setOldPosition(int token, int oldPosition) {
        this.oldPositions[token] = oldPosition;
    }

    public int[] getNewPositions() {
        return newPositions;
    }
    
    /**
     * Sets the new position of a specific token
     * @param token 0-3
     * @param newPosition new (after applying a move) position of the token
     */
    public void setNewPosition(int token, int newPosition) {
        this.newPositions[token] = newPosition;
    }

    public boolean isTrainingMode() {
        return trainingMode;
    }

    public void setTrainingMode(boolean trainingMode) {
        this.trainingMode = trainingMode;
    }

    public PlayerType getCurrPlaying() {
        return playing[currPlayer];
    }
    
    public PlayerType getPlaying(int playerNumber) {
        return playing[playerNumber];
    }
    
    public void setPlaying(int playerNumber, PlayerType type) {
        this.playing[playerNumber] = type;
    }

    public boolean isStarsAndGlobes() {
        return starsAndGlobes;
    }

    public void setStarsAndGlobes(boolean starsAndGlobes) {
        this.starsAndGlobes = starsAndGlobes;
    }
    
}