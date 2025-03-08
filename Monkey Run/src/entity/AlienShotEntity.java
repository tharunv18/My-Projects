package entity;

/*******************************************************************
 * Name: AlienShotEntity.java
 * Authors: Ivy, Tharun, Shubham
 * Date: November 21, 2022
 * Purpose: gives the zookeeper the ability to shoot at the monkey
 * 			and checks if there is any collision between its shot
 * 			and monkey's shot, monkey, or monkey's shield.
 *******************************************************************/

import main.Game;

public class AlienShotEntity extends Entity {

  private double moveSpeed = 300; // vert speed shot moves
  private boolean used = false; // true if shot hits something
  private Game game; // the game in which the ship exists

  /* construct the shot
   * input: game - the game in which the shot is being created
   *        ref - a string with the name of the image associated to
   *              the sprite for the shot
   *        x, y - initial location of shot
   */
  public AlienShotEntity(Game g, String r, int newX, int newY) {
    super(r, newX, newY);  // calls the constructor in Entity
    game = g;
    dx = -moveSpeed;
  } // constructor

  /* move
   * input: delta - time elapsed since last move (ms)
   * purpose: move shot
   */
  public void move (long delta){
    super.move(delta);  // calls the move method in Entity

    // if shot moves off top of screen, remove it from entity list
    if (x < 0) {
      game.removeEntity(this);
    } // if

  } // move

   // checks if a collision has occurred between the zookeeper's shot
   // and the monkey or the monkey's weapons
   public void collidedWith(Entity other) {
     // prevents double kills
     if (used) {
       return;
     } // if
     
     if (other instanceof ShotEntity) {
    	 game.removeEntity(this);
         game.removeEntity(other);
         used = true;
         return;
     }
     // if it has hit an alien, kill it!
     if (other instanceof MonkeyEntity) {
       // remove affect entities from the Entity list
       game.removeEntity(this);
       game.removeEntity(other);
       
       // notify the game that the alien is dead
       game.notifyDeath();
       used = true;
     } // if

     if (other instanceof ShieldEntity) {
         // remove affect entities from the Entity list
         game.removeEntity(this);
         game.removeEntity(other);
         used = true;
       } // if
   } // collidedWith

} // ShipEntity class
