/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ludo.project;

/**
 * Class to hold Token objects with it's position
 * @author Kaj Hejlesen
 */
public class Token {
    int position;
    
    public Token(int playerNumber, int pos) {
        this.position = pos;
    }
    
    public int getPos(){
        return position;
    }

    public void setPos(int pos) {
        this.position = pos;
    }    
}