import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {

	public static void main(String[] args)  throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String input = reader.readLine();
		String [] inputNums = input.split(" ");
		int [] distances = new int [4];
		int [][] cityDistances = new int [5][5];
		
		cityDistances[0][0] = cityDistances[1][1] = cityDistances[2][2] = cityDistances[3][3] = cityDistances[4][4] = 0;
		cityDistances [0][1] = cityDistances[1][0] = Integer.parseInt(inputNums[0]);
		cityDistances [0][2] = cityDistances[2][0] = Integer.parseInt(inputNums[1]) + Integer.parseInt(inputNums[0]);
		cityDistances [0][3] = cityDistances[3][0] = Integer.parseInt(inputNums[1]) + Integer.parseInt(inputNums[0]) + Integer.parseInt(inputNums[2]);
		cityDistances [0][4] = cityDistances[4][0] = Integer.parseInt(inputNums[1]) + Integer.parseInt(inputNums[0]) + Integer.parseInt(inputNums[2]) + Integer.parseInt(inputNums[3]);
		cityDistances [1][2] = cityDistances[2][1] = Integer.parseInt(inputNums[1]);
		cityDistances [1][3] = cityDistances[3][1] = Integer.parseInt(inputNums[1]) + Integer.parseInt(inputNums[2]);
		cityDistances [1][4] = cityDistances[4][1] = Integer.parseInt(inputNums[1]) + Integer.parseInt(inputNums[2]) + Integer.parseInt(inputNums[3]);
		cityDistances [2][3] = cityDistances[3][2] = Integer.parseInt(inputNums[2]);
		cityDistances [2][4] = cityDistances[4][2] = Integer.parseInt(inputNums[2])+ Integer.parseInt(inputNums[3]);
		cityDistances [3][4] = cityDistances[4][3] = Integer.parseInt(inputNums[3]);
		
		for (int i =0; i < 5; i++) {
			System.out.println();
			for (int j = 0; j< 5; j++) {
				System.out.print(cityDistances[i][j] + " ");
			}
		}
		
		
		
		
	}

}
