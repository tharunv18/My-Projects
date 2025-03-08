import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) throws IOException {

		// Enter data using BufferReader
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int nikkiPos = 0;
		int nikkiNeg = 0;
		int byronPos = 0;
		int byronNeg = 0;
		int stepsToWhistle = 0;
		int nikkiPosition = 0;
		int byronPosition = 0;
		int nikkiSteps = 0;
		int byronSteps = 0;
		int counter = 0;
		
		nikkiPos = Integer.parseInt(reader.readLine());
		nikkiNeg = Integer.parseInt(reader.readLine());
		byronPos = Integer.parseInt(reader.readLine());
		byronNeg = Integer.parseInt(reader.readLine());
		stepsToWhistle = Integer.parseInt(reader.readLine());
		
			while (nikkiSteps < stepsToWhistle - 1) {
				for (int i = 0; i < nikkiPos; i++) {
					if (nikkiSteps < stepsToWhistle) {
						nikkiPosition++;
						nikkiSteps++;
						
					}
				}
				
				for (int i = 0; i < nikkiNeg; i++) {
					if (nikkiSteps < stepsToWhistle) {
						nikkiPosition--;
						nikkiSteps++;
						
					}
				}
				
			}
			
			while (byronSteps < stepsToWhistle - 1) {
				for (int i = 0; i < byronPos; i++) {
					if (byronSteps < stepsToWhistle) {
						byronPosition++;
						byronSteps++;
					}
					
				}
				
				for (int i = 0; i < byronNeg; i++) {
					if (byronSteps < stepsToWhistle) {
						byronPosition--;
						byronSteps++;
					}
				}
			}
			
			if (byronPosition > nikkiPosition) {
				System.out.println("Byron");
			} else if (byronPosition < nikkiPosition) {
				System.out.println("Nikki");
			} else {
				System.out.println("Tied");
			}
		
		
		
		
		
	}

}
