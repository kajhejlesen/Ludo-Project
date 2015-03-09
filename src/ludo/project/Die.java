/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ludo.project;
import java.util.Random;

/**
 * Simulates a die of n sides
 * @author Kaj Hejlesen
 */
public class Die {
    int sides;
    Random roll;
    
    public Die(int sides) {
        this.sides = sides;
        roll = new Random();
    }

   /**
    * Rolling the die
    * @return result of the die roll
    */ 
    public int rollDie() {
        return roll.nextInt(this.sides) + 1;
    }
}  
