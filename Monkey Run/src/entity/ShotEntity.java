package entity;

import main.Game;

/****************************************************************** 
 * Name: ShotEntity.java
 * Authors: Ivy, Tharun, Shubham
 * Date: November 21, 2022
 * Purpose: gives the monkey the ability to shoot at the zookeeper
 * 			and checks for any collisions that have occurred.
 ******************************************************************/

public class ShotEntity extends Entity {
	
  private double moveSpeed = 300; // vert speed shot moves
  private boolean used = false; // true if shot hits something
  private Game game; // the game in which the ship exists

  /* construct the shot
   * input: game - the game in which the shot is being created
   *        ref - a string with the name of the image associated to
   *              the sprite for the shot
   *        x, y - initial location of shot
   */
  public ShotEntity(Game g, String r, int newX, int newY) {
	super(r, newX, newY);  // calls the constructor in Entity
	game = g;
	dx = moveSpeed;
  } // constructor

  /* move
   * input: delta - time elapsed since last move (ms)
   * purpose: move shot
   */
  public void move(long delta){
	super.move(delta);  // calls the move method in Entity
	
	// if shot moves off top of screen, remove it from entity list
	if (x > 1800) {
	  game.removeEntity(this);
	} // if

  } // move

   // checks if a collision has occurred between the monkey's shot
   // and the zookeeper, zookeeper's shot, monkey's shield
   public void collidedWith(Entity other) {
	 // prevents double kills
	 if (used) {
	   return;
	 } // if
	 
	 if (other instanceof AlienShotEntity) {
		 game.removeEntity(this);
	     game.removeEntity(other);
	 } // if
	 
	 // if it has hit an alien, kill it!
	 if (other instanceof AlienEntity) {
	   // remove affect entities from the Entity list
	 game.playSound("klonk.wav");
	 other.health -= 300;
	 game.removeEntity(this);
	 
	 if (other.health == 0) {
		 game.notifyAlienKilled();
	     game.removeEntity(other);
	 } // if
	 
	 switch (other.healthBarNum) {
		 case 1:
			 game.setHealthBarImage(game.heart1, other);
			 break;
		 case 2:
			 game.setHealthBarImage(game.heart2, other);
			 break;
		 case 3:
			 game.setHealthBarImage(game.heart3, other);
			 break;
		 case 4: 
			 game.setHealthBarImage(game.heart4, other);
			 break;
		 case 5:
			 game.setHealthBarImage(game.heart5 , other);
			 break;
	 } // switch 
	
	   used = true;
	 } // if
	
	 if (other instanceof ShieldEntity) {
	     game.removeEntity(this);
	     used = true;
	   } // if
   } // collidedWith

} // ShotEntity class