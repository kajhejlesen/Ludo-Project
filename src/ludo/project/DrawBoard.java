/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ludo.project;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

class DrawPanel extends JPanel {

    BufferedImage image;
    State state;
    GamePlay turn;
    Timer timer;
    Tile[] tiles;
    Player[] players;
    Image backgroundImg, star, rock, rock2, bolt;
    int count, size, tokenSelected, moveX, moveY;
    Color lightBlue, background, lighterGrey;
    
    /**
    * Draws the elements of the GUI as well as mouseListeners
    * @author Kaj Hejlesen
    */
    public DrawPanel(State state, GamePlay turn) {
        this.state = state;
        this.turn = turn;
        count = 0;
        tokenSelected = -1; // token highlighted of the current player
        size = state.getSize();
        tiles = state.getAllTiles();
        players = state.getAllPlayers();
        lightBlue = new Color(80, 120, 240);
        background = new Color(220, 235, 255);
        lighterGrey = new Color(230, 230, 230);
        loadImages();
    }

    /**
     * Draws the die in classic theme
     * @param g 
     */
    private void drawDie(Graphics g) {
        Graphics2D die = (Graphics2D) g;
        Color aniDieColor;
        
        int dieX = 0, dieY = 0; 
        
        // Sets the position of the topleft corner of the die
        switch (state.getCurrPlayer()) {
            case 0:
                dieX = 9 * size;
                dieY = 20 * size;
                break;
            case 1:
                dieX = 9 * size;
                dieY = 9 * size;
                break;
            case 2:
                dieX = 20 * size;
                dieY = 9 * size;
                break;                
            case 3:
                dieX = 20 * size;
                dieY = 20 * size;
                break;                
        }
        
        int dieW = 3 * size; // size of the die
        int dieS = dieW / 5; // size of the dice spots

        // changing color based on timer if gameState = 1 to simulate flashing effect
        if (state.getGameState() ==1 && state.getTries() <= 3) {
            aniDieColor = new Color(255, 255 / ((count/3) % 6 + 1), 255 / ((count/3) % 6 + 1));
            die.setColor(aniDieColor);
        } else {
            die.setColor(Color.RED);
        }

        die.fillRoundRect(dieX, dieY, dieW, dieW, dieW / (size / 3), dieW / (size / 3));

        die.setColor(Color.BLACK);
        die.drawRoundRect(dieX, dieY, dieW, dieW, dieW / (size / 3), dieW / (size / 3));

        die.setColor(Color.WHITE);
        
        // drawing the individual dice spots
        switch (state.getDieResult()) {

            case 5:
                die.fillOval(dieX + dieW / 5 - dieS / 2, dieY + dieW / 5 - dieS / 2, dieS, dieS);
                die.fillOval(dieX + dieW * 4 / 5 - dieS / 2, dieY + dieW * 4 / 5 - dieS / 2, dieS, dieS);

            case 3:
                die.fillOval(dieX + dieW * 4 / 5 - dieS / 2, dieY + dieW / 5 - dieS / 2, dieS, dieS);
                die.fillOval(dieX + dieW / 5 - dieS / 2, dieY + dieW * 4 / 5 - dieS / 2, dieS, dieS);

            case 1:
                die.fillOval(dieX + dieW / 2 - dieS / 2, dieY + dieW / 2 - dieS / 2, dieS, dieS);
                break;

            case 6:
                die.fillOval(dieX + dieW / 2 - dieS / 2, dieY + dieW / 5 - dieS / 2, dieS, dieS);
                die.fillOval(dieX + dieW / 2 - dieS / 2, dieY + dieW * 4 / 5 - dieS / 2, dieS, dieS);

            case 4:
                die.fillOval(dieX + dieW / 5 - dieS / 2, dieY + dieW / 5 - dieS / 2, dieS, dieS);
                die.fillOval(dieX + dieW * 4 / 5 - dieS / 2, dieY + dieW * 4 / 5 - dieS / 2, dieS, dieS);

            case 2:
                die.fillOval(dieX + dieW * 4 / 5 - dieS / 2, dieY + dieW / 5 - dieS / 2, dieS, dieS);
                die.fillOval(dieX + dieW / 5 - dieS / 2, dieY + dieW * 4 / 5 - dieS / 2, dieS, dieS);
                break;
        }
    }

    /**
     * Drawing the die (rocks) in space theme
     * @param g 
     */
    private void drawDieSpace(Graphics g) {
        Graphics2D die = (Graphics2D) g;
        int distance = 4 * size;
        int dieX = 34 * size;
        int animation;
        if (state.getGameState() == 1) {        // only animate in game state 1 when waiting for input
            animation = 64 * ((count) % 16);
        } else {
            animation = 0;
        }

        // Drawing rocks that acts as dice
        switch (state.getDieResult()) {
            case 6:
                die.drawImage(rock2, dieX - 2*size, 6*distance - 2*size,
                        dieX+2 * size, 6*distance+2 * size, 0 + animation, 0, 64+animation, 64, null);

            case 5:
                die.drawImage(rock2, dieX - 2 * size, 5 * distance - 2 * size,
                        dieX + 2 * size, 5 * distance + 2 * size, 0 + animation, 0, 64 + animation, 64, null);

            case 4:
                die.drawImage(rock2, dieX - 2 * size, 4 * distance - 2 * size,
                        dieX + 2 * size, 4 * distance + 2 * size, 0 + animation, 0, 64 + animation, 64, null);

            case 3:
                die.drawImage(rock2, dieX - 2 * size, 3 * distance - 2 * size,
                        dieX + 2 * size, 3 * distance + 2 * size, 0 + animation, 0, 64 + animation, 64, null);

            case 2:
                die.drawImage(rock2, dieX - 2 * size, 2 * distance - 2 * size,
                        dieX + 2 * size, 2 * distance + 2 * size, 0 + animation, 0, 64 + animation, 64, null);

            case 1:
                die.drawImage(rock2, dieX - 2 * size, distance - 2 * size,
                        dieX + 2 * size, distance + 2 * size, 0 + animation, 0, 64 + animation, 64, null);
        }
    }

    /**
     * Drawing the main board in classic and space themes
     * @param g 
     */
    private void drawGameBoard(Graphics g) {
        Graphics2D board = (Graphics2D) g;

        switch (state.getLayout()) {
            case "classic":

                // Drawing the background in the main game area
                board.setColor(background);
                board.fillRoundRect(size / 2, size / 2, 31 * size, 31 * size, 3 * size / 2, 3 * size / 2);
                board.setColor(Color.BLACK);
                board.drawRoundRect(size / 2, size / 2, 31 * size, 31 * size, 3 * size / 2, 3 * size / 2);

                // Drawing the houses
                Polygon house = new Polygon();
                house.addPoint(7 * size, 20 * size);
                house.addPoint(12 * size, 25 * size);
                house.addPoint(7 * size, 30 * size);
                house.addPoint(2 * size, 25 * size);

                board.setColor(players[0].getColor().brighter());
                board.fillPolygon(house);
                board.setColor(Color.BLACK);
                board.drawPolygon(house);

                house.translate(0, - 18 * size);
                board.setColor(players[1].getColor().brighter());
                board.fillPolygon(house);
                board.setColor(Color.BLACK);
                board.drawPolygon(house);

                house.translate(18 * size, 0);
                board.setColor(players[2].getColor().brighter());
                board.fillPolygon(house);
                board.setColor(Color.BLACK);
                board.drawPolygon(house);

                house.translate(0, 18 * size);
                board.setColor(players[3].getColor().brighter());
                board.fillPolygon(house);
                board.setColor(Color.BLACK);
                board.drawPolygon(house);

                // Drawing the outline of the mainboard, useful for the corner cases. Filling all with white
                Polygon mainTiles = new Polygon();
                mainTiles.addPoint(size, 13 * size);
                mainTiles.addPoint(12 * size, 13 * size);
                mainTiles.addPoint(13 * size, 12 * size);
                mainTiles.addPoint(13 * size, size);
                mainTiles.addPoint(19 * size, size);
                mainTiles.addPoint(19 * size, 12 * size);
                mainTiles.addPoint(20 * size, 13 * size);
                mainTiles.addPoint(31 * size, 13 * size);
                mainTiles.addPoint(31 * size, 19 * size);
                mainTiles.addPoint(20 * size, 19 * size);
                mainTiles.addPoint(19 * size, 20 * size);
                mainTiles.addPoint(19 * size, 31 * size);
                mainTiles.addPoint(13 * size, 31 * size);
                mainTiles.addPoint(13 * size, 20 * size);
                mainTiles.addPoint(12 * size, 19 * size);
                mainTiles.addPoint(size, 19 * size);

                board.setColor(Color.WHITE);
                board.fillPolygon(mainTiles);
                board.setColor(Color.BLACK);
                board.drawPolygon(mainTiles);

                board.drawLine(12 * size + size / 2, 19 * size + size / 2,
                        19 * size + size / 2, 12 * size + size / 2);

                board.drawLine(12 * size + size / 2, 12 * size + size / 2,
                        19 * size + size / 2, 19 * size + size / 2);

                // Drawing the four tiles in the middle, as diamonds
                Polygon winTile = new Polygon();
                winTile.addPoint(14 * size, 18 * size);
                winTile.addPoint(16 * size, 16 * size);
                winTile.addPoint(18 * size, 18 * size);
                winTile.addPoint(16 * size, 20 * size);

                board.setColor(players[0].getColor().brighter());
                board.fillPolygon(winTile);
                board.setColor(Color.BLACK);
                board.drawPolygon(winTile);

                winTile.translate(- 2 * size, - 2 * size);
                board.setColor(players[1].getColor().brighter());
                board.fillPolygon(winTile);
                board.setColor(Color.BLACK);
                board.drawPolygon(winTile);

                winTile.translate(2 * size, - 2 * size);
                board.setColor(players[2].getColor().brighter());
                board.fillPolygon(winTile);
                board.setColor(Color.BLACK);
                board.drawPolygon(winTile);

                winTile.translate(2 * size, 2 * size);
                board.setColor(players[3].getColor().brighter());
                board.fillPolygon(winTile);
                board.setColor(Color.BLACK);
                board.drawPolygon(winTile);

                board.setColor(Color.DARK_GRAY);
                board.fillRect(15 * size, 15 * size, 2 * size, 2 * size);

                // Drawing all tiles, one by one
                for (int i = 0; i < 92; i++) {
                    if (i >= 52 && i <= 57 || i == 0) {
                        board.setColor(players[0].getColor().brighter());
                    } else if (i >= 58 && i <= 63 || i == 13) {
                        board.setColor(players[1].getColor().brighter());
                    } else if (i >= 64 && i <= 69 || i == 26) {
                        board.setColor(players[2].getColor().brighter());
                    } else if (i >= 70 && i <= 75 || i == 39) {
                        board.setColor(players[3].getColor().brighter());
                    } else {
                        board.setColor(Color.WHITE);
                    }

                    if (       i <= 3
                            || i >= 6 && i <= 16
                            || i >= 19 && i <= 29
                            || i >= 32 && i <= 42
                            || i >= 45 && i <= 56
                            || i >= 58 && i <= 62
                            || i >= 64 && i <= 68
                            || i >= 70 && i <= 74) {
                        board.fillRect(tiles[i].getX() - size, tiles[i].getY() - size, 2 * size, 2 * size);
                        board.setColor(Color.BLACK);
                        board.drawRect(tiles[i].getX() - size, tiles[i].getY() - size, 2 * size, 2 * size);
                    }

                    if (i >= 76) {
                        board.setColor(Color.WHITE);
                        board.fillOval(tiles[i].getX() - size, tiles[i].getY() - size, 2 * size, 2 * size);
                        board.setColor(Color.BLACK);
                        board.drawOval(tiles[i].getX() - size, tiles[i].getY() - size, 2 * size, 2 * size);
                    }

                }

                // Drawing stars and globes
                for (int i = 0; i < 92; i++) {
                    if (tiles[i].getAtt() == TileType.globe || i == 0 || i == 13 || i == 26 || i == 39) {
                        if (tiles[i].getAtt() == TileType.globe) {
                            board.setColor(lighterGrey);

                            board.fillOval(tiles[i].getX() - 3 * size / 4,
                                    tiles[i].getY() - 3 * size / 4, 3 * size / 2, 3 * size / 2);
                        }
                        board.setColor(Color.BLACK);

                        board.drawOval(tiles[i].getX() - 3 * size / 4,
                                tiles[i].getY() - 3 * size / 4, 3 * size / 2, 3 * size / 2);

                        board.drawOval(tiles[i].getX() - size / 6,
                                tiles[i].getY() - 3 * size / 4, size / 3, 3 * size / 2);

                        board.drawOval(tiles[i].getX() - 3 * size / 4,
                                tiles[i].getY() - size / 6, 3 * size / 2, size / 3);

                    } else if (tiles[i].getAtt() == TileType.star) {

                        Polygon star = new Polygon();
                        star.addPoint(tiles[i].getX() + (int) (size * Math.cos(17 * Math.PI / 10)),
                                tiles[i].getY() + (int) (size * Math.sin(17 * Math.PI / 10)));

                        star.addPoint(tiles[i].getX() + (int) (size * Math.cos(5 * Math.PI / 10)),
                                tiles[i].getY() + (int) (size * Math.sin(5 * Math.PI / 10)));

                        star.addPoint(tiles[i].getX() + (int) (size * Math.cos(13 * Math.PI / 10)),
                                tiles[i].getY() + (int) (size * Math.sin(13 * Math.PI / 10)));

                        star.addPoint(tiles[i].getX() + (int) (size * Math.cos(1 * Math.PI / 10)),
                                tiles[i].getY() + (int) (size * Math.sin(1 * Math.PI / 10)));

                        star.addPoint(tiles[i].getX() + (int) (size * Math.cos(9 * Math.PI / 10)),
                                tiles[i].getY() + (int) (size * Math.sin(9 * Math.PI / 10)));

                        board.setColor(Color.DARK_GRAY);
                        board.fillPolygon(star);
                    }
                }
                break;

            // space theme
            case "space":

                // drawing background image
                board.drawImage(backgroundImg, 0, 0, this.getWidth(), this.getHeight(), null);

                for (int i = 0; i < 92; i++) {
                    int x = tiles[i].getX();
                    int y = tiles[i].getY();

                    // drawing the individual tiles (stars, globes, landing lights)
                    if (tiles[i].getAtt() == TileType.star)
                        // avoiding array errors with -1 value
                        if (tokenSelected != -1)
                            if (i == state.getNewPositions()[tokenSelected])
                                // drawing double size if star is the goal of a token
                                board.drawImage(star, x - 2*size, y - 2*size, x + 2*size, y + 2*size,
                                    0 + 128 * ((count / 2) % 16), 0, 128 + 128 * ((count / 2) % 16), 128, null);
                            else 
                                // normal size otherwise
                                board.drawImage(star, x - size, y - size, x + size, y + size,
                                    0 + 128 * ((count / 2) % 16), 0, 128 + 128 * ((count / 2) % 16), 128, null);
                        else
                            board.drawImage(star, x - size, y - size, x + size, y + size,
                                    0 + 128 * ((count / 2) % 16), 0, 128 + 128 * ((count / 2) % 16), 128, null);
 
                    else if (tiles[i].getAtt() == TileType.globe) {
                        if (tokenSelected != -1)
                            if (i == state.getNewPositions()[tokenSelected])
                                board.drawImage(bolt, x - 2*size / 2, y - 2*size / 2, x + 2*size / 2,
                                    y + 2*size / 2, 0 + 32 * ((count) % 16), 0, 32 + 32 * ((count) % 16), 64, null);
                            else
                                board.drawImage(bolt, x - size / 2, y - size / 2, x + size / 2,
                                y + size / 2, 0 + 32 * ((count) % 16), 0, 32 + 32 * ((count) % 16), 64, null);
                        else
                            board.drawImage(bolt, x - size / 2, y - size / 2, x + size / 2,
                                y + size / 2, 0 + 32 * ((count) % 16), 0, 32 + 32 * ((count) % 16), 64, null);

                    } else if (i < 76) {
                        if (tokenSelected != -1)
                            if (i == state.getNewPositions()[tokenSelected])
                                board.drawImage(rock, x - size, y - size, x + size, y + size, 0 + 64 * ((count/2) % 16), 0, 64 + 64 * ((count/2) % 16), 64, null);
                            else
                                board.drawImage(rock, x - size/2, y - size/2, x + size/2, y + size/2, 0, 0, 64, 64, null);
                        else
                            board.drawImage(rock, x - size/2, y - size/2, x + size/2, y + size/2, 0, 0, 64, 64, null);
                    }

                    if (i > 51 && i < 58) {

                        // Animating the landing lights
                        if (state.getPlayerInGame(0)) {
                            if (i - 48 >= (count / 4) % 16 + 1 && i - 48 <= (count / 4) % 16 + 3) {
                                board.setColor(players[0].getColor().brighter());
                                board.fillOval(x - size - 2, y + size - 2, 4, 4);
                                board.fillOval(x + size - 2, y + size - 2, 4, 4);
                            } else if (i - 48 == (count / 4) % 16 || i - 48 == (count / 4) % 16 + 4) {
                                board.setColor(players[0].getColor().darker());
                                board.fillOval(x - size - 2, y + size - 2, 4, 4);
                                board.fillOval(x + size - 2, y + size - 2, 4, 4);
                            }
                        }

                    } else if (i > 57 && i < 64) {
                        if (state.getPlayerInGame(1)) {
                            if (i - 54 >= (count / 4) % 16 + 1 && i - 54 <= (count / 4) % 16 + 3) {
                                board.setColor(players[1].getColor().brighter());
                                board.fillOval(x - size - 2, y - size - 2, 4, 4);
                                board.fillOval(x - size - 2, y + size - 2, 4, 4);

                            } else if (i - 54 == (count / 4) % 16 || i - 54 == (count / 4) % 16 + 4) {
                                board.setColor(players[1].getColor().darker());
                                board.fillOval(x - size - 2, y - size - 2, 4, 4);
                                board.fillOval(x - size - 2, y + size - 2, 4, 4);
                            }
                        }
                    } else if (i > 63 && i < 70) {
                        if (state.getPlayerInGame(2)) {
                            if (i - 60 >= (count / 4) % 16 + 1 && i - 60 <= (count / 4) % 16 + 3) {
                                board.setColor(players[2].getColor().brighter());
                                board.fillOval(x - size - 2, y - size - 2, 4, 4);
                                board.fillOval(x + size - 2, y - size - 2, 4, 4);

                            } else if (i - 60 == (count / 4) % 16 || i - 60 == (count / 4) % 16 + 4) {
                                board.setColor(players[2].getColor().darker());
                                board.fillOval(x - size - 2, y - size - 2, 4, 4);
                                board.fillOval(x + size - 2, y - size - 2, 4, 4);
                            }
                        }

                    } else if (i > 69 && i < 76) {
                        if (state.getPlayerInGame(3)) {
                            if (i - 66 >= (count / 4) % 16 + 1 && i - 66 <= (count / 4) % 16 + 3) {
                                board.setColor(players[3].getColor().brighter());
                                board.fillOval(x + size - 2, y - size - 2, 4, 4);
                                board.fillOval(x + size - 2, y + size - 2, 4, 4);

                            } else if (i - 66 == (count / 4) % 16 || i - 66 == (count / 4) % 16 + 4) {
                                board.setColor(players[3].getColor().darker());
                                board.fillOval(x + size - 2, y - size - 2, 4, 4);
                                board.fillOval(x + size - 2, y + size - 2, 4, 4);
                            }
                        }
                    }
                }

                // Displaying #tries left as circles above or below the players house
                if (state.getTries() > 0) {
                    board.setColor(players[state.getCurrPlayer()].getColor().brighter());
                    int startX = tiles[players[state.getCurrPlayer()].getFirstHomeTile()].getX() - 2 * size;
                    int startY;
                    if (state.getCurrPlayer() == 0 || state.getCurrPlayer() == 3) {
                        startY = tiles[players[state.getCurrPlayer()].getFirstHomeTile()].getY() + 6 * size;
                    } else {
                        startY = tiles[players[state.getCurrPlayer()].getFirstHomeTile()].getY() - 2 * size;
                    }
                    for (int i = 0; i < Game.TRIES; i++) {
                        board.drawOval(startX + i * 2*size - size/4, startY - size/4, size/2, size/2); // drawing empty circles
                        if (i < state.getTries()-1)
                            board.fillOval(startX + i * 2*size - size/4, startY - size/4, size/2, size/2); // filling them based on tries
                    }
                }
                
                // drawing the 'light circles' around the active players house
                int lights = 40; // number of lights in total
                int turnedOn = 3; // number of lights turned on in each go
                int repeatedAnimations = 4; // number of repeated sequences
                Color colorCircles;

                for (int i = 0; i < 4; i++) {
                    if (state.getPlayerInGame(i)) {
                        colorCircles = players[i].getColor();
                        for (int j = 0; j < lights; j++) {

                            if (i == state.getCurrPlayer() && (count/2 - j + lights) % (lights / repeatedAnimations) < turnedOn) {
                                board.setColor(colorCircles.brighter());
                            } else {
                                board.setColor(Color.DARK_GRAY);
                            }
                            double t = 2 * Math.PI * j / lights;

                            // getting the center of the circle, as it is just 2*size below the first tile
                            // of a players house
                            int x = (int) Math.round(tiles[players[i].getFirstHomeTile()].getX()
                                    + 3 * size * Math.cos(t));

                            int y = (int) Math.round(tiles[players[i].getFirstHomeTile()].getY()
                                    + 2 * size + 3 * size * Math.sin(t));

                            board.fillOval(x - 2, y - 2, 4, 4);
                        }
                    }
                }

                break;
        }
    }

    /**
     * Drawing the tokens in classic theme and ships in space theme
     * @param g 
     */
    private void drawTokens(Graphics g) {
        Graphics2D token = (Graphics2D) g;
        Random gen = new Random();
        int tok, player;
        for (int i = 0; i < 4; i++) {
            player = (state.getCurrPlayer() + i + 1) % 4; // drawing current players tokens last
            Color primColor = players[player].getColor();
            
            if (state.getPlayerStarted(player)) {
                for (int j = 0; j < 4; j++) {
                    if (tokenSelected != -1) // drawing a selected token last so it always is on top
                        tok = (tokenSelected + j + 1) % 4;
                    else
                        tok = j;
                    
                    int x = tiles[players[player].getTokenPos(tok)].getX();
                    int y = tiles[players[player].getTokenPos(tok)].getY();
                    
                    // the space shifted to make room for several tokens on a tile
                    int shift = (12 * size) / 20;
                    int[] multi = state.multipleTokensOnTile(player, tok);

                    switch (multi[0]) {

                        case 2:
                            if (multi[1] == 1) {
                                x = x - shift;
                            } else if (multi[1] == 2) {
                                x = x + shift;
                            }
                            break;
                        case 3:
                            if (multi[1] == 1) {
                                x = x - shift;
                                y = y - shift;
                            } else if (multi[1] == 2) {
                                x = x + shift;
                                y = y - shift;
                            } else if (multi[1] == 3) {
                                y = y + shift;
                            }
                            break;
                        case 4:
                            if (multi[1] == 1) {
                                x = x - shift;
                                y = y - shift;
                            } else if (multi[1] == 2) {
                                x = x + shift;
                                y = y - shift;
                            } else if (multi[1] == 3) {
                                x = x - shift;
                                y = y + shift;
                            } else if (multi[1] == 4) {
                                x = x + shift;
                                y = y + shift;
                            }
                    }

                    // drawing the token while being dragged
                    if (player == state.getCurrPlayer() && tok == tokenSelected) {
                        x = moveX;
                        y = moveY;
                    }
                        
                    switch (state.getLayout()) {

                        case "classic":

                            // drawing the animation on the new position when a token is selected
                            if (tokenSelected != -1) {
                                    int newX = tiles[state.getNewPositions()[tokenSelected]].getX();
                                    int newY = tiles[state.getNewPositions()[tokenSelected]].getY();
                                    int rev = 36;   // number of points per round
                                    
                                    double t = 2 * Math.PI * (count%rev) / rev;
                                    int rSize = 2 * size / 3;
                                    
                                    token.setColor(Color.BLACK);
                                    token.drawLine(newX + (int) Math.round(rSize * Math.cos(t)), 
                                            newY + (int) Math.round(rSize * Math.sin(t)), 
                                            newX - (int) Math.round(rSize * Math.cos(t)), 
                                            newY - (int) Math.round(rSize * Math.sin(t)));
                                    
                                    token.drawLine(newX + (int) Math.round(rSize * Math.cos(t + Math.PI/2)),
                                            newY + (int) Math.round(rSize * Math.sin(t + Math.PI/2)), 
                                            newX - (int) Math.round(rSize * Math.cos(t + Math.PI/2)), 
                                            newY - (int) Math.round(rSize * Math.sin(t + Math.PI/2)));
                                    token.setColor(players[state.getCurrPlayer()].getColor().darker());
                                    token.fillOval(newX - size / 4, newY - size / 4, size/2, size/2);
                                    
                                    token.drawOval(newX - size/2, newY - size/2, size, size);
                            }
                            
                                // animating the tokens that have legal moves
                                if (state.getCurrPlayer() == player && state.getTokensHasLegalMove()[tok] && tokenSelected == -1) {
                                    x += gen.nextInt(3) - 1;
                                    y += gen.nextInt(3) - 1;
                                }
                                
                                // creating and drawing the tokens, three polygons in different shades of the same color to create the 3D effect
                                Polygon side1 = new Polygon();
                                side1.addPoint(x - (7 * size) / 20, y - (13 * size) / 20);
                                side1.addPoint(x - size / 2, y - size / 2);
                                side1.addPoint(x - size / 2, y + size / 2);
                                side1.addPoint(x - (7 * size) / 20, y + (7 * size) / 20);

                                Polygon side2 = new Polygon();
                                side2.addPoint(x - size / 2, y + size / 2);
                                side2.addPoint(x - (7 * size) / 20, y + (7 * size) / 20);
                                side2.addPoint(x + (13 * size) / 20, y + (7 * size) / 20);
                                side2.addPoint(x + size / 2, y + size / 2);

                                token.setColor(primColor.brighter());
                                token.fillPolygon(side1);

                                token.setColor(primColor.darker());
                                token.fillPolygon(side2);

                                token.setColor(primColor);
                                token.fillRect(x - (7 * size) / 20, y - (13 * size) / 20, size, size);

                                token.setColor(Color.BLACK);
                                token.drawPolygon(side1);
                                token.drawPolygon(side2);
                                token.drawRect(x - (7 * size) / 20, y - (13 * size) / 20, size, size);
                            break;

                        case "space":
                            // drawing spaceship animated if it has legal move, otherwise remain as a single image (aniShip = 0)
                            int aniShip = 0;
                            if (state.getCurrPlayer() == player && state.getTokensHasLegalMove()[tok]) {
                                aniShip = 128 * ((count/2) % 16);
                            }
                            token.drawImage(players[player].getShip(), x - 3 * size / 2, y - 3 * size / 2, 
                                    x + 3 * size / 2, y + 3 * size / 2, 0 + aniShip, 0, 128 + aniShip, 128, null);
                            break;
                    }
                }
            }
        }
    }

    /**
     * Drawing the text in the right side in classic theme
     * @param g 
     */
    public void drawText(Graphics g) {
        Graphics2D text = (Graphics2D) g;

        text.setColor(Color.BLACK);
        text.rotate(Math.PI / 2);
        text.setFont(new Font("Serif", Font.BOLD, 5 * size));

        text.drawString("Player " + (state.getCurrPlayer() + 1), size, - 33 * size);

        text.setFont(new Font("Serif", Font.PLAIN, size));
        
        for (int i = 0; i < 4; i++) {
            if (state.getPlayerRanking()[i] == -1) break;
            else
                text.drawString("Player " + (state.getPlayerRanking()[i]+1) + " has finished", 23 * size, - (36 - i) * size);
        }
    }

    /**
     * Load images for space theme
     */
    private void loadImages() {
        try {
            backgroundImg = ImageIO.read(new File("src/images/spaceBackground.png"));
            star = ImageIO.read(new File("src/images/star.png"));
            rock = ImageIO.read(new File("src/images/rock.png"));        
            rock2 = ImageIO.read(new File("src/images/rock2.png"));  
            bolt = ImageIO.read(new File("src/images/bolt.png"));
          
        } catch (IOException e) {
            System.out.println("Error reading dir: " + e.getMessage());
        }
    }

    public void setSize(int s) {
        this.size = s;
    }

    /**
     * Creates new timer to control the animations
     */
    public void setTimer() {
        timer = new Timer(30, animation);
    }

    ActionListener animation = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            count++;
            repaint();
        }
    };

    public void updateSize() {
        this.size = state.getSize();
    }
    
    /**
     * Paints the panel
     * @param g 
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGameBoard(g);

        switch (state.getLayout()) {
            case "classic":
                drawDie(g);
                drawTokens(g);
                drawText(g);
                break;

            case "space":
                drawTokens(g);
                drawDieSpace(g);
                break;
        }
    }

    /**
     * setup mouseListener and mouseMotionListener and control their behaviour according to state
     */
    public void setMouseList() {
        addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                moveX = e.getX();
                moveY = e.getY();
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent me) {

                // only activating actionListeners when the current player is human
                if (state.getGameState() == 1 && state.getCurrPlaying() == PlayerType.human) {
                    turn.startPlayerTurn();
                } else if (state.getGameState() == 2 && state.getCurrPlaying() == PlayerType.human) {
                    tokenSelected = checkTokenInPosition(me.getX(), me.getY());
                    // this sets the initial x and y coordinate to avoid token being drawn in the wrong spot
                    moveX = me.getX();
                    moveY = me.getY();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent me) {
                int tempToken = tokenSelected; // needed to call the moveToken method to make sure tokenSelected is reset
                if (tokenSelected != -1 && checkNewPosition(me.getX(), me.getY()) ) {
                    tokenSelected = -1;
                    turn.moveToken(tempToken);
                }
                tokenSelected = -1;
            }            
        });
    }

    /**
     * Check if a token is within range to a coordinate value
     * @param x x coord
     * @param y y coord
     * @return token of current player in range of coordinate
     */
    private int checkTokenInPosition(int x, int y) {
        int range = (3 * state.getSize()) / 4;
        for (int i = 0; i < 4; i++)
            if (Math.abs(tiles[players[state.getCurrPlayer()].getTokenPos(i)].getX() - x) < range &&
                    Math.abs(tiles[players[state.getCurrPlayer()].getTokenPos(i)].getY() - y) < range &&
                    state.getTokensHasLegalMove()[i])
                    return i;
        return -1;
    }

    /**
     * check if a coordinate is within range of the new position of the currently selected token
     * @param x x coord
     * @param y y coord
     * @return true if tile is in range
     */
    private boolean checkNewPosition(int x, int y) {
        int range = state.getSize();
        return Math.abs(tiles[state.getNewPositions()[tokenSelected]].getX() - x) < range &&
                Math.abs(tiles[state.getNewPositions()[tokenSelected]].getY() - y) < range;
    }
}

/**
 * Sets up the frame for the GUI, including the menu and keyListeners
 * @author Kaj Hejlesen
 */
public class DrawBoard extends JFrame {

    State state;
    GamePlay turn;
    Game game;
    DrawPanel d;

    public DrawBoard(State s, GamePlay t) {
        this.state = s;
        this.turn = t;
        this.game = state.getLudoGame();

        initUI();
    }
    
    public DrawBoard getThis() {
        return this;
    }

    /**
     * Setup the menubar
     */
    public void setMenu() {

        JMenuBar menubar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);

        JMenuItem exit = new JMenuItem("Exit");
        exit.setMnemonic(KeyEvent.VK_E);
        exit.setToolTipText("Exit application");

        JMenuItem restart = new JMenuItem("Restart");
        restart.setMnemonic(KeyEvent.VK_R);
        restart.setToolTipText("Restart game with same parameters");
        
        JMenuItem newGame = new JMenuItem("New Game");
        newGame.setMnemonic(KeyEvent.VK_N);
        newGame.setToolTipText("Creates a new game");
        
        JMenu options = new JMenu("Options");
        options.setMnemonic(KeyEvent.VK_O);

        JMenu screenSize = new JMenu("Screen Size");
        screenSize.setMnemonic(KeyEvent.VK_S);

        JMenuItem small = new JMenuItem("Small");
        small.setMnemonic(KeyEvent.VK_S);
        small.setToolTipText("Set screen size to small");

        JMenuItem normal = new JMenuItem("Normal");
        normal.setMnemonic(KeyEvent.VK_N);
        small.setToolTipText("Set screen size to normal");

        JMenuItem large = new JMenuItem("Large");
        large.setMnemonic(KeyEvent.VK_L);
        large.setToolTipText("Set screen size to large");

        JMenu theme = new JMenu("Theme");
        theme.setMnemonic(KeyEvent.VK_L);

        JMenuItem classic = new JMenuItem("Classic");
        classic.setMnemonic(KeyEvent.VK_C);
        classic.setToolTipText("Set to classic");

        JMenuItem space = new JMenuItem("Space");
        space.setMnemonic(KeyEvent.VK_S);
        space.setToolTipText("Set to space");
        
        JMenu delay = new JMenu("Delay");
        delay.setMnemonic(KeyEvent.VK_D);
        delay.setToolTipText("Set delay of AI");
        
        JMenuItem delay0 = new JMenuItem("0 ms");        
        JMenuItem delay500 = new JMenuItem("500 ms");
        JMenuItem delay1000 = new JMenuItem("1000 ms"); 
        JMenuItem delay2000 = new JMenuItem("2000 ms");         

        JMenu help = new JMenu("Help");

        JMenuItem about = new JMenuItem("About");

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit", "Quit Game?", JOptionPane.YES_NO_OPTION);
                if (option == 0) System.exit(0);
            }
        });
        
        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to restart game?", "Restart Game?", JOptionPane.YES_NO_OPTION);
                if (option == 0) game.restartGame();
            }
        });        
        
        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to abondon this game and start a new one?", "Start New Game?", JOptionPane.YES_NO_OPTION);
                if (option == 0) {               
                    getThis().setVisible(false);
                    game.startMenu();
                }
            }
        });     
        

        classic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                state.setLayout("classic");
                game.updateTiles();
            }
        });

        space.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                state.setLayout("space");
                game.updateTiles();
            }
        });

        small.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                state.setSize(15);
                d.updateSize();
                game.updateTiles();
                updateDimension();
            }
        });

        normal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                state.setSize(20);
                d.updateSize();
                game.updateTiles();
                updateDimension();
            }
        });

        large.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                state.setSize(25);
                d.updateSize();
                game.updateTiles();
                updateDimension();
            }
        });

        delay0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                turn.setTimer(0);
            }
        });        
        
        delay500.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                turn.setTimer(500);
            }
        });             
        
        delay1000.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                turn.setTimer(1000);
            }
        });             
        
        delay2000.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                turn.setTimer(2000);
            }
        });

        file.add(newGame);
        file.add(restart);
        file.addSeparator();
        file.add(exit);
        options.add(theme);
        options.add(screenSize);
        options.add(delay);
        theme.add(classic);
        theme.add(space);
        screenSize.add(small);
        screenSize.add(normal);
        screenSize.add(large);
        delay.add(delay0);
        delay.add(delay500);
        delay.add(delay1000);
        delay.add(delay2000);
        help.addSeparator();
        help.add(about);

        menubar.add(file);
        menubar.add(options);
        menubar.add(Box.createHorizontalGlue());
        menubar.add(help);

        setJMenuBar(menubar);
    }

    /**
     * Updates the dimension of the frame, setting the size parameter in state
     */
    public void updateDimension() {
        setSize(38 * state.getSize(), 55 + 32 * state.getSize());
    }

    /**
     * Initialises the UI
     */
    public final void initUI() {

        d = new DrawPanel(state, turn);
        add(d);
        setTitle("Ludo Game");
        updateDimension();
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        d.setMouseList();
        setMenu();

        d.setTimer();
        d.timer.start();
        if (state.isTestMode()) // For easy testing
        {
            addKeyListener(new KeyList());
        }
    }

    /**
     * Handles the keyListener, only used for testing
     */
    class KeyList extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int keycode = e.getKeyCode();

            switch (keycode) {

                case KeyEvent.VK_1:
                    state.setDieResult(1);
                    break;

                case KeyEvent.VK_2:
                    state.setDieResult(2);
                    break;

                case KeyEvent.VK_3:
                    state.setDieResult(3);
                    break;

                case KeyEvent.VK_4:
                    state.setDieResult(4);
                    break;

                case KeyEvent.VK_5:
                    state.setDieResult(5);
                    break;

                case KeyEvent.VK_6:
                    state.setDieResult(6);
                    break;

            }

            turn.checkForLegalMoves();
        }
    }
}
