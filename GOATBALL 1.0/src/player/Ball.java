package player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

import main.Sprite;
import main.SpriteStore;
import player.Player;
import player.Player1;
import player.Player2;


public class Ball extends Player implements Runnable {

	//global variables
	int x, y, xDirection, yDirection;
	
	protected Sprite sprite; // this entity's sprite
	int p1score, p2score;

	
	Rectangle ball;

	public Ball(int x, int y, String r){
		p1score = p2score = 0;
		this.x = x;
		this.y = y;
		sprite = (SpriteStore.get()).getSprite(r);
		//create "ball"
		ball = new Rectangle(this.x, this.y, 15, 15);
	}
	
	 public void setCoords(int x, int y) {
    	 ball.x = x;
    	 ball.y = y;
     } // setCoords
	 
	public void setXDirection(int xDir){
		xDirection = xDir;
	}
	public void setYDirection(int yDir){
		yDirection = yDir;
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		sprite.draw(g,ball.x, ball.y);
	}
	
	public void move() {
		ball.x += xDirection;
		ball.y += yDirection;
		//bounce the ball when it hits the edge of the screen
		if (ball.x <= 0) {
			setCoords(1000,500);
			setXDirection(0);
			setYDirection(0);
			p2score++;
			
	}
		if (ball.x >= 2000) {
			setCoords(1000,500);
			setXDirection(0);
			setYDirection(0);
			p1score++;
		}
		
		if (ball.y <= 10) {
			setYDirection(yDirection * -1);
		}
		
		if (ball.y >= 1000) {
			setYDirection(yDirection * -1);
		}
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				move();
				Thread.sleep(8);
			}
		}catch(Exception e) { System.err.println(e.getMessage()); }

	}

	@Override
	public void collidedWith(Player other) {
		// TODO Auto-generated method stub
		
	}

}



/*
 * package player;
 * 
 * import java.awt.Color; import java.awt.Graphics; import java.awt.Rectangle;
 * import java.util.Random;
 * 
 * import main.Game; import main.Sprite; import main.SpriteStore; import
 * player.Player1; import player.Player2;
 * 
 * 
 * public class Ball extends Player{ private Game game; public Ball(Game g,
 * String r, int newX, int newY) { super(r, newX, newY); game = g; } //
 * constructor
 * 
 * public void setHorizontalMovement(double newDX) { dx = newDX; } //
 * setHorizontalMovement
 * 
 * public void setVerticalMovement(double newDY) { dy = newDY; } //
 * setVerticalMovement
 * 
 * public void collidedWith(Player other) { // TODO Auto-generated method stub
 * 
 * } }
 */
