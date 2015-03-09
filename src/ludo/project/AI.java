/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ludo.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Algorithms of the different implementations of the AI
 * @author Kaj Hejlesen
 */
public class AI {
    final static int BOARDLENGTH = 59; // amount of tiles each individual token can be at (used by Q-Learning)
    State state;
    Player[] players;
    Tile[] tiles;
    GamePlay turn;
    double[][] QTable;
    
    public AI (State state, Player[] players, Tile[] tiles, GamePlay turn) {
        this.state = state;
        this.players = players;
        this.tiles = tiles;
        this.turn = turn;
        QTable = readQTable();
    }

    /**
     * returns the result of an AI based on the current playertype
     * @return token as chosen by the AI
     */
    public int chooseToken() {
        
        switch (state.getCurrPlaying()) {
            
            case AI_easy:
                return chooseTokenQLearn();
            case AI_normal:
                return chooseTokenNormal(false);
            case AI_hard:
                return chooseTokenNormal(true); // hunting mode
            case AI_random:
                return chooseTokenRandom();
            case AI_legal:
                return firstLegalMove();
            default:
                return chooseTokenRandom();
        }
    }
    
    /**
     * Default AI assigning arbitrary points for each token based on position
     * @param hunt true if AI should be 'unfair' to human players for harder difficulty
     * @return token decided to move
     */
    public int chooseTokenNormal(boolean hunt) {
        int tokenToMove;
        int[] tokenScore = new int[] {0,0,0,0};
        int oldP, newP;
        int firstStarHit;
        int count;  // variable used in for loops
        
        // going through is individual token that has a legal move and giving it a score based on a number of parameters
        for (int i = 0; i < 4; i++) {
            if (state.getTokensHasLegalMove()[i]) {   
                oldP = state.getOldPositions()[i];
                newP = state.getNewPositions()[i];       
                int[] occupied = turn.isOccupiedBy(newP);
                
                // if token has hit a star, firstStarHit is the star it has hit and newP is the final location
                if (tiles[newP].getAtt() == TileType.star) {
                    firstStarHit = newP;            
                    newP = state.findNextStar(newP);
                } else {
                    firstStarHit = -1;
                }

                // giving a score that is 1/3th of the progress on the main board
                if (oldP < 52 && newP < 52)
                    tokenScore[i] += (( 52 + oldP - players[state.getCurrPlayer()].getStartTile()) % 52) / 4;

                // Get a new token in play
                if (oldP >= 76 && newP < 52)
                    tokenScore[i] += 50;
                
                // considering all the instances where newP is occupied by another players token                    
                if (occupied[0] > 0) {
                    
                    // sending another player home, bonus depending on how far it have come
                    if (occupied[0] == 1 && tiles[newP].getAtt() != TileType.globe && newP != players[occupied[1]].getStartTile() ) {
                        if (!hunt) 
                            tokenScore[i] += 40 + (( 52 + newP - players[occupied[1]].getStartTile()) % 52) / 4;
                        else if (hunt && state.getPlaying(occupied[1]) != PlayerType.AI_normal)
                            tokenScore[i] -= 200; // only want to send human tokens home
                        else if (hunt && state.getPlaying(occupied[1]) == PlayerType.AI_normal)
                            tokenScore[i] += 200; // is a human? die!
                    }
                        
                    
                    // token sending itself home
                    else if (occupied[0] >= 1 || (occupied[0] == 1 && tiles[newP].getAtt() == TileType.globe) 
                            || (newP != players[occupied[1]].getStartTile() && occupied[0] >= 0))
                        tokenScore[i] -= 80  - ((52 +oldP - players[state.getCurrPlayer()].getStartTile()) % 52) / 2;
                }
             
                // checking if position on a star that is hit, is occupied
                if (firstStarHit != -1 && turn.isOccupiedBy(firstStarHit)[0] > 0) {
                    
                    if (turn.isOccupiedBy(firstStarHit)[0] == 1)
                        tokenScore[i] += 30 + ((52 + newP - players[turn.isOccupiedBy(firstStarHit)[1]].getStartTile()) % 52) / 4;
                    
                    else if (turn.isOccupiedBy(firstStarHit)[0] > 1)
                        tokenScore[i] -= 90 - ((52 + oldP - players[state.getCurrPlayer()].getStartTile()) % 52) / 2;
                }
            
                // Hitting a globe that is not occupied
                if (tiles[newP].getAtt() == TileType.globe && occupied[0] == 0 )
                    tokenScore[i] += 20;
            
                // hitting a star
                if (firstStarHit != -1)
                    tokenScore[i] += 20;
                
                // hitting the last star meaning you have do do another round
                else if (firstStarHit == (52 + (players[state.getCurrPlayer()].getStartTile() - 2)) % 52)
                    tokenScore[i] -= 100;
                
                // leaving a safe globe
                if (tiles[oldP].getAtt() == TileType.globe || oldP == players[state.getCurrPlayer()].getStartTile())
                    tokenScore[i] -= 5;
                    
                // entering the victory lane (safe)
                if (oldP < 52 && newP >= 52) {
                    tokenScore[i] += 50;
                }
                
                // Hitting the center win tile directly
                if (newP == players[state.getCurrPlayer()].getWinTile())
                    tokenScore[i] += 20;

                // Check if token hits a 'dangerous' start tile of an opponent
                if (isStartTile(newP)) {
                    tokenScore[i] -= 12;
                }

                // opponent token right behind the old position unless it is safe
                count = 0;
                for (int t = (52 + oldP - 6) % 52; t < oldP; t++) {
                    if (turn.isOccupiedBy(t)[0] > 0 && tiles[oldP].getAtt() != TileType.globe 
                            && oldP != players[state.getCurrPlayer()].getStartTile() 
                            && oldP < 52) 
                        tokenScore[i] += 5 + 2 * count;
                    count++;                    
                }

                // opponent token right behind the new position 
                count = 0;
                for (int t = (52 + newP - 6) % 52; t < newP; t++) {
                    if (turn.isOccupiedBy(t)[0] > 0 && oldP < 52 && newP < 52)
                        tokenScore[i] -= (1 + count);
                    count++;
                }

                // hunting enemy tokens, higher reward the closer the token is, unless
                // the token is on a globe, it's home tile or not on the main track
                count = 0;
                for (int t = (newP + 1) % 52; t < (newP + 7) % 52; t++) {
                    int[] occ = turn.isOccupiedBy(t % 52);
                    if (occ[0] > 0 &&
                            tiles[t % 52].getAtt() != TileType.globe && 
                            (t % 52) == players[occ[1]].getStartTile() &&
                            oldP < 52 && newP < 52)
                        tokenScore[i] += 15 - 2 * count;
                    count++;
                }           
                
                // hunting season on humans is on...
                if (hunt) {
                    for (int t = (newP + 1) % 52; t < (newP + 20) % 52; t++) {
                        int[] occ = turn.isOccupiedBy(t % 52);
                        if (occ[0] > 0
                                && tiles[t % 52].getAtt() != TileType.globe
                                && (t % 52) == players[occ[1]].getStartTile()
                                && oldP < 52 && newP < 52 && state.getPlaying(occ[1]) == PlayerType.AI_normal)
                            tokenScore[i] += 60 - 3 * count; // increasing score for following human players
                        count++;
                    }
                }

            }
        }

//        // for testing, outputs the different values for the token together with the playernumber and dieresult
//        System.out.print("player " + (state.getCurrPlayer()+1) + " ");
//        for (int j = 0; j < 4; j++) {
//            System.out.print(tokenScore[j] + " ");
//        }
//        System.out.print("DieR = " + state.getDieResult() + "\n");
        
        // finding the highest value (if a tie, picks the first token)
        int highest = tokenScore[firstLegalMove()];
        tokenToMove = firstLegalMove();
        for (int i = firstLegalMove() + 1; i < 4; i++) {
            if (state.getTokensHasLegalMove()[i] && tokenScore[i] > highest) {
                highest = tokenScore[i];
                tokenToMove = i;
            }
        }
        return tokenToMove;
    }
    
   /**
    * Gets the first token of a player that have a legal move, -1 if none
    * @return First token that can move, -1 if no legal move
    */
    public int firstLegalMove() {
        for (int i = 0; i < 4; i++) {
            if (state.getTokensHasLegalMove()[i]) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Finds a legal token to move at random
     * @return random token, -1 if no legal move
     */
    public int chooseTokenRandom(){
        Random generator = new Random();
        int tokenToMove = -1;
        int rand;
        boolean moved = false;

        while (!moved) {
            rand = generator.nextInt(4);
            if (state.getTokensHasLegalMove()[rand]) {
                tokenToMove = rand;
                moved = true;
            }
        }
        return tokenToMove;
    }
    
    /**
     * finds the best token to move, according to the Q-Learning algorithm. Token 
     * is found based on the value of the action compared to the maximum value of the current state
     * @return token decided, -1 if no legal move
     */
    public int chooseTokenQLearn() {
        double bestScore = -100;
        double score, currentStateScore, newStateScore;
        int tokenChosen = -1;
        for (int i = 0; i < 4; i++) {
            if (state.getTokensHasLegalMove()[i]) {
                
                currentStateScore = QForAvgAction(convertTileToArray(state.getOldPositions()[i], state.getCurrPlayer())) * 
                                    (6 - opponentsTokenNear(state.getCurrPlayer(), i, false, false) / 6);
                
                newStateScore = QTable[convertTileToArray(state.getNewPositions()[i], state.getCurrPlayer())][state.getDieResult()-1] *
                                    (6 - opponentsTokenNear(state.getCurrPlayer(), i, true, false) / 6);
                
                score = newStateScore - currentStateScore;

                if (score >= bestScore) {
                    bestScore = score;
                    tokenChosen = i;
                }
            }
        }
        return tokenChosen;
    }
    
    public int opponentsTokenNear(int player, int token, boolean hasMoved, boolean recursive) {
        int opponents = 0;
        int position;
        if (!hasMoved)
            position = state.getOldPositions()[token];
        else {
            position = state.getNewPositions()[token];
            if (tiles[position].getAtt() == TileType.star)
                position = state.findNextStar(position);
        }
        
        for (int t = (52 + position - 6) % 52; t < position; t++) {
            if (turn.isOccupiedBy(t)[0] > 0 && position < 52)
                opponents++;
        }
        
        if (tiles[position].getAtt() == TileType.star && !recursive)
            opponents += opponentsTokenNear(player, token, hasMoved, true);
        return opponents;
    }
    
    /**
     * Find the maximum Q-Value for the possible actions in the current state
     * @param i index into q-table in range from 0 to length of board - 1
     * @return The maximum Q-value for index i 
     **/
    double QForMaxAction(int i) {
	double max=0;
	for(int j=1;j<6;j++) {
	    if (QTable[i][j] > max)
                max = QTable[i][j];
	}
	return(max);
    }       
    
    /**
     * Find the average Q-Value for the possible actions in the current state
     * @param i index into q-table in range from 0 to length of board - 1
     * @return The average Q-value for index i 
     **/
    double QForAvgAction(int i) {
	double sum=0;
	for(int j=1;j<6;j++) {
            sum += QTable[i][j];
	}
	return sum / 6 ;
    }        

    
    /**
     * Converts the tile number used in the appliciation to the array used in Q-Learning
     * @param oldTileNumber tilenumber in the normal format
     * @param playerNumber 0-3
     * @return place in the array
     */
    public int convertTileToArray(int oldTileNumber, int playerNumber) {
        if (oldTileNumber < 52)
            return (52 + oldTileNumber - 13 * playerNumber) % 52 + 1;
        else if (oldTileNumber < 76)
            return 53 + (oldTileNumber - 52) % 6;
        else
            return 0;
    }    
    
    /**
     * Reads the values of a table with QLearning numbers from file
     * @return table with values
     */
    private double[][] readQTable() {
        double[][] table = new double[BOARDLENGTH][6];
        try {        
            Scanner input = new Scanner(new File("src/resources/qlearndata.txt"));
            input.useDelimiter("\n");
            for (int i = 0; i < BOARDLENGTH; i++) {
                for (int j = 0; j < 6; j++) {
                    table[i][j] = Double.parseDouble(input.next());
                }
            }
            input.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Error: cannot find file qlearndate.txt");
            Logger.getLogger(AI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return table;
    }
    
    public double[][] getQTable() {
        return this.QTable;
    }
    
    /**
     * Sets the value in a specific cell in QTable
     * @param column
     * @param row
     * @param value between 0-1
     */
    public void setValueQTable(int column, int row, double value) {
        this.QTable[column][row] = value;
    }
    
   /**
    * Find if a tile is a hometile for an opponent and if there are any tokens home of that player
    * @param tile tile to be checked
    * @return 
    */
    public boolean isStartTile(int tile) {
        int player;
        
        for (int i = 1; i < 4; i++) {  // checking if tile is the start tile of another player
            player = (state.getCurrPlayer() + i) % 4;
            if (players[player].getStartTile() == tile) 
                for (int j = 0; j < 4; j++)  // checking if there are tokens at home for the specific player
                    if (players[player].getTokenPos(j) >= players[player].getFirstHomeTile() &&
                           players[player].getTokenPos(j) <= players[player].getFirstHomeTile()+3)
                        return true;
        }
        return false;
    }    
    
}
