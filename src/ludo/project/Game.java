/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ludo.project;

import javax.swing.SwingUtilities;


/**
 * Game class of the project, handles the initialization of the game as well as restarts
 * 
 * @author Kaj Hejlesen
 */
public class Game {
    
    final static int BOARDSIZE = 52;
    final static int TRIES = 3; // number of tries allowed when no token are in play
    
    private String[] args;
    State state;
    GamePlay turn;
    Tile[] tiles;
    Player[] players;
    StartMenu menu;
    DrawBoard drawer;
    StartMenu stMenu;
    AI kermIT;

    public Game(String[] args) {
        this.args = args;
    }

   /**
    * Creates the state object holding all the information about the game
    * @return state object
    */
    public State initState() {
        state = new State(this);
        return state;
    }
    
   /**
    * Creates turn, a GamePlay object to handle the core game logic
    */
    public void initTurn() {
        turn = new GamePlay(state, this);
    }
    
   /**
    * Creates the tileset used and calls updateTiles
    */
    public void initTiles() {
        tiles = new Tile[92];
        this.updateTiles();
    }
    
   /**
    * Distributes objects to classes that have to use them
    */ 
    public void distObjects() {
        state.importTiles(tiles);
        state.importPlayers(players);
        turn.importObjects(tiles, players, kermIT);
    }
    
    /**
    * Set up the drawing of the startMenu
    */
    public void startMenu() {
        SwingUtilities.invokeLater(new Runnable() { // using invokeLater to avoid the graphics hanging
            @Override
            public void run() {
                menu = new StartMenu(state, players);
            }
        });
    }
    
   /**
    * Set up the drawing of the GUI
    */
    public void draw() {
        SwingUtilities.invokeLater(new Runnable() { // using invokeLater to avoid the graphics hanging
            @Override
            public void run() {
                drawer = new DrawBoard(state, turn);
                drawer.setVisible(true);
            }
        });
    }
    
   /**
    * Updates the tile attributes, globes, stars and X,Y positions to be drawn
    */
    public void updateTiles() {

        int size = state.getSize(); // the conversion between the position in the coordinate system, and the position in the frame
        int x = 14, y = 28;     // coordinate of the starting tile (0). In the coordinate system each normal tile spans 2x and 2y
        int[] globes, stars;
        
        for (int i = 0; i < 92; i++)
            tiles[i] = new Tile(TileType.empty, 0, 0);  // Creates the tiles, setting all to empty initially
  
        
        // arranging the tiles in the coordinate system, starting with the first tile and positioning the others based on the previous one
        for (int i = 0; i < 92; i++) {
            tiles[i].setX(x * size);
            tiles[i].setY(y * size);
            
            // Placing a number of tiles in arcs if layout 'space'
            if (state.getLayout().equals("space")) {
                if (i >= 1 && i  <= 8) {
                    tiles[i].setX( (4 * size) + (int)(size * 10 * Math.cos( (i) * Math.PI / 18  )));  
                    tiles[i].setY( (28 * size) - (int)(size * 10 * Math.sin( (i) * Math.PI / 18  )));                      
                } else if (i >= 14 && i <= 21) {
                    tiles[i].setX( (4 * size) + (int)(size * 10 * Math.cos( (22 - i) * Math.PI / 18  )));
                    tiles[i].setY( (4 * size) + (int)(size * 10 * Math.sin( (22 - i) * Math.PI / 18  )));
                } else if (i >= 27 && i <= 34) {
                    tiles[i].setX( (28 * size) + (int)(size * 10 * Math.cos( (44 - i) * Math.PI / 18  )));
                    tiles[i].setY( (4 * size) + (int)(size * 10 * Math.sin( (44 - i) * Math.PI / 18  )));                    
                } else if (i >= 40 && i <= 47) {
                    tiles[i].setX( (28 * size) + (int)(size * 10 * Math.cos( (i - 30) * Math.PI / 18  )));
                    tiles[i].setY( (28 * size) - (int)(size * 10 * Math.sin( (i - 30) * Math.PI / 18  )));                    
                }
            }
            
            // Setting x-coord based on the the previous tile
            if (       i >= 12 && i <= 17 || 
                       i >= 23 && i <= 24 || 
                       i >= 30 && i <= 35 ||
                       i >= 58 && i <= 62 || 
                       i == 51)
                x += 2;
             else if ( i >= 38 && i <= 43 ||
                       i >= 70 && i <= 74 || 
                       i >= 49 && i <= 50 ||
                       i >= 4  && i <= 9)
                x -= 2;
                        
            // Setting y-coord based on the previous tile
            if (       i >= 25 && i <= 30 || 
                       i >= 64 && i <= 68 || 
                       i >= 36 && i <= 37 ||
                       i >= 43 && i <= 48)
                y += 2;
            else if (  i >= 0  && i <= 4  || 
                       i >= 52 && i <= 56 || 
                       i >= 10 && i <= 11 ||
                       i >= 17 && i <= 22 || 
                       i == 51)
                y -= 2; 
 
            // Setting special cases (players home, center tracks)
            else if (  i == 76 || i == 78 || 
                       i == 80 || i == 82 ||
                       i == 84 || i == 86 || 
                       i == 88 || i == 90) {
                x += 2;
                y += 2;
            } else if (i == 81 || i == 85 || 
                       i == 77 || i == 89) {
                x -= 4;
            }
            else if (  i == 57) { x -= 12 ; y -= 2;  }
            else if (  i == 63) { x += 2  ; y -= 12; }
            else if (  i == 69) { x += 12 ; y += 2;  }
            else if (  i == 75) { x -= 11 ; y += 7;  }
            else if (  i == 79) {           y -= 22; }
            else if (  i == 83) { x += 18 ; y -= 4;  }
            else if (  i == 87) {           y += 14; }
        }
        
        // Setting the attributes of the tiles if isStarsAndGlobes is set to true
        if (state.isStarsAndGlobes()) {
            globes = new int[] {8, 21, 34, 47};
            stars = new int[] {5, 11, 18, 24, 31, 37, 44, 50};
        
            for (int globe : globes)
                tiles[globe].setAtt(TileType.globe);
        
            for (int star : stars)
                tiles[star].setAtt(TileType.star);
        }
    }
   
   /**
    * Creates 4 new players. The players are created even if the player is not actively in the game
    * @return array of players
    */
    public Player[] initPlayers() {
        players = new Player[4];
        for (int i = 0; i < 4; i++) {
                players[i] = new Player(i);
        }
        return players;
    }
    /**
     * Setting up the AI (excluding the training algorithm for Q-Learning
     */
    public void initKermIT() {
        kermIT = new AI(state, players, tiles, turn);
    }
    
    /**
     * Setup the different objects needed in the game and calls the startmenu to allow that to set up the parameters
     * Handles the 
     */
    public void setupGame() {

 
        initState();

        // applying arguments
        for (String arg : args) {

            // set trainingmode on, and substitutes any human player
            if (arg.equalsIgnoreCase("train")) {
                state.setTrainingMode(true);
                for (int i = 0; i < 4; i++) {
                    if (state.getPlaying(i) == PlayerType.human) {
                        state.setPlaying(i, PlayerType.AI_easy);  // making sure there are no human players
                    }
                }
            }
            
            // set testMode on, allowing the user to decide die rolls
            else if (arg.equalsIgnoreCase("cheat"))
                state.setTestMode(true);
        }

        initTurn();
        initTiles();
        initPlayers();
        initKermIT();
        if (!state.isTrainingMode())
            startMenu();
        else
            finalizeSetup();
    }
    
    /**
     * Finishes the setup, after parameters have been set
     */
    public void finalizeSetup() {
        // first time through when there is not any drawer object yet
        if (drawer == null) {
            draw();
            turn.setTimer(1000);
            distObjects();
        } else
            drawer.setVisible(true);    // setting the frame visible again after creating a new game
        
        testSetting();  // for testing the game with diffirent startparameters
        if (state.isTrainingMode()) {
            QLearn q = new QLearn(this, turn, tiles, state, kermIT);
            q.learn(); // starting learning algorithm
        }
        else 
            restartGame();
    }
    
   /**
    * Checks for the first player in the game and whether it is an AI player
    */
    public void firstPlayer() {
        for (int i = 0; i < 4; i++) {
            if (state.getPlayerInGame(i)) {
                state.setCurrPlayer(i);
                break;
            }
        }
        if (state.getCurrPlaying().toString().startsWith("AI"))
            turn.timer.start(); // if first player is an AI, start its move right away without waiting for input
    }
    
   /**
    * Resets game and start a new one with the same parameters
    */ 
    public void restartGame() {
        state.setGameState(1);
        state.setTries(1);
        state.setDieResult(0);
        state.resetPlayersFinished();
        state.resetPlayerRanking();
        for (int i = 0; i < 4; i++) {
            if (state.getPlayerInGame(i)) {
                players[i].resetTokens();
            }
        }
        testSetting();
        if (!state.isTrainingMode())
            firstPlayer();
    }
   /**
    * Set special condition for testing
    */
    public void testSetting() {
//        players[0].setTokenPos(0, 50);
//        players[0].setTokenPos(1, 50);
//        players[0].setTokenPos(2, 50);
//        players[0].setTokenPos(3, 50);
    }
    
}