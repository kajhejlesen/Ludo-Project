/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ludo.project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 * Contains the core game logic
 * @author Kaj Hejlesen
 */
public class GamePlay {
    State state;
    Tile[] tiles;
    Player[] players;
    Game game;
    Die dieD6;
    AI kermIT;
    Timer timer;    
    
    public GamePlay(State state, Game game) {
        this.state = state;
        this.game = game;
        dieD6 = new Die(6);
    }    
        
   /**
    * Rolls a die, managing amount of try to give a player and calls a check for legal
    * moves unless certain conditions makes it unnecessary (all tokens in the house
    * and dieResult != 6
    */
    public void startPlayerTurn() {
        state.setDieResult(dieD6.rollDie());
        
        // Checking if a player should get 3 attempts to get out of the house
        if (state.getDieResult() != 6 && !players[state.getCurrPlayer()].getInPlay()) {
            state.setTries(state.getTries() + 1);
            if (state.getTries() <= Game.TRIES && !state.isTrainingMode()) {
                if (state.getCurrPlaying().toString().startsWith("AI")) {
                     timer.start();
                }
            } else {
                if (state.getCurrPlaying() == PlayerType.human) {
                    state.setGameState(3);   // Setting gameState to 3 to avoid a player cheating during the small delay
                    timer.start();
                } else if (!state.isTrainingMode())
                    timer.start(); 
            }

        } else {
            state.setTries(0);
            if (!state.isTrainingMode())
                checkForLegalMoves();
        }
    }
    
    /**
     * return the new position of a token based on die roll and game rules
     * @param token input token 0-3
     * @param dieRoll current die roll
     * @return new position of token (not accounting for stars and other tokens)
     */
    public int checkForMove(int token, int dieRoll) {
        int oldPosition = state.getOldPositions()[token];
        int newPosition = -1;
        
        if (dieRoll == 6 && oldPosition >= 76) {
            newPosition = players[state.getCurrPlayer()].getStartTile();

            // tokens move inside the victory lane. Moving back and forth until it hits the center directly
        } else if (oldPosition >= 52) {
            if ((oldPosition + dieRoll) > players[state.getCurrPlayer()].getWinTile()) {
                newPosition = 2 * players[state.getCurrPlayer()].getWinTile() - (oldPosition + dieRoll);
            }
            else
                newPosition = oldPosition + dieRoll;
        } else if (oldPosition <= 51) {

            // token move to their victory lane!
            if (oldPosition <= players[state.getCurrPlayer()].getEndTile() && oldPosition +
                    dieRoll > players[state.getCurrPlayer()].getEndTile()) {

                // Setting position inside the victory lane
                newPosition =  (oldPosition + dieRoll) %
                        players[state.getCurrPlayer()].getEndTile() + 
                        players[state.getCurrPlayer()].getStartOfVictoryLane()-1;

            } else 
                newPosition = (oldPosition + dieRoll) % 52;
            }
        return newPosition;
    }
    
   /**
    * Performs the actual move of a token based on dieroll and rules.
    * @param token token that have a legal move
    */
    public void moveToken(int token) {
        int[] conflict;
        boolean hitStar = false;
        boolean gameEnd = false;
        // set new Position
        players[state.getCurrPlayer()].setTokenPos(token, state.getNewPositions()[token]);
        
        // check for any conflict or star in that new position
        do {
        if (hitStar) {
            players[state.getCurrPlayer()].setTokenPos(token, state.findNextStar(players[state.getCurrPlayer()].getTokenPos(token)));
        }

        conflict = isOccupiedBy(players[state.getCurrPlayer()].getTokenPos(token));

            if (conflict[0] > 0) {

                // Sending current players token home, if there is more than one token or on a globe or homeTile
                if (conflict[0] > 1 || tiles[players[state.getCurrPlayer()].getTokenPos(token)].getAtt() == TileType.globe
                        || players[conflict[1]].getStartTile() == players[state.getCurrPlayer()].getTokenPos(token)) {

                    sendHome(state.getCurrPlayer(), token);
                    break;     // to prevent the token to move to the 2nd star if being send home on the first

                } else {
                    sendHome(conflict[1], conflict[2]);
                }
            }
            hitStar = tiles[state.getNewPositions()[token]].getAtt() == TileType.star && state.getNewPositions()[token] <= 51 && !hitStar;
        }
        while (hitStar);
        
        //Check for win condition
        if(players[state.getCurrPlayer()].getAtEnd() == 4 && state.isAnyPlayerInGame()) {
            if (!state.isTrainingMode()) {
                System.out.printf("\nPlayer %d has finished in place %d!\n", state.getCurrPlayer() + 1,state.getPlayersFinished() + 1);
            }
            
            state.setPlayerRanking(state.getPlayersFinished(), state.getCurrPlayer());
            state.setPlayerFinished(state.getCurrPlayer());

            if (!state.isTrainingMode()) {
                gameEnd = true;
                for (int i = 0; i < 4; i++) {
                    if (state.getPlayerInGame(i)) {
                        gameEnd = false;
                        break;
                    }
                }
                if (gameEnd) {
                    gameEnd();
                }
            }
        }
        if (!state.isTrainingMode() && state.isAnyPlayerInGame()){
            nextPlayer();}
    }

   /**
    * Calls method to check if tokens of current player have legals move and manages game states based on the info
    */
    public void checkForLegalMoves() {
        state.setTokensHasLegalMove(legalMove(state.getDieResult()));
        
        if (state.anyTokenHasLegalMove()) {
            for (int i = 0; i < 4; i++) {
                if (state.getTokensHasLegalMove()[i]) {
                    state.setOldPosition(i, players[state.getCurrPlayer()].getTokenPos(i));
                    state.setNewPosition(i, checkForMove(i, state.getDieResult()));
                }
            }
            state.setGameState(2); // set gameState to 2: waiting for input / AI to move a token
            state.setTries(0);
            if (state.getCurrPlaying().toString().startsWith("AI") && !state.isTrainingMode()) {
                timer.start();
            }
        } else if (!state.isTrainingMode())
            nextPlayer();
    }
   
    /**
     * Sets the current player to the next active player, and sets gameState to 2.
     * Handles the case of giving extra turns to the same player
     */
    public void nextPlayer() {
        state.setNoTokensHasLegalMove();
        if (state.getDieResult() != 6) {
            
            boolean playerFound = false;
            do {

                state.setCurrPlayer((state.getCurrPlayer() + 1) % 4);

                if (state.getPlayerInGame(state.getCurrPlayer())) {
                    state.setGameState(1);
                    playerFound = true;

                    if (!players[state.getCurrPlayer()].getInPlay()) {
                        state.setTries(1);
                    } else {
                        state.setTries(0);
                    }

                    if (state.getCurrPlaying().toString().startsWith("AI") && !state.isTrainingMode()) {
                        timer.start();
                    }
                }
            } while (!playerFound);

        } else {
            state.setGameState(1);
            state.setTries(0);
            if (state.getCurrPlaying().toString().startsWith("AI") && (!state.isTrainingMode())) {
                 timer.start();
            }
        }
    }
    
   /**
    * Check if a tile is occupied by opponent tokens
    * @param tileNumber tile number to be examined
    * @return int[3] = {Number of tokens, belonging to player (or -1), last token (or -1)}
    */
    public int[] isOccupiedBy(int tileNumber) {

        // Note we only store one value for token. If there is more than one token on the tile, we will never use it.
        // Likewise, there will never be more than one player on a tile.
        int[] tokenOnTile = new int[]{0, -1, -1};

        for (int i = 1; i < 4; i++) {                                                               // only checking 3 players
            if (state.getPlayerInGame((state.getCurrPlayer() + i) % 4) == true ) {                  // is that player still playing?
                for (int token = 0; token < 4; token++) {                                           // go through all tokens of that player
                    if (players[(state.getCurrPlayer() +i) % 4].getTokenPos(token) == tileNumber) { // anyone on that tile?
                        tokenOnTile[0]++;                                                           // increment token count
                        tokenOnTile[1] = (state.getCurrPlayer() + i) % 4;                           // Token belongs to this player
                        tokenOnTile[2] = token;                                                     // Token#
                    }
                }
            }
            if (tokenOnTile[0] != 0) {                                                              // We found token belonging to a player,
                break;                                                                              // no need to go on looking for more.
            }
        }
        return tokenOnTile;
    }        
    
    /**
     * Send the selected token home
     * @param playerNumber owner of the token
     * @param token #token
     */
    public void sendHome(int playerNumber, int token) {
        players[playerNumber].setTokenPos(token, players[playerNumber].checkFirstHomeTile());
    }
    
   /**
    * Checks if a token of current player has a legal move
    * @param move die result
    * @return true if token has legal move
    */
    public boolean[] legalMove(int move) {
        boolean[] tokenCanMove = new boolean[4];
        int tokenPosition;
        for (int token = 0; token < 4; token++) {
            tokenPosition = players[state.getCurrPlayer()].getTokenPos(token);
            // token is in home and the result is not a 6
            if (tokenPosition >= 76 && move != 6)
                tokenCanMove[token] = false;
            else if (tokenPosition == players[state.getCurrPlayer()].getWinTile())
                tokenCanMove[token] = false;
                // token is overshooting the win tile
            else if (tokenPosition + move > players[state.getCurrPlayer()].getWinTile()
                    && tokenPosition <= 75 && !canMovePastWinTile(token, move))
                tokenCanMove[token] = false;
            else
                tokenCanMove[token] = true;
            
            //System.out.println("token #" + token + " at " + tokenPosition + ": " + tokenCanMove[token]);
        }
        return tokenCanMove;
    }
    
    /**
     * checking if any other token has a legal move.
     * @param token
     * @param move dieroll
     * @return true if token has a legal move
     */
    public boolean canMovePastWinTile(int token, int move) {
        int tokenPosition;
        // This is similar to legalMove, we check all tokens for a possible move
        for (int i = 1; i < 4; i++) {
            tokenPosition = players[state.getCurrPlayer()].getTokenPos((token + i) % 4);
            if (!(tokenPosition >= 76 && move != 6) &&
                    !(tokenPosition + move > players[state.getCurrPlayer()].getWinTile()
                    && tokenPosition <= 75))
                return false;
        }
        return true;
    }
    
    public void setTimer(int delay) {
        timer = new Timer(delay, wait);
    }
    
    
    ActionListener wait = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            timer.stop();
            if (state.getCurrPlaying().toString().startsWith("AI")) {
                if (state.getGameState() == 1 && state.getTries() <= Game.TRIES)
                    startPlayerTurn();
                else if (state.getGameState() == 2)
                    moveToken(kermIT.chooseToken());
                else
                    nextPlayer();
            } else if (state.getGameState() == 3) {
                state.setGameState(1);
                nextPlayer();
            }
        }
    };

   /**
    * Run after all players have finished. Displays rank and prompts user for an action
    */
    public void gameEnd() {

        if (state.isTrainingMode()) {
            game.restartGame(); // skipping any dialogs while learning
        } else {
            String rankings = "";
            String[] abbr = {"1st", "2nd", "3rd", "4th"};
            
            // concatenating ranking string for players in the game
            for (int i = 0; i < 4; i++) {
                if (state.getPlayerRanking()[i] == -1) break;
                else rankings += abbr[i] + " place: Player " + (state.getPlayerRanking()[i]+1) + "\n";
            }
            // Displaying end dialog prompting a user to restart or exit
            Object[] options = {"Start new game", "Exit game"};            
            int choice = JOptionPane.showOptionDialog(null, rankings, "Game has ended",
                         JOptionPane.CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
                         null, options, options[0]);

            if (choice == 0) game.restartGame();
            else System.exit(0); // together with CANCEL_OPTION this makes the program close when you close the dialog
        }
    }
    
   /**
    * import Objects for the initialization of the game
    * @param tiles 
     * @param players 
     * @param kermIT 
    */
    public void importObjects(Tile[] tiles, Player[] players, AI kermIT) {
        this.tiles = tiles;
        this.players = players;
        this.kermIT = kermIT;
    }
}