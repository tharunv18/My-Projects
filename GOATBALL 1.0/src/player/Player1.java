package player;

import main.Game;

/**********************************************************
 * Name: MonkeyEntity.java
 * Authors: Ivy, Tharun, Shubham
 * Date: November 21, 2022
 * Purpose: gives the monkey the ability to move 
 * 			and checks if a collision has occurred between
 * 			the monkey and a NonLivingEntity, banana, or
 * 			zookeeper.
 **********************************************************/
public class Player1 extends Player {

  private Game game; // the game in which the ship exists

  /* construct the player's ship
   * input: game - the game in which the ship is being created
   *        ref - a string with the name of the image associated to
   *              the sprite for the ship
   *        x, y - initial location of ship
   */
  public Player1(Game g, String r, int newX, int newY) {
    super(r, newX, newY);  // calls the constructor in Entity
    game = g;
  } // constructor

  // checks if the monkey is past a certain boundary
  public void move (long delta){
	  
	// stop at left side of screen
    if ((dx < 0) && (x < 10)) {
      return;
    } // if
    
    // stop at right side of screen
    if ((dx > 0) && (x > 1800)) {
      return;
    } // if
    
    // stops at certain top limit
    if ((dy < 0) && (y < 100)) {
      return;
    } // if
    
    // stops at certain bottom limit    
    if ((dy > 0) && (y > 525)) {
      return;
    } // if 
    
    super.move(delta); // calls the move method in Entity
  } // move  
  
   // 
   public void collidedWith(Player other) {
   } // collidedWith
} // MonkeyEntity class