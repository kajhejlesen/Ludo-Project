/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ludo.project;

/**
 * Class to hold Tile objects. It's type, and it's coordinates
 * @author Kaj Hejlesen
 */
public class Tile {
    
    private TileType attribute;
    private int x;
    private int y;
    
    public Tile(TileType att, int xcoord, int ycoord) {
        this.attribute = att;
        this.x = xcoord;
        this.y = ycoord;
    }
    
    public void setAtt(TileType att) {
        this.attribute = att;
    }

    public TileType getAtt() {
        return this.attribute;
    }

    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }    

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
