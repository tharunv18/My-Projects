package entity;

import main.Game;

/****************************************************************
 * Name: NonLivingEntity.java
 * Authors: Ivy, Tharun, Shubham
 * Date: November 21, 2022
 * Purpose: to reserve entities that have no affect on the game,
 * 			such as road, water, tree, etc.
 ****************************************************************/

public class NonLivingEntity extends Entity {

  private Game game; // the game in which the alien exists

  /* construct a new alien
   * input: game - the game in which the alien is being created
   *        r - the image representing the alien
   *        x, y - initial location of alien
   */
  public NonLivingEntity(Game g, String r, int newX, int newY) {
	  super(r, newX, newY);  // calls the constructor in Entity
	  game = g;  // start off moving left
  } // constructor
  
   //collisions dealt with in other classes
   public void collidedWith(Entity other) {
	   
   } // collidedWith
  
} // NonLivingEntity class