package entity;

import main.Game;

/*********************************************************************
 * Name: ShieldEntity.java
 * Authors: Ivy, Tharun, Shubham
 * Date: November 21, 2022
 * Purpose: gives the monkey the ability to use the shield and checks
 * 			if a shield is placed on top of another shield.
 *********************************************************************/

public class ShieldEntity extends Entity {

  private double moveSpeed = 0; // horizontal speed

  private Game game; // the game in which the alien exists

  /* construct a new alien
   * input: game - the game in which the alien is being created
   *        r - the image representing the alien
   *        x, y - initial location of alien
   */
  public ShieldEntity(Game g, String r, int newX, int newY) {
    super(r, newX, newY);  // calls the constructor in Entity
    game = g;
    dx = -moveSpeed;  // start off moving left
  } // constructor

   // checks if a shield is placed on top of another shield
   public void collidedWith(Entity other) {
	   if (other instanceof ShieldEntity) {
		   game.removeEntity(this);
	  } // if
   } // collidedWith  
} // ShieldEntity class