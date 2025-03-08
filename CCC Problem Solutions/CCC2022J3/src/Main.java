import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String input = reader.readLine();
		String output = "";
		
		for (int i = 0; i < input.length(); i++) {
			String [] instructions = input.split("");
			if (instructions[i].equals("+")) {
				output += " tighten ";
			} else if (instructions[i].equals("-")) {
				output += " loosen "; 
			} else {
				output += instructions[i];
				
				
				if (i + 1  < input.length()) {
					//if instructions[i] is a number, and instructions[i + 1] is a letter, add \n
					if ((input.charAt(i) >= 48 && input.charAt(i) <= 57) && (input.charAt(i + 1) >= 65 && input.charAt(i) <= 90)) {
						output += "\n";
					}
				}
				
			}
			
		}
		
		System.out.println(output);

	}

}
