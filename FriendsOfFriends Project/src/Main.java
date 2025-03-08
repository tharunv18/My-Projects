/**************************
Name: T. Venkatesh
Date: September 13, 2022
Exercise: Friends Of Friends
Purpose:To determine the shortest path from one person to another through friends of friends as well as how many friends
a person has, how many friends of friends they have and to be able to make or delete friendships
*****************************************************/
import java.util.Scanner;


public class Main     {

	static int friendships [] [] = new int [51][51]; //stores friendships
	static int path[][] = new int [51][51]; //stores the shortest path from person to person (degree of seperation)
	
	public static void main(String[] args)     {
		String input = "";  //stores user input
		Scanner scan = new Scanner(System.in); //scans user input
		
		fillArray(friendships);
		
		
		do     {
	 System.out.println("Please enter a command");
	 input = scan.nextLine();
	 String[] command = input.split(" ");
	 
	 // prints statements after user input to confirm action or output result
	 switch(command[0])    {
	     case "i":
	    	 makeThemFriends(command[1], command[2]);
	    	 System.out.println("Person " + command[1] + " and " + "Person " + command[2] + " are friends!");
	    	 continue;
	     case "n":
	    	 System.out.println("Person " + command[1] + " has: " + howManyFriends(command[1]) + " friends");
	    	 continue;
	     case "d":
	    	 deleteFriendship(command[1], command[2]);
	    	 System.out.println("Person " + command[1] + " and " + "Person " + command[2] + " are no longer friends");
	    	 continue;
	     case "q":
	    	 System.out.println("Thanks for using this program!");
	     System.exit(0);
	     case "s":
	    	 int intCommand1 = Integer.parseInt(command[1]);
	    	 int intCommand2 = Integer.parseInt(command[2]);
	    	 degreeOfSeperation();
	    	 if (path[intCommand1][intCommand2] == 9999999) {
	    		 System.out.println("The degree of seperation between those two is: not connected ");
	    	 } else {
	    	System.out.println("The degree of seperation between those two is: " + path[intCommand1][intCommand2]);
	    	 } //else 
	    	 continue;
	     case "f":
	    	 System.out.println("The number of friends of friends they have is: " + friendsOfFriends(command[1]));
	    	 continue;
	 		} //switch
     } while (true);
	} //main		
		
	// fills array with initial values
	public static void fillArray(int[][] friendships)    {
		
		friendships [1][6] = 1;
		friendships [2][6] = 1;
		friendships [3][4] = 1;
		friendships [3][5] = 1;
		friendships [3][6] = 1;
		friendships [3][15] = 1;
		friendships [4][3] = 1;
		friendships [4][5] = 1;
		friendships [4][6] = 1;
		friendships [5][3] = 1;
		friendships [5][4] = 1;
		friendships [5][6] = 1;
		friendships [6][1] = 1;
		friendships [6][2] = 1;
		friendships [6][3] = 1;
		friendships [6][4] = 1;
		friendships [6][5] = 1;
		friendships [6][7] = 1;
		friendships [7][6] = 1;
		friendships [7][8] = 1;
		friendships [8][7] = 1;
		friendships [8][9] = 1;
		friendships [9][8] = 1;
		friendships [9][10] = 1;
		friendships [9][12] = 1;
		friendships [10][9] = 1;
		friendships [10][11] = 1;
		friendships [11][10] = 1;
		friendships [11][12] = 1;
		friendships [12][9] = 1;
		friendships [12][11] = 1;
		friendships [12][13] = 1;
		friendships [13][12] = 1;
		friendships [13][14] = 1;
		friendships [13][15] = 1;
		friendships [14][13] = 1;
		friendships [15][3] = 1;
		friendships [15][13] = 1;
		friendships [16][17] = 1;
		friendships [16][18] = 1;
		friendships [17][16] = 1;
		friendships [17][18] = 1;
		friendships [18][16] = 1;
		friendships [18][17] = 1;
		
	} //fillArray

	//this method determines the number of friends of friends a person has 
	
		 public static int friendsOfFriends(String string)     {
			 int person =  Integer.parseInt(string); //user input's entered person
			 int numOfFriendsOfFriends = 0;
			 
			 //determines the degree of seperation between people and find how many friends of friends the user's input has
			 for (int j = 0; j < path.length; j++)     {
				degreeOfSeperation();
			 if (path[person][j] == 2 && j != person)    {
				 numOfFriendsOfFriends++;
			 	} //if
			 } //for
		return numOfFriendsOfFriends;
	} //friendsOfFriends

		 //this method deletes a friendship between 2 people
		 
		public static void deleteFriendship(String string, String string2)    {
			int friend1 = Integer.parseInt(string);
		    int friend2 = Integer.parseInt(string2);
			 
			 friendships[friend1][friend2] = 0;
			 friendships[friend2][friend1] = 0;
			
	} //deleteFriendship

		// this method returns how many friends a person has
		
		public static int howManyFriends(String person1) {
			 int person = Integer.parseInt(person1);
			 int numOfFriends = 0;

			 //determines how many friends a person has using the friendships array
			for (int j = 0; j < friendships[person].length; j++)    {
						if (friendships[person][j] == 1 && j != person)    {
							numOfFriends++;
						} //if
				} //inner for
			 return numOfFriends;
	} // howManyFriends

		//this method makes 2 people friends
		
		public static void makeThemFriends(String person1, String person2)    {
			int friend1 = Integer.parseInt(person1);
			int friend2 = Integer.parseInt(person2);
		
			friendships[friend1][friend2] = 1;
			friendships[friend2][friend1] = 1;
			
	} //makeThemFriends

		//this method determines and returns the degree of separation between 2 people
		
		public static void degreeOfSeperation ()    {
		
			 /* This algorithm generates a 2-dimensional array that stores the degree of   separation between any two people. 
		      At each step in the algorithm, path[i][j] is the length of the shortest path
		from i to j using intermediate friends (1..k-1).   
		      Initialize the path array so that each path[i][j] is 
		 1 if two people are friends or infinity (that is, a very large number) if there is no relationship.
		 */
			
			//fills the path array with 1 if people are friends and 9999999 if they are not
			for (int i = 0; i < friendships.length; i++)    {
				for (int j = 0; j < friendships[i].length; j++)    {
				if (friendships [i][j] == 1)    {
					path[i][j] = friendships [i][j];
				} else    {
					path[i][j] = 9999999;
				} //else
			} //inner for
		} //outer for
			
			//fills the path array with the degree of separation between everyone
		 for (int k = 1; k < path.length; k++)    {
			 for (int i = 1; i < path.length; i++)    {
			    for (int j = 1; j < path.length; j++)    {
			    	path[i][j] = Math.min ( path[i][j], path[i][k] + path[k][j]);
			    	} //inner for
			 	} //middle for
		 	}//outer for
		} //degreeOfSep

	} //Main



