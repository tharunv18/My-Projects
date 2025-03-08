import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {

	public static void main(String[] args)  throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int lines = Integer.parseInt(reader.readLine());
		String output = "";
		int length = 1;
		
		for (int i = 0; i < lines; i++) {
			String input = reader.readLine();
			String [] chars = input.split("");
			for (int j = 0; j< input.length(); j++) {
				if (j+1 == input.length()) {
					output += length + " " + chars[j] + " ";
					break;
				} else {
					if (chars[j + 1].equals(chars[j])) {
						length++;
						//System.out.println(length + " " + chars[j+1] + " " + chars[j]);
					} else {
						//System.out.println(chars[i]);
						output += length + " " + chars[j] + " ";
						length = 1;
					}//else
				}
			}//inner for
			output += "\n";
		}//outer for
		
		System.out.println(output);

	}

}
