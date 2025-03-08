package entity;

import entity.Entity;
import entity.MonkeyEntity;
import main.Game;

/*********************************************************** 
 * Name: ScoreEntity.java
 * Authors: Ivy, Tharun, Shubham
 * Date: November 21, 2022
 * Purpose: checks if the monkey has collected the banana.
 ***********************************************************
 */
public class ScoreEntity extends Entity {

  private boolean used = false; // true if shot hits something
  private Game game; // the game in which the ship exists

  /* construct the shot
   * input: game - the game in which the shot is being created
   *        ref - a string with the name of the image associated to
   *              the sprite for the shot
   *        x, y - initial location of shot
   */
  public ScoreEntity(Game g, String r, int newX, int newY) {
    super(r, newX, newY);  // calls the constructor in Entity
    game = g;
  } // constructor

  /* collidedWith
   * input: other - the entity with which the shot has collided
   * purpose: notification that the shot has collided
   *          with something
   */
   public void collidedWith(Entity other) {
     // prevents double kills
     if (used) {
       return;
     } // if
     
     // if it has hit an alien, kill it!
     if (other instanceof MonkeyEntity) {
    	 
       // remove affect entities from the Entity list
       game.removeEntity(this);     
     } // if

   } // collidedWith
} // ScoreEntity class