import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {

	public static void main(String[] args)  throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int num = Integer.parseInt(reader.readLine());
		String [] input = new String [3];
		input[0] = "*x*";
		input[1] = " xx";
		input[2] = "* *";
		int counter = 0;
		int counter2 = 0;
		
		if (num == 1) {
			System.out.println("*x*");
			System.out.println( " xx");
			System.out.println( "* *");
		} else if (num == 2) {
			System.out.println("**xx**");
			System.out.println("**xx**");

			System.out.println( "  xxxx");
			System.out.println( "  xxxx");
			System.out.println( "**  **");
			System.out.println( "**  **");
		} else {
		
			for (int i = 0; i < num; i++) {
				if (counter2 == 3) {
					break;
				}
					String [] line = input[counter2].split("");
					counter2++;
				
				for (int j = 0; j < num; j++) {
					counter = 0;
					while (counter < num) {
						System.out.print(line[0]);
						counter++;
					}
					
					counter = 0;
					while (counter < num) {
						System.out.print(line[1]);
						counter++;
					}
					
					counter = 0;
					while (counter < num) {
						System.out.print(line[2]);
						counter++;
					}
					System.out.println();
						
				}	
			}
		}
	}

}
