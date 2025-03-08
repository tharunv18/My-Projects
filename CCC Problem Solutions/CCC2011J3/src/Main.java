import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {

	public static void main(String[] args)  throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int num1 = Integer.parseInt(reader.readLine());
		int num2 = Integer.parseInt(reader.readLine());
		int length = 2;
		
		do {
			int difference = num1 - num2;
			if (difference <= num2) {
				num1 = num2;
				num2 = difference;
				//System.out.println(difference);
				length++;
			} else {
				length++;
				System.out.println(length);
				break;
			}
			
		} while (true);
		
	}

}
