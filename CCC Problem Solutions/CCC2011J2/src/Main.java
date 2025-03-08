import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {

	public static void main(String[] args)throws IOException {
		// Enter data using BufferReader
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int humidity = 0;
		int hoursToWait = 0;
		int hoursTaken = 0;
		boolean possible = false;
		int altitude = 0;
		
		humidity = Integer.parseInt(reader.readLine());
		hoursToWait = Integer.parseInt(reader.readLine());
		
		for (int i = 1; i < hoursToWait; i++) {
			
			altitude += (-6 *i * i * i * i);
			altitude += (humidity * i * i * i);
			altitude += (2 * i * i);
			altitude += i;
			
			if (altitude <= 0) {
				hoursTaken = i;
				possible = true;
				break;
			}
		}
		
		if (possible == true) {
			System.out.println("The balloon first touches the ground at hour: " + hoursTaken);
		} else {
			System.out.println("The balloon does not touch ground in the given time.");
		}
		
		
		
		
	}

}
