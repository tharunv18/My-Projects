package main;

/*****************************************************************************
 * Name: Monkey Run!
 * Authors: Ivy, Tharun, Shubham
 * Date: November 21, 2022
 * Purpose: To provide a fun, easy-going game to play in their free time.
 * 			The monkey has to defeat the zookeepers and robots in all four
 * 			levels to escape to the wilderness.
 ******************************************************************************/

/*
 Image sources:
 
  Monkey Sprite: https://commons.wikimedia.org/wiki/File:440-monkey.svg
  Zookeeper (Royalty Free): https://www.shutterstock.com/image-vector/cartoon-zookeeper-looking-angry-1071863819
  Robot: https://freesvg.org/comic-robot
  Net: https://freesvg.org/brown-cartoon-net
  Road (Free Image): https://www.shutterstock.com/image-vector/road-icon-cartoon-illustration-vector-web-1043505334
  Tree: https://www.pixilart.com/art/tree-sprite-370bd4fd7a6bdec
  Jungle (attribution here): Image by <a href="https://www.freepik.com/free-vector/detailed-
  jungle-background_13859419.htm#query=cartoon%20jungle&position=0&from_view=keyword">
  Bullet: https://freesvg.org/cartoon-bullet-1575552260
  Shield: https://pixabay.com/vectors/bricks-wall-red-161235/
 */
import javax.swing.*;

import entity.AlienEntity;
import entity.AlienShotEntity;
import entity.Entity;
import entity.NonLivingEntity;
import entity.ScoreEntity;
import entity.ShieldEntity;
import entity.MonkeyEntity;
import entity.ShotEntity;
import entity.TitleEntity;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


public class Game extends Canvas {

      	private BufferStrategy strategy; // take advantage of accelerated graphics
        private boolean waitingForKeyPress = true;  // true if game held up until a key is pressed
        private boolean leftPressed = false;  // true if left arrow key currently pressed
        private boolean rightPressed = false; // true if right arrow key currently pressed
        private boolean firePressed = false; // true if firing
        private boolean upPressed = false; // true if up arrow key currently pressed
        private boolean downPressed = false; // true if down arrow key currently pressed
        private boolean shieldPressed = false; // true if key "S" is pressed
        private boolean wantToPlay = false; // true if user presses "Enter" key
        private boolean showLevel = false; // displays level once game has started
        private boolean justDied = false; // user loses
        private boolean justWon = false; // user wins
        public int level = 1; // current level
        private int counter = 0; // number of zookeepers in final boss level
        private boolean showInstructions = true; // game instructions
        private boolean IPressed = false; // true if key "I" is pressed
        
        private boolean gameRunning = true;
        private ArrayList<Entity> entities = new ArrayList<Entity>(); // list of entities in game
        private ArrayList<Entity> removeEntities = new ArrayList<Entity>(); // list of entities to remove this loop
        private ArrayList<Entity> alienEntities = new ArrayList<Entity>(); // number of zookeepers
        private Entity ship; // the monkey
        private double moveSpeed = 600; // hor. vel. of ship (px/s)
        private long lastFire = 0; // time last shot fired
        private long alienLastFire = 0; // time last shot fired
        private long lastShield = 0; // activation of shield
        private long firingInterval = 300; // firing interval for monkey
        private long shieldInterval = 500; // shield interval for monkey
        private long alienFiringInterval = 200; // firing interval for zookeepers
        private int alienCount = 7; // # of aliens left on screen
        private int bananaScore = 0; // number of bananas collected
        
        private String message = ""; // prints instructions
        private String message2  = ""; // prints death message
        private String message3  = ""; // prints victory message

        private boolean logicRequiredThisLoop = false; // true if logic
        Entity title = new TitleEntity(this, "sprites/title.png"); // title image  
        Entity instructions = new TitleEntity(this, "sprites/instructions.png"); // instructions image
        
        // health bar images for zookeepers in final boss level
    	public Entity heart1 = new NonLivingEntity(this, "sprites/fullhealthbar.png", 0, 600);
    	public Entity heart2 = new NonLivingEntity(this, "sprites/fullhealthbar.png", 400, 600);
    	public Entity heart3 = new NonLivingEntity(this, "sprites/fullhealthbar.png", 800, 600);
    	public Entity heart4 = new NonLivingEntity(this, "sprites/fullhealthbar.png", 1200, 600);
    	public Entity heart5 = new NonLivingEntity(this, "sprites/fullhealthbar.png", 1600, 600);
        
    	// Construct our game and set it running.
    	public Game() {
    		// create a frame to contain game
    		JFrame container = new JFrame("Monkey Run");
    
    		// get hold the content of the frame
    		JPanel panel = (JPanel) container.getContentPane();
    
    		// set up the resolution of the game
    		panel.setPreferredSize(new Dimension(2000,2000));
    		panel.setLayout(null);
    
    		// set up canvas size (this) and add to frame
    		setBounds(0,0,2000,2000);
    		panel.add(this);    		
    
    		// Tell AWT not to bother repainting canvas since that will
            // be done using graphics acceleration
    		setIgnoreRepaint(true);
    
    		// make the window visible
    		container.pack();
    		container.setResizable(false);
    		container.setVisible(true);    
    
            // if user closes window, shutdown game and jre
    		container.addWindowListener(new WindowAdapter() {
    			public void windowClosing(WindowEvent e) {
    				System.exit(0);
    			} // windowClosing
    		});
    
    		// add key listener to this canvas
    		addKeyListener(new KeyInputHandler());
    
    		// request focus so key events are handled by this canvas
    		requestFocus();

    		// create buffer strategy to take advantage of accelerated graphics
    		createBufferStrategy(2);
    		strategy = getBufferStrategy();
    
    		// initialize entities
    		initEntities();
    
    		// start the game
    		gameLoop();
        } // constructor
    
    	// displays game instructions on home screen
    	 public void showGameInstructions() {
     		instructions.setCoords(550, 300);
     		entities.add(instructions);
     		 if (!waitingForKeyPress) {
     			 entities.remove(instructions);
     		 } // if
     	} // showGameInstructions
    	 
         // initializes monkey
    	private void initEntities() {
             
    		 // create the ship and put in center of screen
    		ship = new MonkeyEntity(this, "sprites/monkey.png", 200, 500);
    		loadTitleScreen();  
    	} // initEntities
    	    	
    	// plays music from a .wav file
    	public static synchronized void playSound(final String ref) {
    		(new Thread(new Runnable() {
    			public void run() {
    				  try {
    					  Clip clip = AudioSystem.getClip();
    		              URL url = getClass().getClassLoader().getResource("sound/" + ref);
    		              if (url == null) {
    		                System.out.println("Failed to load: " + ref);
    		                System.exit(0);
    		              } // if
    		              AudioInputStream inputStream = 
    		                AudioSystem.getAudioInputStream(url);
    		              clip.open(inputStream);
    		              clip.start();
    		            } catch (Exception e) {
    		              e.printStackTrace();
    		            } // try-catch
    		          } // run
    		        })).start(); // Runnable Thread
    		} // playSound
        
    	public void loadTitleScreen() {
    		entities.add(title);
    	    if (wantToPlay) {
    	    	entities.remove(title);
                entities.add(ship);
            } // if
    	} // loadTitleScreen
    	
    	// creates map for each level
    	public void loadMap(int i) {
    		switch (i) {
	    		case 1:
	    			
	    			// removes title
	        		showLevel = true;
	        		justDied = false;
        			justWon = false;

	    	    	entities.remove(title);
	    	    	ship.setCoords(200, 500);
	    	    	
	    	    	// adds images to ArrayList
	    			entities.add(ship);
	    			for (int row = 0; row < 32; row++) {
	    				for (int col = 0; col < 32; col++) {
		    				if (row >= 2 && row <= 9) {
		    					Entity firstRow = new NonLivingEntity(this, "sprites/realroad.png", (col * 65), row * 65);
		    					entities.add(firstRow);
		    				} // if
	    				
		    				if (row > 9) {
		    					Entity secondRow = new NonLivingEntity(this, "sprites/water.png", (col * 65), row * 65);
		    					try {
		    						entities.add(secondRow);
		    					} catch (Exception e) {
		    						System.out.println("Null entity, technical error, please try again.");
		    						System.exit(0);
		    					} // try-catch
		    				} // if
		    				Entity thirdRow = new NonLivingEntity(this, "sprites/tree.png", (col * 65), row);
		    				entities.add(thirdRow);
	    				} // inner for
	    			} // outer for
	    		
	    			for (int row = 2; row < 9; row++) {
	    				Entity hunter = new AlienEntity(this, "sprites/zookeeper.png", 1800, row * 65);
	    				entities.add(hunter);
	    			} // for
	    			break;
	    			
	    		case 2:
	    			
	    			// removes entities from previous level
	    			entities.clear();
	    			int coord = ship.getY();
	    			entities.remove(ship);
	    			ship = new MonkeyEntity(this, "sprites/monkey.png", 0, coord);
	                entities.add(ship);
	                for(int j = 0; j < entities.size(); j++) {
	                	for(int k = 0; k < entities.size(); k++) {
	                		Entity entity = entities.get(k);
	                		if(entity instanceof NonLivingEntity) {
	                			entities.remove(k);
	                		} // if
	                	} // inner for
	                } // outer for
	                
	                // adds images to ArrayList    			
	    			for(int row = 0; row < 32; row++) {
	    				for(int col = 0; col < 32; col++) {
	    					if(row >= 2 && row <= 9) {
	    						Entity secondRow = new NonLivingEntity(this, "sprites/realroad.png", (col * 65), row * 65);
		    					entities.add(secondRow);
	    					} // if
	    					if (row > 9) {
		    					Entity secondRow = new NonLivingEntity(this, "sprites/grass.png", (col * 65), row * 65);
		    					entities.add(secondRow);
	    					} // if
	    					Entity thirdRow = new NonLivingEntity(this, "sprites/tree.png", (col * 65), row);
	    					entities.add(thirdRow);
	    				} // inner for
	    			} // outer for
	    			
	    			for (int row = 2; row < 9; row++) {
	    				Entity hunter = new AlienEntity(this, "sprites/zookeeper.png", 1800, row * 65);
	    				entities.add(hunter);
	    				hunter = new AlienEntity(this, "sprites/zookeeper.png", 1500, row * 65);
	    				entities.add(hunter);
	    				hunter = new AlienEntity(this, "sprites/zookeeper.png", 1200, row * 65);
	    				entities.add(hunter);
	    			} // for    			
	    			break;
	    			
	    		case 3:
	    			
	    			// removes entities from previous level
	    			entities.clear();
	    			int coord2 = ship.getY();
	    			entities.remove(ship);
	    			ship = new MonkeyEntity(this, "sprites/monkey.png", 0, coord2);
	                entities.add(ship);
	                for(int j = 0; j < entities.size(); j++) {
	                	for(int k = 0; k < entities.size(); k++) {
	                		Entity entity = entities.get(k);
	                		if(entity instanceof NonLivingEntity) {
	                			entities.remove(k);
	                		} // if
	                	} // inner for
	                } // outer for
	                
	                // adds images to ArrayList
	                for(int row = 0; row < 32; row++) {
	    				for(int col = 0; col < 32; col++) {
	    					if(row >= 2 && row <= 9) {
	    						Entity secondRow = new NonLivingEntity(this, "sprites/realroad.png", (col * 65), row * 65);
		    					entities.add(secondRow);
	    					} // if
	    					if (row > 9) {
		    					Entity secondRow = new NonLivingEntity(this, "sprites/water.png", (col * 65), row * 65);
		    					entities.add(secondRow);
	    					} // if
	    					Entity thirdRow = new NonLivingEntity(this, "sprites/tree.png", (col * 65), row);
	    					entities.add(thirdRow);
	    				} // inner for
	    			} // outer for
	                
	                for (int row = 2; row < 9; row++) {
	    				Entity hunter2 = null;
	                	int x = (int)((Math.random() * 1800) + 500);
	                	int y = (int)((Math.random() * 425) + 100);
	                	hunter2 = new AlienEntity(this, "sprites/zookeeper.png", x, y);
	    				entities.add(hunter2);
	    			} // for
	                break;
	                
	    		case 4:
	    			
	    			// removes entities from previous level
	    			entities.clear();
	    			int coord3 = ship.getY();
	    			entities.remove(ship);
	    			ship = new MonkeyEntity(this, "sprites/monkey.png", 0, coord3);
	                entities.add(ship);
	                for(int j = 0; j < entities.size(); j++) {
	                	for(int k = 0; k < entities.size(); k++) {
	                		Entity entity = entities.get(k);
	                		if(entity instanceof NonLivingEntity) {
	                			entities.remove(k);
	                		} // if
	                	} // inner for
	                } // outer for
	                
	                // adds images to ArrayList
	                for(int row = 0; row < 32; row++) {
	    				for(int col = 0; col < 32; col++) {
	    					if(row >= 2 && row <= 9) {
	    						Entity secondRow = new NonLivingEntity(this, "sprites/realroad.png", (col * 65), row * 65);
		    					entities.add(secondRow);
	    					} else {
	    						Entity thirdRow = new NonLivingEntity(this, "sprites/tree.png", (col * 65), row);
	    						entities.add(thirdRow);
	    					} // if-else 
	    				} // inner for
	    			} // outer for
	                
	                for(int row = 2; row <= 6; row++) {
	                	
	                if (counter == 5) {
                		counter = 0;
                	} // if
	                	counter++;
	                	Entity hunter = new AlienEntity(this, "sprites/robot.png", 1800, row * 65, counter);
	                	hunter.setHealth(900);
	                	hunter.setHorizontalMovement(0);
	                	entities.add(hunter);
	                } // for
	                
	                heart1.setImage("sprites/fullhealthbar.png");
                	heart2.setImage("sprites/fullhealthbar.png");
                	heart3.setImage("sprites/fullhealthbar.png");
                	heart4.setImage("sprites/fullhealthbar.png");
                	heart5.setImage("sprites/fullhealthbar.png");

	            	entities.add(heart1);
	            	entities.add(heart2);
	            	entities.add(heart3);
	            	entities.add(heart4);
	            	entities.add(heart5);
	    		} // switch
	    		
	    		// adds random number of bananas at random locations
				int num = (int) (Math.random()*7) + 1;
				for(i = 0; i < num; i++) {
					if(!waitingForKeyPress) {
						int xCor = (int)((Math.random()* 1800) + 10);
						int yCor = (int)((Math.random()* 445) + 100);
						Entity score = new ScoreEntity(this, "sprites/banana.png", xCor, yCor);
						entities.add(score);
					} // outer if
				} // for    		
    	} // loadMap

        /* Notification from a game entity that the logic of the game
         * should be run at the next opportunity 
         */
         public void updateLogic() { 
           logicRequiredThisLoop = true;
         } // updateLogic

         /* Remove an entity from the game.  It will no longer be
          * moved or drawn.
          */
         public void removeEntity(Entity entity) {
           removeEntities.add(entity);
         } // removeEntity

         // Notification that the player has died.
         public void notifyDeath() {
        	 message2 = "So close! Want to try again?";
        	 int x = ship.getX();
             int y = ship.getY();
             entities.remove(ship);
             waitingForKeyPress = true;
             level = 1;
             showLevel = false;
             wantToPlay = false;
             IPressed = false;
             justDied = true;
             bananaScore = 0;
             alienCount = 7;
             entities.add(new MonkeyEntity(this, "sprites/monkey.png", x, y));
             entities.clear();
             loadTitleScreen();
             return;
         } // notifyDeath

         // Notification that the play has killed all zookeeper
         public void notifyWin(){
           message2 = "Congratulations! You are now free in the jungle! Want to play again?";
           justWon = true;
           wantToPlay = false;
           waitingForKeyPress = true;
           IPressed = false;
           level = 1;
           bananaScore = 0;
           alienCount = 7;
         } // notifyWin

         // Notification that a zookeeper has been killed
         public void notifyAlienKilled() {
           alienCount--;
           
           // reassigns the number of aliens once they have all been killed
           if (alienCount == 0 && level < 4) {
        	   level++;
        	   alienCount = level != 2 ? 7 : 21;
        	   
		       // loads next level
			   if(level != 5) {
				   loadMap(level);
			   } // if
           } else if (alienCount == 0 && level == 4) {
        	   alienCount = 5;
        	   notifyWin();
           } // else if
         } // notifyAlienKilled

         // shields user when key 'S' is pressed in final level
         public void shieldUser() {
        	 ShieldEntity shield = new ShieldEntity(this, "sprites/shield.png", ship.getX() + 10, ship.getY() + 45);

             // check that we've waited long enough to fire
             if ((System.currentTimeMillis() - lastShield) < shieldInterval){
               return;
             } // if

             // otherwise adds shield
             lastShield = System.currentTimeMillis();
       	  	 entities.add(shield);
           } // tryToFire           
         
        // allows monkey to fire at zookeeper
        public void tryToFire() {
        	
        	// check that we've waited long enough to fire
        	if ((System.currentTimeMillis() - lastFire) < firingInterval){
        		return;
        	} // if

        	// otherwise adds a shot
        	lastFire = System.currentTimeMillis();

        	ShotEntity shot = new ShotEntity(this, "sprites/coconut.png", ship.getX() + 10, ship.getY() + 45);
        	entities.add(shot);  
        } // tryToFire
        
        // allows zookeeper to fire at monkey
        public void alienTryToFire(Entity other) {
        	
        	 // check that we've waited long enough to fire
            if ((System.currentTimeMillis() - alienLastFire) < alienFiringInterval){
              return;
            } // if

            // otherwise adds a shot
            alienLastFire = System.currentTimeMillis();

      	  	AlienShotEntity shot = new AlienShotEntity(this, "sprites/bullet.png", other.getX() - 50, other.getY() + 45);
      	  	entities.add(shot);
        } // alienTryToFire

        // changes health bar image of each zookeeper in level 4
        public void setHealthBarImage(Entity entity, Entity alien) {
	         if (alien.health == 600) {
	 	 	   	entity.setImage("sprites/halffull.png");
	 	 	 } else if (alien.health == 300) {
	 	 	    entity.setImage("sprites/almostempty.png");
	 	 	 } else {
	 	 	   	entity.setImage("sprites/empty.png");
	 	 	 } // if-else if-else
        } // setHealthBarImage
	/*
	 * gameLoop
         * input: none
         * output: none
         * purpose: Main game loop. Runs throughout game play.
         *          Responsible for the following activities:
	 *           - calculates speed of the game loop to update moves
	 *           - moves the game entities
	 *           - draws the screen contents (entities, text)
	 *           - updates game events
	 *           - checks input
	 */
	public void gameLoop() {
          long lastLoopTime = System.currentTimeMillis(); // number of milliseconds since last loop
          int bulletTime = 0; // bullet intervals
          
          // plays main music
          playSound("music.wav");
          
          // keep loop running until game ends
          while (gameRunning) {
        	  
        	  // calculate time since last update, will be used to calculate entities movement
        	  long delta = System.currentTimeMillis() - lastLoopTime;
        	  lastLoopTime = System.currentTimeMillis();

        	  // get graphics context for the accelerated surface and make it black
        	  Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
        	  g.setColor(new Color(194,178,128));            
        	  g.fillRect(0,0,2000,2000);
 
        	  // bullet intervals        	  
        	  bulletTime = level != 4 ? 40 - (5 * level) : 10;
          
            // randomly selects an alien to fire at the monkey
            if (lastLoopTime % bulletTime == 0 && !waitingForKeyPress) {
            	for(int i = 0; i < entities.size(); i++) {
            		if(entities.get(i) instanceof AlienEntity) {
            			alienEntities.add(entities.get(i));
            		} // if
            	} // for

            	int num = (int)(Math.random() * alienEntities.size());
            	if (alienEntities.size() != 0) {
            		alienTryToFire(alienEntities.get(num));
            	} else {
            		if (level == 4) {
            			notifyWin();
            		} else {
            			loadMap(level);
            		} // inner if-else
            	} // outer if-else
            	
            	alienEntities.clear();
            } // outer if           
            
            // move each entity
            if (!waitingForKeyPress) {
              for (int i = 0; i < entities.size(); i++) {
                Entity entity = entities.get(i);
                try { 
                	entity.move(delta);        
                } catch (Exception e) {
                	System.out.println("Null entity, technical error, please try again.");                	
                	System.exit(0);
                } // try-catch
              } // for
            } // if

            // draw all entities
            for (int i = 0; i < entities.size(); i++) {
               Entity entity = (Entity) entities.get(i);
               try {
            	   entity.draw(g);            	   
               } catch (Exception e) {
            	   System.out.println("Null entity, technical error, please try again."); 
            	   System.exit(0);
               } // try-catch
            } // for
           
            for(int i = 0; i < entities.size(); i++) {
            	if(entities.get(i) instanceof MonkeyEntity) {
            		entities.get(i).move(delta);
            		entities.get(i).draw(g);
            	} // if
            } // for
            
            // checks for collisions between entities
           for (int i = 0; i < entities.size() - 1; i++) {
             for (int j = i + 1; j < entities.size(); j++) {
                Entity me = (Entity)entities.get(i);
                Entity him = null;
                try {
                	him = (Entity)entities.get(j);
                } catch (Exception e) {
                	e.printStackTrace();
                	him = new MonkeyEntity(this, "sprites/monkey.png", 0, 900);
                } // try-catch
                
               if (him != null && me != null) {
	               if (me.collidesWith(him)) {
	                  me.collidedWith(him);
	                  him.collidedWith(me);
	               } // inner if
               } // outer if
             } // inner for
           } // outer for
           
           // increases score for every banana collected
           for(int j = 0; j < entities.size(); j++) {
				Entity entity = entities.get(j);
				if(entity instanceof ScoreEntity) {
					if(ship.collidesWith(entity)) {			
						bananaScore += 100;
					} // inner if
				} // outer if
			} // for
            
           // font attributes
          	g.setColor(Color.WHITE);
          	g.setFont(new Font("Sans Serif",Font.BOLD, 30));
           
          	// shows score and level
           if(showLevel) {
				g.drawString(level < 4 ? "Level: " + level : "Final Boss Level", (2000 - g.getFontMetrics().stringWidth(level < 4 ? "Level: " + level : "Final Boss Level"))/2, 50);	
				g.drawString("Score: " + bananaScore, (3600 - g.getFontMetrics().stringWidth("Score: " + bananaScore))/2, 50);
           }
			
           // remove dead entities
           entities.removeAll(removeEntities);
           removeEntities.clear();

           // run logic if required
           if (logicRequiredThisLoop) {
             for (int i = 0; i < entities.size(); i++) {
               Entity entity = entities.get(i);
               entity.doLogic();
             } // for
             logicRequiredThisLoop = false;
           } // if

           // draws messages to screen
           if (waitingForKeyPress) {
             g.setColor(Color.white);
             g.drawString(message, (1910 - g.getFontMetrics().stringWidth(message))/2, 270);
             g.drawString("Press enter to start or press 'I' to display instructions", (1910 - g.getFontMetrics().stringWidth("Press any key to start or press 'I' to display instructions"))/2, 270);
           
             if (justDied) {
            	 entities.clear();
            	 entities.add(new TitleEntity(this, "sprites/deathscreen.png"));
                 g.setColor(Color.white);
                 g.drawString(message2, (1910 - g.getFontMetrics().stringWidth(message2))/2, 230);
                 g.drawString("So close! Want to try again?", (1910 - g.getFontMetrics().stringWidth("So close! Want to try again?"))/2, 230);
 				 g.drawString("Score: " + bananaScore, (3600 - g.getFontMetrics().stringWidth("Score: " + bananaScore))/2, 50);
             } else if (justWon){
            	 entities.clear();
            	 entities.add(new TitleEntity(this, "sprites/winscreen.png"));
                 g.setColor(Color.white);
                 g.drawString(message3, (1910 - g.getFontMetrics().stringWidth(message3))/2, 230);
                 g.drawString("Congratulations! You are now free in the jungle! Want to play again?", (1910 - g.getFontMetrics().stringWidth("Congratulations! You are now free in the jungle! Want to play again?"))/2, 230);
                 g.drawString("Score: " + bananaScore, (3600 - g.getFontMetrics().stringWidth("Score: " + bananaScore))/2, 50);
             } // if-else if
           } // if           

            // clear graphics and flip buffer
            g.dispose();
            strategy.show();

            // ship should not move without user input
            ship.setHorizontalMovement(0);
            ship.setVerticalMovement(0);

            // respond to user moving ship
            if ((leftPressed) && (!rightPressed)) {
            	 ship.setHorizontalMovement(-moveSpeed);
            } else if ((rightPressed) && (!leftPressed)) {
            	 ship.setHorizontalMovement(moveSpeed);
            } else if(upPressed && (!downPressed)) {
                 ship.setVerticalMovement(-moveSpeed);
            } else if(downPressed && (!upPressed)) {
            	 ship.setVerticalMovement(moveSpeed);
            } // if-else if
           
          // if spacebar pressed, try to fire
            if (firePressed) {
              tryToFire();
            } // if
            
            // enables shield if 'S' key is pressed  
            if (level == 4 && shieldPressed) { shieldUser(); }
            
            // pause
            try { Thread.sleep(4); } catch (Exception e) {}
          } // while

	} // gameLoop

        /* startGame
         * input: none
         * output: none
         * purpose: start a fresh game, clear old data
         */
         private void startGame() {
            // clear out any existing entities and initialize a new set
            entities.clear();
            
            initEntities();
            
            // blank out any keyboard settings that might exist
            leftPressed = false;
            rightPressed = false;
            firePressed = false;
            upPressed = false;
            downPressed = false;
            shieldPressed = false;
            justDied = false;
            justWon = false;
         } // startGame

        /* inner class KeyInputHandler
         * handles keyboard input from the user
         */
	private class KeyInputHandler extends KeyAdapter {
                 
	     private int pressCount = 1;  // the number of key presses since
	                                  // waiting for 'any' key press
	
	    /* The following methods are required
	     * for any class that extends the abstract
	     * class KeyAdapter.  They handle keyPressed,
	     * keyReleased and keyTyped events.
	     */
		public void keyPressed(KeyEvent e) {
			
			// starts game if "Enter" key is pressed
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                wantToPlay = true;
                waitingForKeyPress = false;
                leftPressed = false;
                rightPressed = false;
                firePressed = false;
                upPressed = false;
                downPressed = false;
                shieldPressed = false;
				IPressed = false;
				bananaScore = 0;
                entities.clear();
                loadMap(level);
            } // if
	
          // checks if "I" key is pressed and game has not started
          if (waitingForKeyPress) {
        	  if(e.getKeyCode() == KeyEvent.VK_I) {
  			    justDied = false;
			    justWon = false;
			    showGameInstructions();
			    IPressed = true;
			  
        	} // if              
            return;
          } // if
          
          // respond to move left, right, up, down, shield or fire
          if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = true;
          } // if

          if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = true;
            
          } // if
          
          if(e.getKeyCode() == KeyEvent.VK_UP) {
        	  upPressed = true;
          } // if

          if(e.getKeyCode() == KeyEvent.VK_DOWN) {
        	  downPressed = true;
          } // if
          
          if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            firePressed = true;
          } // if

          if (e.getKeyCode() == KeyEvent.VK_S) {
              shieldPressed = true;
          } // if
		} // keyPressed

		public void keyReleased(KeyEvent e) {
			
			// if waiting for keypress to start game, do nothing
			if (waitingForKeyPress) {
				return;
			} // if
          
			// respond to move left, right, up, down, shield or fire
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = false;
			} // if
          
			if(e.getKeyCode() == KeyEvent.VK_UP) {
				upPressed = false;
			} // if
          
			if(e.getKeyCode() == KeyEvent.VK_DOWN) {
				downPressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_S) {
				shieldPressed = false;
			} // if
                 
		} // keyReleased

 	    public void keyTyped(KeyEvent e) {

            // if waiting for key press to start game
        	if (waitingForKeyPress && !IPressed) {
        		 if (pressCount == 1) {
                   waitingForKeyPress = false;
                   startGame();
                   pressCount = 0;
                 } else {
                   pressCount++;
                 } // else
               } // if waitingForKeyPress

               // if escape is pressed, end game
               if (e.getKeyChar() == 27) {
                 System.exit(0);
               } // if escape pressed
		} // keyTyped

	} // class KeyInputHandler

	/**
	 * Main Program
	 */
	public static void main(String [] args) {
		
        // instantiate this object
		new Game();		
	} // main
} // Game

