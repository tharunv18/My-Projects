import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {

	public static void main(String[] args)  throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int year = Integer.parseInt(reader.readLine());
		boolean found = false;
		int nextYear = 0;
		while (found == false) {
			year += 1;
			String yearString = "" + year;
			String [] digits = yearString.split("");
			String usedDigits = "";
			for (int i = 0; i < yearString.length(); i++) {
				if (usedDigits.contains(digits[i])) {
					found = false;
					break;
				} else {
					usedDigits += digits[i];
					found = true;
				}
			}
			
			if (found == true) {
				nextYear = Integer.parseInt(yearString);
				break;
			}
		}

		System.out.println(nextYear);
		
	}

}
