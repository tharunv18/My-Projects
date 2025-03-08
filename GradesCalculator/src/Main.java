import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Main    {
		static ArrayList <Student> people = new ArrayList <Student> ();
		
	public static void main(String[] args)    {
		Scanner in = new Scanner(System.in); 
		
		//get input and output file names from user
		System.out.println("Please enter input file name: ");
		String input = in.nextLine();
		System.out.println("Please enter output file name: ");
		String output = in.nextLine();
		
		String[] fileContents = getFileContents(input);
		int size = Integer.parseInt(fileContents[0]);
		String lastName = "";
		String firstName = "";
		String subject = "";
		
		//splits file contents and stores data in variables before initializing and storing the new student
		for (int i = 1; i < fileContents.length; i++)    {
			String [] data = fileContents[i].split(" ");
			int [] grades =  new int [fileContents[i].length()];
			
			if (i % 2 != 0)    {
				firstName = data[1];
				lastName = data[0].replace(",", "");
			} else    {
				subject =  data[0];
				data[0] = "";
				
				for (int j = 1; j < data.length; j++) {
					grades[j] = Integer.parseInt(data[j]);
				}//for
				
				if (subject.equals("English"))    {
					people.add(new EnglishStudents(firstName, lastName, grades));
				} else if (subject.equals("Math"))    {
					people.add(new MathStudents(firstName, lastName, grades));
				} else    {
					people.add(new HistoryStudents(firstName, lastName, grades));
				} //inner else
			}//outer else
		}//for
		writeArrayToFile(output, getOutput(size));
	}//main
	
	//return a formatted output string
	public static String [] getOutput (int size)    {
		String [] output = new String[4];
		String subject = "";
		String englishStudents = "";
		String historyStudents = "";
		String mathStudents = "";
		String title = String.format("%-20s", "Name") + String.format("%-20s", "Final Exam") +  String.format("%-20s", "Final Average") +   String.format("%-20s", "Letter Grade") + "\n";
	
		historyStudents += "***** History ******" + "\n";
		historyStudents += title;
		
		englishStudents += "****** English *****" + "\n";
		englishStudents += title;
		
		mathStudents += "****** English *****" + "\n";
		mathStudents += title;
		
		Collections.sort(people);
		
		//creates a string for all 3 subjects by iterating through the people array
		for (Student s1 : people) {
			subject= s1.getSubject();
			String firstName = s1.getFirstName();
			String lastName = s1.getLastName();
			String finalExamMark = s1.getFinalExamMark() + "%";
			String finalAverage = s1.getFinalAverage() + "%";
			String letterGrade = s1.getLetterGrade();
			
			if (subject.equals("History")) {
				historyStudents += String.format("%-20s", firstName + " " + lastName)  + String.format("%-20s",finalExamMark )   + String.format("%-20s",finalAverage) + String.format("%-20s",letterGrade) + "\n";
			} else if (subject.equals("English")) {
				englishStudents += String.format("%-20s", firstName + " " + lastName) + String.format("%-20s",finalExamMark) + String.format("%-20s",finalAverage) + String.format("%-20s",letterGrade) + "\n";
			} else {
				mathStudents += String.format("%-20s", firstName + " " + lastName)  + String.format("%-20s",finalExamMark)  + String.format("%-20s",finalAverage)+ String.format("%-20s",letterGrade) + "\n";
			} //else
		}//for
		
		output[0] = historyStudents;
		output[1] = englishStudents;
		output[2] = mathStudents;
		output[3] = calculateGradeDistribution();
		
		return output;
	}//getOutput

	//calculates grade distribution
	 private static String calculateGradeDistribution()    {
		 int numOfA = 0;
		 int numOfB = 0;
		 int numOfC = 0;
		 int numOfD = 0;
		 int numOfF = 0;
		 String gradeDistribution = "";
		 gradeDistribution += "****** Grade Distribution *****" + "\n";
		 
		 //determines each students letter grade and stores it in a variable for each letter
		 for (Student s1 : people) {
			 if (s1.getLetterGrade().equals("A"))    {
				 numOfA++;
			 } else if (s1.getLetterGrade().equals("B"))    {
				 numOfB++;
			 } else if (s1.getLetterGrade().equals("C"))    {
				 numOfC++;
			 } else if (s1.getLetterGrade().equals("D"))    {
				 numOfD++;
			 } else    {
				 numOfF++;
			 }//else
		 }//for
		 
		 gradeDistribution += String.format("%-8s", "A - " + numOfA);
		 gradeDistribution += String.format("%-8s", "B - " + numOfB);
		 gradeDistribution += String.format("%-8s", "C - " + numOfC);
		 gradeDistribution += String.format("%-8s", "D - " + numOfD);
		 gradeDistribution += String.format("%-8s", "F - " + numOfF);
		 
		 return gradeDistribution;
	}//calcGradeDistr


	// reads fileName and returns the contents as String array
    //  with each line of the file as an element of the array
	public static String [] getFileContents(String fileName)    {

        String [] contents = null;
        int length = 0;
        try    {

           // input
           String folderName = "/subFolder/"; // if the file is contained in the same folder as the .class file, make this equal to the empty string
           String resource = fileName;

			// this is the path within the jar file
			InputStream input = Main.class.getResourceAsStream(folderName + resource);
			if (input == null)    {
				// this is how we load file within editor (eg eclipse)
				input = Main.class.getClassLoader().getResourceAsStream(resource);
			}//if
			
			BufferedReader in = new BufferedReader(new InputStreamReader(input));	
          
			in.mark(Short.MAX_VALUE);  // see api

           // count number of lines in file
           while (in.readLine() != null)    {
             length++;
           }//while

           in.reset(); // rewind the reader to the start of file
           contents = new String[length]; // give size to contents array

           // read in contents of file and print to screen
           for (int i = 0; i < length; i++)    {
             contents[i] = in.readLine();
           }//for
           
           in.close();
       } catch (Exception e) {
           System.out.println("File Input Error");
       }//catch
        
       return contents;

    } // getFileContents

	   // writes the array a to fileName, one array element per line in the file
	public static void writeArrayToFile(String fileName, String [] a)    {
		try    {

           // output file pointer
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));

           // write array to file
			for (int i = 0; i < a.length; i++) {
               out.write(a[i] + "");
               out.newLine(); // adds new line to file
			} // for

			out.close();

       } catch (Exception e)    {
           System.out.println("File Output Error");
       }//catch
    } // writeArrayToFile
}//Main