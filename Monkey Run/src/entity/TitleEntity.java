package entity;

import main.Game;

/*************************************************************** 
 * Name: TitleEntity.java
 * Authors: Ivy, Tharun, Shubham
 * Date: November 21, 2022
 * Purpose: a class that has no effect on the game but allows
 * 			a title screen and instructions to be printed from
 * 			Game.java
 ***************************************************************/

public class TitleEntity extends Entity {

  private Game game; // the game in which the alien exists

  /* construct a new alien
   * input: game - the game in which the alien is being created
   *        r - the image representing the alien
   *        x, y - initial location of alien
   */
  public TitleEntity(Game g, String r) {
    super(r, 0, 0);  // calls the constructor in Entity
    game = g;
  } // constructor
  
  // collisions dealt with in other classes
  public void collidedWith(Entity other) {
	  
  } // collidedWith  
} // TitleEntity class