import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) throws IOException {
		//every palindrome -> bb or nan
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String palindrome = "";
		palindrome = reader.readLine();
		String[] palindromeLetters = palindrome.split("");
		int palindromeLength = 0;
		
		for (int i = 0; i < palindrome.length(); i++) {
			if (palindromeLetters[i -1].equals(palindromeLetters[i -1])) {
				palindromeLength = 2;
				for (int j = 2; j < palindrome.length() - i; j++) {
					if (palindromeLetters[i - j].equals(palindromeLetters[i + j])) {
						palindromeLength += 2; 
					}
				}
				
				
			} else if (palindromeLetters[i +1].equals(palindromeLetters[i-1])) {
				palindromeLength = 3;
				for (int j = 2; j < palindrome.length() - i; j++) {
					if (palindromeLetters[i - j].equals(palindromeLetters[i + j])) {
						palindromeLength += 2; 
					}
				}
				
			}
		}

	}

}
