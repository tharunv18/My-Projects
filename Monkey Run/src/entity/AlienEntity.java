package entity;

/*********************************************** 
 * Name: AlienEntity.java
 * Authors: Ivy, Tharun, Shubham
 * Date: November 21, 2022
 * Purpose: gives the alien the ability to move.
 **********************************************/

import main.Game;

public class AlienEntity extends Entity {

  private double moveSpeed = 125; // horizontal speed
  
  private Game game; // the game in which the alien exists

  /* construct a new alien
   * input: game - the game in which the alien is being created
   *        r - the image representing the alien
   *        x, y - initial location of alien
   */
  public AlienEntity(Game g, String r, int newX, int newY) {
    super(r, newX, newY);  // calls the constructor in Entity
    game = g;
    dx = -moveSpeed;  // start off moving left
    health = 300;
  } // constructor
  
  public AlienEntity(Game g, String r, int newX, int newY, int healthBarNum) {
	    super(r, newX, newY);  // calls the constructor in Entity
	    game = g;
	    dx = -moveSpeed;  // start off moving left
	    health = 300;
	    this.healthBarNum = healthBarNum;
	  } // constructor
  
  /* move
   * input: delta - time elapsed since last move (ms)
   * purpose: move alien
   */
  
  public void move (long delta){    

    // if we reach right side of screen and are moving right
    // request logic update
    if ((dx > 0) && (x > 1000)) {
      game.updateLogic();
    } // if
    
    if (dx < 0 && x < 0) {
    	game.notifyDeath();
    }
    
    // proceed with normal move
    super.move(delta);
  } // move

  /* doLogic
   * Updates the game logic related to the aliens,
   * ie. move it down the screen and change direction
   */
  public void doLogic() {
    // swap horizontal direction and move down screen 10 pixels
     x -= 10;
  } // doLogic
  
   //collisions dealt with in other classes
   public void collidedWith(Entity other) {
	   
   } // collidedWith
  
} // AlienEntity class