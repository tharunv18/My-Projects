import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {

	public static void main(String[] args)  throws IOException {
		//instructions are 5 digits
		//first two: need sum.
		//store last 3 digits in one number
		//last line = 99999
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		String direction = "";
		String output = "";

		while (input != "99999"){
			input = reader.readLine();
			if (!(input.equals("99999"))) {
				String [] inputNumbers = input.split(""); 
				if ((Integer.parseInt(inputNumbers[0]) + Integer.parseInt(inputNumbers[1])) % 2 == 0 && (Integer.parseInt(inputNumbers[0]) + Integer.parseInt(inputNumbers[1])) != 0) {
				direction = "right";
				} else if ((Integer.parseInt(inputNumbers[0]) + Integer.parseInt(inputNumbers[1])) % 2 == 1)  {
				direction = "left";
				} 
				String numSteps = inputNumbers[2] + inputNumbers[3] + inputNumbers[4];
				int numberSteps = Integer.parseInt(numSteps);
				output += direction + " " + numberSteps + "\n";
			} else {
				break;
			}
		}
		

		System.out.println(output);
		

	}

}
