import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {

	public static void main(String[] args)  throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int lines = Integer.parseInt(reader.readLine());
		int [] x = new int [lines];
		int [] y = new int [lines];
		int maxX = 0;
		int maxY = 0;
		int minX = 0;
		int minY = 0;
		
		
		for (int i = 0; i < lines; i++) {
			String input =  reader.readLine();
			String [] nums = input.split(",");
			x[i] = Integer.parseInt(nums[0]);
			y[i] = Integer.parseInt(nums[1]);
		}
		minY = y[0];
		minX = x[0];
		for (int i = 0; i < lines; i++) {
			if (x[i] > maxX) {
				maxX = x[i];
			} else if (x[i] < minX) {
				minX = x[i];
			}//else if
			
			if (y[i] > maxY) {
				maxY = y[i];
			} else if (y[i] < minY) {
				minY = y[i];
			}//else if
		}//for
		
		System.out.println((minX -1) + "," + (minY -1));
		System.out.println((maxX + 1) + "," + (maxY + 1));
	}//main

}
