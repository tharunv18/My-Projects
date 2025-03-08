package entity;

/**************************************************************
 * Name: Entity.java
 * Authors: Ivy, Tharun, Shubham
 * Date: November 21, 2022
 * Purpose: gets and sets the x and y locations of an image,
 * 			moves the entities, draws the entities, and checks
 * 			for a collision between two entities.
 **************************************************************/
 
 import java.awt.*;

import main.Sprite;
import main.SpriteStore;
 
 public abstract class Entity {

    // Java Note: the visibility modifier "protected"
    // allows the variable to be seen by this class,
    // any classes in the same package, and any subclasses
    // "private" - this class only
    // "public" - any class can see it
    
    protected double x;   // current x location
    protected double y;   // current y location
    protected Sprite sprite; // this entity's sprite
    protected double dx; // horizontal speed (px/s)  + -> right
    protected double dy; // vertical speed (px/s) + -> down
    public int health; // health of zookeeper
    protected int healthBarNum = 0; // zookeeper id
    
    private Rectangle me = new Rectangle(); // bounding rectangle of
                                            // this entity
    private Rectangle him = new Rectangle(); // bounding rect. of other
                                             // entities
                                             
    /* Constructor
     * input: reference to the image for this entity,
     *        initial x and y location to be drawn at
     */
     public Entity(String r, int newX, int newY) {
    	 x = newX;
    	 y = newY;
    	 sprite = (SpriteStore.get()).getSprite(r);
     } // constructor
     
     // get and set image
     public Sprite getImage() {
    	 return this.sprite;
     } // getImage
     
     public void setImage(String s) {
    	 this.sprite = (SpriteStore.get()).getSprite(s);
     } // setImage
     
     // sets x and y coordinates of image
     public void setCoords(int x, int y) {
    	 this.x = x;
    	 this.y = y;
     } // setCoords
     
     // get and set health
     public int getHealth() {
    	 return this.health;
     } // getHealth
     
     public void setHealth(int h) {
    	 this.health = h;
     } // setHealth
     
     /* move
      * input: delta - the amount of time passed in ms
      * output: none
      * purpose: after a certain amount of time has passed,
      *          update the location
      */
     public void move(long delta) {
    	 
    	 // update location of entity based on move speeds
    	 x += (delta * dx) / 1000;
    	 y += (delta * dy) / 1000;
     } // move

     // get and set velocities
     public void setHorizontalMovement(double newDX) {
    	 dx = newDX;
     } // setHorizontalMovement

     public void setVerticalMovement(double newDY) {
    	 dy = newDY;
     } // setVerticalMovement

     // gets horizontal and vertical movements
     public double getHorizontalMovement() {
    	 return dx;
     } // getHorizontalMovement

     public double getVerticalMovement() {
    	 return dy;
     } // getVerticalMovement

     // gets x and y positions
     public int getX() {
    	 return (int) x;
     } // getX

     public int getY() {
    	 return (int) y;
     } // getY
    
     // Draw this entity to the graphics object provided at (x,y)
     public void draw(Graphics g) {
       sprite.draw(g,(int)x,(int)y);
     } // draw
     
    /* Do the logic associated with this entity.  This method
     * will be called periodically based on game events.
     */
     public void doLogic() {}
     
     /* collidesWith
      * input: the other entity to check collision against
      * output: true if entities collide
      * purpose: check if this entity collides with the other.
      */
     public boolean collidesWith(Entity other) {
       me.setBounds((int)x, (int)y, sprite.getWidth(), sprite.getHeight());
       him.setBounds(other.getX(), other.getY(), 
                     other.sprite.getWidth(), other.sprite.getHeight());
       return me.intersects(him);
     } // collidesWith
     
     /* collidedWith
      * input: the entity with which this has collided
      * purpose: notification that this entity collided with another
      * Note: abstract methods must be implemented by any class
      *       that extends this class
      */
      public abstract void collidedWith(Entity other);

 } // Entity class