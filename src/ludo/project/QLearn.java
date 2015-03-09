/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ludo.project;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Q-Learning algorithm
 * @author Kaj Hejlesen
 */
public class QLearn {
    
    final double alpha = 0.001; // learning rate
    final double gamma = 0.95; // discount factor
    
    Game game;
    GamePlay turn;
    State state;
    AI kermIT;
    Tile[] tiles;
    int iterations, count;
    int[] firstPlaces = new int[4]; // keeping track of game winners for evaluation of AI's
    
    public QLearn(Game game, GamePlay turn, Tile[] tiles, State state, AI kermIT) {
        this.game = game;
        this.turn = turn;
        this.state = state;
        this.kermIT = kermIT;
        this.tiles = tiles;
    }
    
    /**
     * Managing the game when in learning mode. This substitutes the normal input or timers in other classes
     */
    public void learn() {
        iterations = 50000; // plays 50000 games
        count = 0;
        boolean gameFinished;
        
        while (count < iterations) {
            gameFinished = false;
            
            // runs an entire game
            while (!gameFinished) {
                
                if (state.getGameState() == 1) {
                    do {
                        turn.startPlayerTurn();
                    } while (state.getTries() > 0 && state.getTries() <= Game.TRIES);

                    turn.checkForLegalMoves();
                    
                    if (!state.anyTokenHasLegalMove()) {
                        turn.nextPlayer();
                    }

                } else if (state.getGameState() == 2) {
                    turn.moveToken(learningAlg());
                    if (!state.isAnyPlayerInGame())
                        gameFinished = true;
                    else
                        turn.nextPlayer();
                }
            }
            count++;
            if (count % 1000 == 0) 
                printQTable(); // prints QTable every 1000 game
            firstPlaces[state.getPlayerRanking()[0]]++; // keeping track of winners
            game.restartGame();
        }
        
        writeResultsToFile(); // writes the result of Q-Learning to a file
        printRankings(); // prints out how many game each player won for testing
        System.exit(0);
    }
    
    /**
     * learning algorithm for Q-Learning
     * @return token, that have been used in the learning algorithm
     */
    public int learningAlg() {
        int oldP, newP, token;
        int dieR = state.getDieResult();
        double r;
        token = kermIT.chooseToken();   // allowing all the different AI's to be used
        
        oldP = kermIT.convertTileToArray(state.getOldPositions()[token], state.getCurrPlayer());
        
        if (tiles[state.getNewPositions()[token]].getAtt() == TileType.star)
            newP = kermIT.convertTileToArray(state.findNextStar(state.getNewPositions()[token]), state.getCurrPlayer());
        else
            newP = kermIT.convertTileToArray(state.getNewPositions()[token], state.getCurrPlayer());

        //reward function
	if ( newP == AI.BOARDLENGTH-1 ) {
            r = 1; // 1 point given if token lands on last square
        } else {
            r = 0; // no points otherwise
	}
        // learning
	kermIT.setValueQTable(oldP, dieR-1, (1-alpha)*kermIT.getQTable()[oldP][dieR-1] + alpha*(r + gamma * kermIT.QForAvgAction(newP)));
  
        return token;
    }
    
    /**
     * Handles writing the result of Q-Learning to a file
     */
    public void writeResultsToFile() {
        try {
            PrintWriter output = new PrintWriter("src/resources/qlearndata.txt");
            for (double[] col : kermIT.getQTable()) {
                for (double value : col) {
                    output.println(value);
                }
            }
            output.close();
            
        } catch (FileNotFoundException ex) {
            System.out.println("error: qlearndata.txt not found.");
            Logger.getLogger(QLearn.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    /**
     * Prints the QTable to console
     **/
    void printQTable() {
        double[][] table = kermIT.getQTable();
	    for(int i=0;i<AI.BOARDLENGTH;i++) {
                	for(int j=0;j<6;j++) {
		System.out.print( String.format( "%.3f", table[i][j] ) + " "); //prints with 3 decimals
	    }
	    System.out.println("");
	}
	System.out.println("");
    }
    
    /**
     * Prints the winning percentages of each player in the console
     */
    void printRankings() {
        for (int i = 0; i < 4; i++)
            System.out.printf("%.1f ", firstPlaces[i]*100.0 / iterations);
        System.out.println();
    }   
}
