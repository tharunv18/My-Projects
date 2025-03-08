/***********************************************************
Program name: ChangeOneLetter - dialog box version
Author: Gen Kiffiak, Tharun Venkatesh 
Date: 5/8/2022
Purpose: A game played by 2 players or a player plays against a computer, where a word is 
changed 1 letter at a time with commands until
it is changed into the goal word.
************************************************************/
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import java.awt.Color;

public class ChangeOneLetter {

	public static String[] dictionary;
	static Object[] options2 = { "Useable Words", "Previous Words", "Continue With Game", "Quit Game" }; // 
	static Object[] playAgain = { "Yes-Play Version Again", "No-Exit Game", "Yes-back to Main Menu" }; // option to replay the game
	static Object[] options = { "Instructions", "1 Player", "2 Player", "Quit Game" }; // main menu

	public static void main(String[] args) {
		dictionary = getFileContents("dictionary.txt"); // contains most 4 letter words
		introScreen();	
	} // main

	// explanation on how to play the game
	public static void introScreen() {
		String playerOneName = ""; 
		String playerTwoName = "";
		String intro = "<font color = #000000> <fontface = Arial> Hello there! Welcome to the <b> " 
				+ "Change One Letter Game </b> ! <br> In this game, one player chooses the start "
				+ "word and the other/CPU chooses a goalword. <br> In each turn one can modify one "
				+ "letter from the previous word as they try to reach the goal word. "
				+ "<font color = #008000> Have fun!";
		
		String getPlayerOneName = "<b> <font color = #ff0000>  Player 1 </b> , please enter your name!";
		String getPlayerTwoName = "<b> <font color = #335EFF>  Player 2 </b> , please enter your name!";
		
		JEditorPane outputScreen = new JEditorPane();
		outputScreen.setContentType("text/html");
		outputScreen.setBackground(new Color(238, 238, 238));
		outputScreen.setEditable(false);

		do {
			// main menu
			int choice = JOptionPane.showOptionDialog(null, "Please select one of the below options:",
					"Welcome to Change One Letter", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
					options, options[0]);
			if (choice == 0) {
				outputScreen.setText(intro);
				JOptionPane.showMessageDialog(null, outputScreen);

				continue;
			} else if (choice == 1) { // Single Player
				do {
					// get user one's name (Single Player)
					outputScreen.setText(getPlayerOneName);
					playerOneName = JOptionPane.showInputDialog(null, outputScreen);
					if (playerOneName == null) { // lets user exit
						System.exit(0);
					} // if
					break;
				} while (true);
				onePlayerGame(playerOneName);
			} else if (choice == 2) { // Two player
				
					
				do { // get user one's name (Two Player)
					outputScreen.setText(getPlayerOneName);
					playerOneName = JOptionPane.showInputDialog(null, outputScreen);
					if (playerOneName == null) { // lets user exit
						System.exit(0);
					}// if
					break;
				} while (true);

				do { //  get user two's name (Two Player)
					outputScreen.setText(getPlayerTwoName);
					playerTwoName = JOptionPane.showInputDialog(null, outputScreen);
					if (playerTwoName == null) { // lets user exit
						System.exit(0);
					} // if
					break;
				} while (true);

				twoPlayerGame(playerTwoName, playerOneName);
			} else {
				System.exit(0);
			} // else
			break;
		} while (true);
	} // introScreen

	// A version of the game where two players face each other
	public static void twoPlayerGame(String playerTwoName, String playerOneName) {
		
		JEditorPane outputScreen = new JEditorPane();
		outputScreen.setContentType("text/html");
		outputScreen.setEditable(false);
		
		String startWord = "";
		String input = ""; // user input
		String goalWord = "";
		String currentWord = ""; // updated word after changes
		String error = ""; // error message
		String currentPlayer = ""; // who is playing
		String userCommand = ""; // user changes
		int turn = 0; // tracks turns
		
		String startWordCommand = "<b> <font color = #BB33FF> <font face = Arial>" + 
		playerOneName + "</b>" + "\n, please enter a <b> <font color = #008000> "
				+ "4 letter English word </b> to start off the game!";
		
		JEditorPane outputScreen1 = new JEditorPane();
		outputScreen1.setContentType("text/html");
		outputScreen1.setBackground(new Color(238, 238, 238));
		outputScreen1.setEditable(false);
		
		JEditorPane outputScreen2 = new JEditorPane();
		outputScreen2.setContentType("text/html");
		outputScreen2.setBackground(new Color(238, 238, 238));
		outputScreen2.setEditable(false);
		
		// Get Start Word
		do { 
			do { 
				turn = 0;
				outputScreen1.setText(error + startWordCommand);
				startWord = JOptionPane.showInputDialog(null, outputScreen1);
				
				// validate the start word
				if (startWord == null) {
					System.exit(0);
				} else if (startWord.length() == 0) {
					error = "<b> <font color = #ff0000>" + " You have to enter a word." + "</b>";
					continue;
				} else if (startWord.length() != 4) {
					error = "<b> <font color = #ff0000>" + startWord + " is not a 4 letter word. " + "</b>";
					continue;
				} else if (isInDictionary(startWord) == false) {
					error = "<b> <font color = #ff0000>" + startWord + " is not a word in the dictionary. " + "</b>";
					continue;
				} else {
					currentWord = startWord;
				} // else
				break;
			} while (true);

			error = "";

			// goal word input
			String goalWordCommand =  "<b> <font color = #335EFF> <font face = Arial>" + playerTwoName + "</b>" + 
									"\n, please enter a <b> <font color = #008000> 4 letter English word </b> "
									+ "to set the goal word! The start word is: " +  "<font color = #FF00FF>" + startWord;
			
			do { // get the goal word from player 2
				outputScreen2.setText(error + goalWordCommand);
				input = JOptionPane.showInputDialog(null, outputScreen2);
				if (input == null) { // lets user exit
					System.exit(0);
				} // if

				// changes the user input to lowercase
				goalWord = input.toLowerCase();

				// validate goal word length
				if (goalWord.length() > 4) {
					error = "<b> <font color = #ff0000>" + "The word you entered was more than 4 letters!" + "</b>";
					continue;
				} // if
				if (goalWord.length() < 4) {
					error = "<b> <font color = #ff0000>" + " The word you entered was less than 4 letters!" + "</b>";
					continue;
				} // if

				// checks if the goal word is equal to start word
				if (goalWord.equals(startWord)) {
					error = "<b> <font color = #ff0000>" + " The goal word and start word can't be the same. " + "</b>";
					continue;
				} // if

				// checks if its a real word
				if (!(isInDictionary(goalWord))) {
					error = "<b> <font color = #ff0000>" + goalWord + " is not in the dictionary!" + "</b>";
					continue;
				} // if
				break;
			} while (true); // goal word input

			error = "";

			currentWord = startWord;

			// execute each turn
			do {
				turn++;
				if (turn % 2 == 0) {
					currentPlayer = playerTwoName;
				} else {
					currentPlayer = playerOneName;
				} // else
				
				// get user command 
				do {
					String getPlayerCommand = "<b>" + currentPlayer + "</b>"  + error
							+ ", please enter a 4 letter word that is <b> <font color = #ff0000> one letter different </b> from the <b>current word. </b> "
							+ " \n The current word is: " + " <b> <font color = #335EFF> " + currentWord + "</b>" + " and the goal word is: " + " <b> <font color = #FF00FF> " + goalWord + "</b>";
					outputScreen2.setText(getPlayerCommand);
					input = JOptionPane.showInputDialog(null, outputScreen2);
					
					if (input == null) { // lets user exit
						System.exit(0);
					} // if

					// changes the command to lowercase

					userCommand = input.toLowerCase();

					// checks if the command length is three
					if (userCommand.length() != 4) {
						error = ", the new word must be a total of 4 characters.\n";
						continue;
					} // if
					if (isOneCharDif(userCommand, currentWord)) {
						currentWord = userCommand;
					} else {
						error = "<b> <font color = #ff0000>" +userCommand + " is not one letter different from " + currentWord + " please try again" + "</b>";
						continue;
					} // else
					break;
				} while (true); // error check step

				// checks if its a real word
				if (!(isInDictionary(userCommand))) {
					error = "<b> <font color = #ff0000>" + "ERROR: " + userCommand + " is not in the dictionary!" + "</b>";
					continue;
				} // if

				error = "";

			} while (!currentWord.equals(goalWord)); // execute each turn

			// winner output
			JOptionPane.showMessageDialog(null, currentPlayer + " won in: " + turn + " turns. ");
			
			// ask user to replay the game
			int playAgain2 = JOptionPane.showOptionDialog(null, "Would you like to play this version again? ",
					"Play Again?", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, playAgain,
					playAgain[0]);
			if (playAgain2 == 0) { // Yes-Play Version Again 
				continue;
			} // if
			if (playAgain2 == 1) { // No-Exit Game
				System.exit(0);
			} // if
			if (playAgain2 == 2) { // Yes-back to Main Menu
				introScreen();
			} // if
			break;
		} while (true);

	} // two player game

	// A version of the game where a player goes against a computer
	public static void onePlayerGame(String playerOneName) {

		JEditorPane outputScreen = new JEditorPane();
		outputScreen.setContentType("text/html");
		outputScreen.setEditable(false);

		dictionary = getFileContents("dictionary.txt"); // contains most 4 letter words
		String startWord = "";
		String error = "";
		String goalWord = "";
		String temp = ""; // randomly chosen word by computer 
		int turns = 0; // tracks turn
		String currentWord = ""; // updated word
		String userCommand = ""; // user input to change current word
		String[] useableWords = new String[1000]; // one character different words from current word
		String previousWords = ""; // tracks words used to play the game
		String startWordCommand = "<b> <font color = #ff0000> <font face = Arial>" + playerOneName + 
				"</b>" + "\n, please enter a <b> <font color = #008000> 4 letter English word </b> "
						+ "to start off the game!";
		
		Object[] goal = { "Start Game", "New Goal Word", "Exit" }; 
		JEditorPane outputScreen2 = new JEditorPane();
		outputScreen2.setContentType("text/html");
		outputScreen2.setBackground(new Color(238, 238, 238));
		outputScreen2.setEditable(false);
		
		do { // get start word
			outputScreen2.setText(error + startWordCommand);
			startWord = JOptionPane.showInputDialog(null, outputScreen2);
	
			if (startWord == null) { // lets user exit
				System.exit(0);
			} // if
			
			// changes the user input to lowercase
			startWord = startWord.toLowerCase();
			
			// validate the start word
			 if (startWord.length() == 0) {
				error = "<b>  <font color = #ff0000>" +" You have to enter a word. "+ "</b>" ;
				continue;
			} else if (startWord.length() != 4) {
				error = "<b>  <font color = #ff0000>" + startWord + " is not a 4 letter word. "+ "</b>";
				continue;
			} else if (isInDictionary(startWord) == false) {
				error = "<b>  <font color = #ff0000>" + startWord + " is not a word in the dictionary. "+ "</b>";
				continue;
			} else {
	
				currentWord = startWord;
			} // else

		
			// computer choose goal word
			do {
				do {
				   goalWord = dictionary[(int)(Math.random()*dictionary.length)];
					String getGoalWord = 	"The " + "<b> <font color = #335EFF> " + "computer " + "</b>" + "chose: " + "<b> <font color = #BB33FF>" + goalWord + "</b>" + " as the goal word. The start word is: " + "<b> <font color = #335EFF>" + startWord + "</b";
					if (isOneCharDif(startWord, goalWord) == true) {
						continue;
					} else {
						outputScreen2.setText( getGoalWord);
						int choice = JOptionPane.showOptionDialog(null,
								outputScreen2,"Please select one of the below options:", JOptionPane.DEFAULT_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, goal, goal[0]);
						if (choice == 0) { // start game
							break;
						} else if (choice == 1) { // new goal word
							continue;
						} else if (choice == 2) { // lets user exit
							System.exit(0);
						} // else if
						break;
					} // else
				} while (true);
				break;
			} while (true);
			error = "";

			// repeat each turn
			do {
				
				// player turn
				do {
					String getUserCommand = "<b>" + playerOneName + "</b>" +
							 ", please enter a 4 letter word that is <b> <font color = #ff0000> one letter different </b> from the <b>current word. </b> "
							 + " \n The current word is: " + " <b> <font color = #335EFF> " + currentWord + "</b>" + " and the goal word is: " + " <b> <font color = #FF00FF> " + goalWord + "</b>";
						
					useableWords = whichUseableWords(currentWord, useableWords);
					 int choice = JOptionPane.showOptionDialog(null, error + playerOneName +
					  ", please select one of the options below!" ,
					  "Welcome to Change One Letter", JOptionPane.DEFAULT_OPTION,
					 JOptionPane.QUESTION_MESSAGE, null, options2, options2[2]);
					 if (choice == 0) { // shows a list of useable words
						 JOptionPane.showMessageDialog(null, useableWords);
						 continue;
					 } else if (choice == 1) { // shows a list of previously used words
						 JOptionPane.showMessageDialog(null, previousWords);
						 continue;
					 } else if (choice == 2) { // continue with game
						 error = "";
						 outputScreen2.setText(error + getUserCommand);
						userCommand = JOptionPane.showInputDialog(null, outputScreen2);
					 } else if (choice == 3) { // lets user exit
						 System.exit(0);
					 } else {
						 System.exit(0);
					 }
					
					
					error = "";
					
					if (userCommand == null) { // lets user exit
						System.exit(0);
						
					// validate user command
					} else if (isInDictionary(userCommand) == true && isOneCharDif(userCommand, currentWord)) {
							turns++;
							currentWord = userCommand;
							previousWords += " " + userCommand;
							break;
					} else if (userCommand.length() != 4) {
							error =  "<b>  <font color = #ff0000>" + " The word must be 4 characters long. " + "</b> ";
							continue;
					} else if (userCommand.length() == 0) {
							error = "<b>  <font color = #ff0000>" +" You have to enter a word. " + "</b> ";
							continue;
					} else if (!isInDictionary(userCommand)){
							error = "<b>  <font color = #ff0000> " + userCommand + " is not a valid word in the dictionary! Please look at Useable Words for potential moves! " + "</b> ";
							continue;
					} else if (!isOneCharDif(userCommand, currentWord)) {
							error = "<b>  <font color = #ff0000> " + userCommand + " is not one char different from the current word. " + "</b> ";
					} // else if
				} while (true);
				if (currentWord.equals(goalWord)) {
					break;
				} // if
					// computer turn
				do {
					temp = dictionary[(int) (Math.random() * dictionary.length)];
					if (!isOneCharDif(temp, currentWord)) {
						continue;
					} else if (temp.equals(currentWord)){
						continue;
				} else {
					break;
					}
				} while (true);
				for (int i = 0; i < dictionary.length; i++) {
					
					if (dictionary[i].equals(currentWord)) {
						continue;
					} // if
					
					// checks if current word is two character from goal word
					if (isTwoCharDif(currentWord, goalWord)) {
						currentWord = temp;
						if (isOneCharDif(dictionary[i], currentWord)) {
							currentWord = temp;
							if (isOneCharDif(dictionary[i], goalWord)) {
								currentWord = dictionary[i];
								JOptionPane.showMessageDialog(null,
										"The computer chooses: " + currentWord + " as the next word");
								previousWords +=  " " + currentWord;
								turns++;
								break;
							} // inner if
						} // middle if

						// if computer can win, it will win
					} else if (isOneCharDif(currentWord, goalWord)) {
						JOptionPane.showMessageDialog(null,
								"The computer chooses: " + goalWord + " as the next word");
						currentWord = goalWord;
						turns++;
					
						break;
						
						// checks if current word is three character from goal word
					} else if (isThreeCharDif(currentWord, goalWord)) { 
						currentWord = temp;
							if (isTwoCharDif (dictionary[i], goalWord)) { 
								currentWord = temp;
								if (isOneCharDif (currentWord, dictionary[i])) {
						currentWord = dictionary[i];
						JOptionPane.showMessageDialog(null,
								"The computer chooses: " + currentWord + " as the next word");
						previousWords += " " + currentWord;
						turns++;
					
						break;
								} // inner if
							} // outer if
							
						// checks if current word is four character from goal word
					} else if (isFourCharDif(currentWord, goalWord)) {
						currentWord = temp;
						if (isThreeCharDif (dictionary[i], goalWord)) { 
							currentWord = temp;
							if (isOneCharDif (currentWord, dictionary[i])) {
					currentWord = dictionary[i];
					JOptionPane.showMessageDialog(null,
							"The computer chooses: " + currentWord + " as the next word");
					previousWords += " " + currentWord;
					turns++;
					break;
							} // inner if
						} // outer if
					} // else if
					do {
						temp = dictionary[(int) (Math.random() * dictionary.length)];
						if (!isOneCharDif(temp, currentWord)) {
							continue;
						} else if (temp.equals(currentWord)) {
							continue;
						} else {
							break;
						}
					} while (true);
					
					// displays what the computer chose as the new current word
					JOptionPane.showMessageDialog(null,
							"The computer chooses: " + currentWord + " as the next word");
					previousWords += " " + currentWord;
					turns++;
						break;
				} // for
			} while (currentWord.equals(goalWord) == false);
			
			if (turns % 2 != 0) {
				JOptionPane.showMessageDialog(null, playerOneName + " won in: " + turns + " turns");
			} else {
				JOptionPane.showMessageDialog(null, "The CPU won in: " + turns + " turns");
			} // else
			
			int playAgain2 = JOptionPane.showOptionDialog(null, "Would you like to play again? ",
					"Please select one of the below options:", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, playAgain, playAgain[0]);
			if (playAgain2 == 0) { // play same version again
				continue;
			} // if
			if (playAgain2 == 1) { // lets user exit
				System.exit(0);
			} // if
			if (playAgain2 == 2) { // goes to main menu
				introScreen();
			} // if
		} while (true);

	} // onePlayerGame
	
	// gets words that is one character different for the user to use
	public static String[] whichUseableWords(String currentWord, String[] useableWords) {
		int j = 0;
		for (int i = 0; i < dictionary.length; i++) {
			if (isOneCharDif(dictionary[i], currentWord)) {
					for (; j < useableWords.length;) {
						j++;
					useableWords[j] = dictionary[i];
					break;
				} // inner for
			} // if
			continue;
		} // outer for
		return useableWords;
	} // whichUseableWords
	
	// checks if word is one character different from current word
	public static boolean isOneCharDif(String m, String j) {

		if (m.charAt(0) == j.charAt(0) && m.charAt(1) == j.charAt(1) && m.charAt(2) == j.charAt(2) && m.charAt(3) != j.charAt(3)) {
			return true;
		} else if (m.charAt(0) == j.charAt(0) && m.charAt(1) == j.charAt(1) && m.charAt(3) == j.charAt(3) && m.charAt(2) != j.charAt(2)) {
			return true;
		} else if (m.charAt(1) == j.charAt(1) && m.charAt(2) == j.charAt(2) && m.charAt(3) == j.charAt(3) && m.charAt(0) != j.charAt(0)) {
			return true;
		} else if (m.charAt(0) == j.charAt(0) && m.charAt(2) == j.charAt(2) && m.charAt(3) == j.charAt(3) && m.charAt(1) != j.charAt(1)) {
			return true;
		} else {
			return false;
		} // else
	} // isOneCharDif
	
	// checks if word is two character different from current word
	public static boolean isTwoCharDif(String m, String j) {

		if (m.charAt(0) == j.charAt(0) && m.charAt(1) == j.charAt(1) && m.charAt(2) != j.charAt(2)
				&& m.charAt(3) != j.charAt(3)) {
			return true;
		} else if (m.charAt(0) == j.charAt(0) && m.charAt(3) == j.charAt(3) && m.charAt(1) != j.charAt(1)
				&& m.charAt(2) != j.charAt(2)) {
			return true;
		} else if (m.charAt(2) == j.charAt(2) && m.charAt(3) == j.charAt(3) && m.charAt(0) != j.charAt(0)
				&& m.charAt(1) != j.charAt(1)) {
			return true;
		} else if (m.charAt(1) == j.charAt(1) && m.charAt(2) == j.charAt(2) && m.charAt(0) != j.charAt(0)
				&& m.charAt(3) != j.charAt(3)) {
			return true;
		} else if (m.charAt(1) == j.charAt(1) && m.charAt(3) == j.charAt(3) && m.charAt(0) != j.charAt(0)
				&& m.charAt(2) != j.charAt(2)) {
			return true;
		} else if (m.charAt(0) == j.charAt(0) && m.charAt(2) == j.charAt(2) && m.charAt(0) != j.charAt(0)
				&& m.charAt(2) != j.charAt(2)) {
			return true;
		} else {
			return false;
		} // else
	} // istwoCharDif

	// checks if word is three character different from current word
	public static boolean isThreeCharDif(String m, String j) {
		
		if (m.charAt(0) == j.charAt(0)
				&& (m.charAt(1) != j.charAt(1) && m.charAt(2) != j.charAt(2) && m.charAt(3) != j.charAt(3))) {
			return true;
		} else if (m.charAt(1) == j.charAt(1) && m.charAt(3) != j.charAt(3) && m.charAt(1) != j.charAt(1)
				&& m.charAt(2) != j.charAt(2)) {
			return true;
		} else if (m.charAt(2) == j.charAt(2) && m.charAt(3) != j.charAt(3) && m.charAt(0) != j.charAt(0)
				&& m.charAt(1) != j.charAt(1)) {
			return true;
		} else if (m.charAt(3) == j.charAt(3) && m.charAt(0) != j.charAt(0) && m.charAt(1) != j.charAt(1)
				&& m.charAt(2) != j.charAt(2)) {
			return true;
		} else {
			return false;
		}
	} // iCharDif
	
	// checks if word is four character different from current word
	public static boolean isFourCharDif(String m, String j) {
		// wall & mail
		if (m.charAt(0) != j.charAt(0)
				&& (m.charAt(1) != j.charAt(1) && m.charAt(2) != j.charAt(2) && m.charAt(3) != j.charAt(3))) {
			return true;
		}  else {
			return false;
		}
	} // iCharFourDif

	// goes through 4 letter string array
	public static boolean isInDictionary(String m) {
		for (int i = 0; i < dictionary.length; i++) {
			if (dictionary[i].equals(m)) {
				return true;
			} // if
		} // for
		return false;
	} // isInDictionary

	// extracts the words in the file
	public static String[] getFileContents(String fileName) {

		String[] contents = null;
		int length = 0;
		try {

			// input
			String folderName = ""; // if the file is contained in the same folder as the .class file, make
									// this equal to the empty string
			String resource = fileName;

			// this is the path within the jar file
			InputStream input = ChangeOneLetter.class.getResourceAsStream(folderName + resource);
			if (input == null) {
				// this is how we load file within editor (eg eclipse)
				input = ChangeOneLetter.class.getClassLoader().getResourceAsStream(resource);
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(input));

			in.mark(Short.MAX_VALUE);

			// count number of lines in file
			while (in.readLine() != null) {
				length++;
			}

			in.reset(); // rewind the reader to the start of file
			contents = new String[length]; // give size to contents array

			// read in contents of file and print to screen
			for (int i = 0; i < length; i++) {
				contents[i] = in.readLine();
			}
			in.close();
		} catch (Exception e) {
			System.out.println("dictionary error");
		}
		return contents;
	} // getFileContents

} // ChangeOneLetter



 




