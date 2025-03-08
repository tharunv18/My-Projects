import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {

	public static void main(String[] args)  throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int A = 0;
		int B = 0;
		
		do {
			String input = reader.readLine();
			String [] instructions = input.split(" ");
			
			switch (instructions[0]) {
				case "1": 
					if (instructions[1].equals("A")) {
						A = Integer.parseInt(instructions[2]);
					} else if (instructions[1].equals("B")) {
						B = Integer.parseInt(instructions[2]);
					}
					continue;
				case "2":
					if (instructions[1].equals("A")) {
						System.out.println(A);
					} else if (instructions[1].equals("B")) {
						System.out.println(B);
					}
					continue;
				case "3": 
					if (instructions[1].equals("A")) {
						if (instructions[2].equals("A")) {
							A += A;
						} else if (instructions[2].equals("B")) {
							A += B;
						}
					} else if (instructions[1].equals("B")) {
						if (instructions[2].equals("A")) {
							B += A;
						} else if (instructions[2].equals("B")) {
							B += B;
						}
					}
					continue;
				case "4": 
					if (instructions[1].equals("A")) {
						if (instructions[2].equals("A")) {
							A *= A;
						} else if (instructions[2].equals("B")) {
							A *= B;
						}
					} else if (instructions[1].equals("B")) {
						if (instructions[2].equals("A")) {
							B *= A;
						} else if (instructions[2].equals("B")) {
							B *= B;
						}
					}
					continue;
				case "5": 
					if (instructions[1].equals("A")) {
						if (instructions[2].equals("A")) {
							A -= A;
						} else if (instructions[2].equals("B")) {
							A -= B;
						}
					} else if (instructions[1].equals("B")) {
						if (instructions[2].equals("A")) {
							B -= A;
						} else if (instructions[2].equals("B")) {
							B -= B;
						}
					}
					continue;
				case "6": 
					if (instructions[1].equals("A")) {
						if (instructions[2].equals("A")) {
							A = A/A;
						} else if (instructions[2].equals("B")) {
							A = A/B;
						}
					} else if (instructions[1].equals("B")) {
						if (instructions[2].equals("A")) {
							B = B/A;
						} else if (instructions[2].equals("B")) {
							B = B/B;
						}
					}
					continue;
				case "7": 
					System.exit(0);;
			}
			
		} while (true);

	}

}
