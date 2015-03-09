/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ludo.project;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Class to hold Player objects
 * @author Kaj Hejlesen
 */
public class Player {
    
    int startTile, playerNumber, firstHomeTile, startOfVictoryLane;
    Token[] tokens = new Token[4];
    Image ship;
    Color color;
    
    public Player(int number) {
        this.playerNumber = number;
        this.startTile = Game.BOARDSIZE/4 * playerNumber; // tile where tokens enter play
        this.firstHomeTile = 76 + playerNumber * 4;
        this.startOfVictoryLane = 52 + playerNumber * 6; // start of the central track
        this.color = Color.GRAY; // default color
        createTokens();
        assignShip(playerNumber);
    }
    
   /**
    * Creates 4 tokens and assign them to the players house
    */
    public void createTokens() {
        for (int i = 0; i < 4; i++)
            this.tokens[i] = new Token(playerNumber, firstHomeTile + i);
    }
    
    
   /**
    * get current position of a specific token
    * @param token tokennumber (0-3)
    * @return position of token
    */
    public int getTokenPos(int token) {
        return tokens[token].getPos();
    }
    
   /**
    * sets position of a specific token
    * @param token tokennumber (0-3)
    * @param position new position of token
    */
    public void setTokenPos(int token, int position) {
        tokens[token].setPos(position);
    }
    
   /**
    * Moves all tokens home
    */ 
    public void resetTokens() {
        for (int i = 0; i < 4; i++) {
            if (getTokenPos(i) < 76)
                setTokenPos(i, checkFirstHomeTile());
        }
    }
    
   /**
    * Load a ship sprite into memory and assigns it to a player
    * @param p playernumber (0-3)
    */
    private void assignShip(int p) {
        try {
            this.ship = ImageIO.read(new File("src/images/ship" + p + ".png"));
        } catch (IOException e) {
            System.out.println("Error reading dir: " + e.getMessage());
        }
    }
    
   /**
    * checks the location of the first empty tile in the players house
    * @return location of first empty tile
    */   
    public int checkFirstHomeTile() {
        int firstEmptyTile = -1;
        boolean isEmpty;
        for (int tile = firstHomeTile; tile < firstHomeTile + 4; tile++) {
            isEmpty = true;
            
            for(Token t : tokens) {
                if (t.getPos() == tile){
                    isEmpty = false;
                    break;
                }
            }
            if (isEmpty == true) {
                firstEmptyTile = tile;
                break;
            }
        }
        return firstEmptyTile;
    }
    
    
   /**
    * Checking if player has any token in play on the board
    * @return boolean
    */ 
    public boolean getOnBoard() {
        boolean onBoard = false;
        for (Token t : tokens) {
            if (t.getPos() < Game.BOARDSIZE) {
                onBoard = true; 
                break;
            }
        }
        return onBoard;
    }

   /**
    * Checking if player has any token in play on the victory lane
    * @return boolean
    */ 
    public boolean getOnVictoryLane() {
        boolean onLane = false;
        
        for (Token t : tokens) {
            if ((t.getPos() >= startOfVictoryLane && t.getPos() < this.getWinTile())) {
                onLane = true;
            }
        }
        return onLane;
    }

   /**
    * Checking if player has any token in active play anywhere
    * (might change later)
    * @return boolean
    */     
    public boolean getInPlay() {
        return getOnVictoryLane() || getOnBoard();
    }
    
   /**
    * Keeping track on how many tokens have reached the end for victory conditions
    * @return number of token having reached the end
    */         
    public int getAtEnd() {
        int amount = 0;
        for (Token t : tokens) {
            if (t.getPos() == this.getWinTile()) 
                amount += 1;
        }
        return amount;
    }    

    public Image getShip() {
        return ship;
    }
    
    public int getFirstHomeTile() {
        return this.firstHomeTile;
    }

    public int getStartTile() {
        return this.startTile;
    }
  
    public int getEndTile() {
        return (this.startTile + Game.BOARDSIZE-2) % Game.BOARDSIZE;
    }
    
    public int getStartOfVictoryLane() {
        return this.startOfVictoryLane;
    }

   /**
    * Get the location of the central tile a token has to reach at the end
    * @return location of end tile
    */   
    public int getWinTile() {
        return this.startOfVictoryLane + 5;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
}