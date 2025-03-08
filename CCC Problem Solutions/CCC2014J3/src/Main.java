import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {

	public static void main(String[] args)  throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int Antonia = 100;
		int David = 100;
		int num = Integer.parseInt(reader.readLine());
		
		for (int i =0; i < num; i++) {
			String [] input = reader.readLine().split(" ");
			
			if (Integer.parseInt(input[0]) > Integer.parseInt(input[1])) {
				David -= Integer.parseInt(input[0]);
			} else if (Integer.parseInt(input[1]) > Integer.parseInt(input[0])) {
				Antonia -= Integer.parseInt(input[1]);
			}
		}
		System.out.println(Antonia);
		System.out.println(David);
	}

}
