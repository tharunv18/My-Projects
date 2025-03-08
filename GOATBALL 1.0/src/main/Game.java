package main;
import javax.swing.*;

import player.Player1;
import player.Ball;
import player.Goal;
import player.Player;
import player.Player2;

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
        private boolean upPressed = false; // true if up arrow key currently pressed
        private boolean downPressed = false; // true if down arrow key currently pressed
        private boolean leftPressed2 = false;  // true if left arrow key currently pressed
        private boolean rightPressed2 = false; // true if right arrow key currently pressed
        private boolean upPressed2 = false; // true if up arrow key currently pressed
        private boolean downPressed2 = false; // true if down arrow key currently pressed
        private boolean wantToPlay = false; // true if user presses "Enter" key
        private boolean justWon = false; // user wins
        public int level = 1; // current level
        private boolean showInstructions = true; // game instructions
        
        private boolean gameRunning = true;
        private ArrayList<Player> players = new ArrayList<Player>(); // list of entities in game
        private Player ship; // the monkey
        private Player ship2;
        private Ball ball ; // the monkey
        private Goal goal1;
        private Goal goal2;
        private double moveSpeed = 400;
        private double ballSpeed = 600;// hor. vel. of ship (px/s)
        private int bananaScore = 0; // number of bananas collected
        private String message = ""; // prints instructions
        private String message2  = ""; // prints death message
        private String message3  = ""; // prints victory message

        private boolean logicRequiredThisLoop = false; // true if logic
        //Entity title = new TitleEntity(this, "sprites/title.png"); // title image  
        //Entity instructions = new TitleEntity(this, "sprites/instructions.png"); // instructions image
        
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
    
    	/*
    	// displays game instructions on home screen
    	 public void showGameInstructions() {
     		instructions.setCoords(550, 300);
     		entities.add(instructions);
     		 if (!waitingForKeyPress) {
     			 entities.remove(instructions);
     		 } // if
     	} // showGameInstructions
    	 */
         // initializes monkey
    	private void initEntities() {
             
    		 // create the ship and put in center of screen
    		ship = new Player1(this, "sprites/LeoMessi.png", 200, 500);
    		ship2 = new Player2(this, "sprites/neymarjr.png", 1500, 500);
    		ball = new Ball(850, 500, "sprites/ball.png");
    		players.add(ship2);
    		players.add(ship);
    		players.add(ball);
    		
    		//loadTitleScreen();  
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
        
    	/* public void loadTitleScreen() {
    		entities.add(title);
    	    if (wantToPlay) {
    	    	entities.remove(title);
                entities.add(ship);
                entities.add(ship2);
            } // if
    	} // loadTitleScreen
    	*/
    	
        /* Notification from a game entity that the logic of the game
         * should be run at the next opportunity 
         */
         public void updateLogic() { 
           logicRequiredThisLoop = true;
         } // updateLogic

         /* Remove an entity from the game.  It will no longer be
          * moved or drawn.
          */
         
         // Notification that the play has killed all zookeeper
         public void notifyWin(){
           message2 = "Congratulations! You are now free in the jungle! Want to play again?";
           justWon = true;
           wantToPlay = false;
           waitingForKeyPress = true;
           level = 1;
           bananaScore = 0;
         } // notifyWin
         
       
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
          
          
          // keep loop running until game ends
          while (gameRunning) {
        	  
        	  // calculate time since last update, will be used to calculate entities movement
        	  long delta = System.currentTimeMillis() - lastLoopTime;
        	  lastLoopTime = System.currentTimeMillis();

        	  // get graphics context for the accelerated surface and make it black
        	  Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
        	  g.setColor(new Color(0,128,0));            
        	  g.fillRect(0,0,2000,2000);
     
      

            // draw all entities
            for (int i = 0; i < players.size(); i++) {
               Player entity = (Player) players.get(i);
               try {
            	   entity.draw(g);            	   
               } catch (Exception e) {
            	   //System.out.println("Null entity, technical error, please try again."); 
            	   //System.exit(0);
               } // try-catch
            } // for
           
            for(int i = 0; i < players.size(); i++) {
            	if(players.get(i) instanceof Player1 || players.get(i) instanceof Player2) {
            		players.get(i).move(delta);
            		players.get(i).draw(g);
            	} // if
            } // for
            
            
			/*
			 * String kickDirection = "";
			 * 
			 * if (ship.collidesWith(ball)) { if (leftPressed) { kickDirection = "left"; }
			 * if (rightPressed) { kickDirection = "right"; }
			 * ball.setHorizontalMovement(ship.getHorizontalMovement());
			 * ball.setVerticalMovement(ship.getVerticalMovement());
			 * ball.move(kickDirection, ballSpeed); ball.draw(g); }
			 * 
			 * if (ship2.collidesWith(ball)) { if (leftPressed2) { kickDirection = "left"; }
			 * if (rightPressed2) { kickDirection = "right"; }
			 * ball.setHorizontalMovement(ship2.getHorizontalMovement()*-1);
			 * ball.setVerticalMovement(ship2.getVerticalMovement()*-1);
			 * ball.move(kickDirection, ballSpeed); ball.draw(g); }
			 */
            // checks for collisions between entities
           for (int i = 0; i < players.size() - 1; i++) {
             for (int j = i + 1; j < players.size(); j++) {
            	 Player me = (Player)players.get(i);
                Player him = null;
                try {
                	him = (Player)players.get(j);
                } catch (Exception e) {
                	e.printStackTrace();
                	him = new Player1(this, "sprites/LeoMessi.png", 0, 900);
                } // try-catch
                
               if (him != null && me != null) {
	               if (me.collidesWith(him)) {
	                  me.collidedWith(him);
	                  him.collidedWith(me);
	               } // inner if
               } // outer if
             } // inner for
           } // outer for
           
        // checks for collisions between entities
           for (int i = 0; i < players.size() - 1; i++) {
             for (int j = i + 1; j < players.size(); j++) {
            	 Player me = (Player)players.get(i);
                Player him = null;
                try {
                	him = (Player)players.get(j);
                } catch (Exception e) {
                	e.printStackTrace();
                	him = new Player1(this, "sprites/neymarjr.png", 0, 900);
                } // try-catch
                
               if (him != null && me != null) {
	               if (me.collidesWith(him)) {
	                  me.collidedWith(him);
	                  him.collidedWith(me);
	               } // inner if
               } // outer if
             } // inner for
           } // outer for
           
           // font attributes
          	g.setColor(Color.WHITE);
          	g.setFont(new Font("Sans Serif",Font.BOLD, 30));

           // run logic if required
           if (logicRequiredThisLoop) {
             for (int i = 0; i < players.size(); i++) {
            	 Player entity = players.get(i);
               entity.doLogic();
             } // for
             logicRequiredThisLoop = false;
           } // if

           // draws messages to screen
           if (waitingForKeyPress) {
             g.setColor(Color.white);
             g.drawString(message, (1910 - g.getFontMetrics().stringWidth(message))/2, 270);
             g.drawString("Press enter to start or press 'I' to display instructions", (1910 - g.getFontMetrics().stringWidth("Press any key to start or press 'I' to display instructions"))/2, 270);
           
          if (justWon){
        	  players.clear();
            	 //entities.add(new TitleEntity(this, "sprites/winscreen.png"));
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
            
            // ship should not move without user input
            ship2.setHorizontalMovement(0);
            ship2.setVerticalMovement(0);

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
            
            // respond to user moving ship
            if ((leftPressed2) && (!rightPressed2)) {
            	 ship2.setHorizontalMovement(-moveSpeed);
            } else if ((rightPressed2) && (!leftPressed2)) {
            	 ship2.setHorizontalMovement(moveSpeed);
            } else if(upPressed2 && (!downPressed2)) {
                 ship2.setVerticalMovement(-moveSpeed);
            } else if(downPressed2 && (!upPressed2)) {
            	 ship2.setVerticalMovement(moveSpeed);
            } // if-else if
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
        	 players.clear();
            
            initEntities();
            
            // blank out any keyboard settings that might exist
            leftPressed = false;
            rightPressed = false;
            upPressed = false;
            downPressed = false;
            
            // blank out any keyboard settings that might exist
            leftPressed2 = false;
            rightPressed2 = false;
            upPressed2 = false;
            downPressed2 = false;
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
                upPressed = false;
                downPressed = false;
                leftPressed2 = false;
                rightPressed2 = false;
                upPressed2 = false;
                downPressed2 = false;
				bananaScore = 0;
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
          
       // respond to move left, right, up, down, shield or fire
       			if (e.getKeyCode() == KeyEvent.VK_A) {
       				leftPressed2 = true;
       			} // if

       			if (e.getKeyCode() == KeyEvent.VK_D) {
       				rightPressed2 = true;
       			} // if
                 
       			if(e.getKeyCode() == KeyEvent.VK_W) {
       				upPressed2 = true;
       			} // if
                 
       			if(e.getKeyCode() == KeyEvent.VK_S) {
       				downPressed2 = true;
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
			
			// respond to move left, right, up, down, shield or fire
			if (e.getKeyCode() == KeyEvent.VK_A) {
				leftPressed2 = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_D) {
				rightPressed2 = false;
			} // if
          
			if(e.getKeyCode() == KeyEvent.VK_W) {
				upPressed2 = false;
			} // if
          
			if(e.getKeyCode() == KeyEvent.VK_S) {
				downPressed2 = false;
			} // if
                 
		} // keyReleased

 	    public void keyTyped(KeyEvent e) {

            // if waiting for key press to start game
        	if (waitingForKeyPress) {
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

