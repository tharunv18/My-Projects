import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.Calendar;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) throws IOException {
	    SystemManager sm = new SystemManager();
	    
	    String [] input = Course.getFileContents("Timetables.txt");
	    Course.addLines(input);
	    
	    sm.initializeSequenceRules();
	    sm.initializeCourses();
	    sm.initializeBlockingRules();
	    
	    
	    sm.initializeStudentRequests();
	    sm.initializeTimeTables();
	    sm.writeOverlapsToFile();
	    System.out.println();
	    System.out.println();
	    System.out.println();
	    Course.printBlocks();
	    sm.getMetrics();

	    Course.writeArrayToFile("Timetables.txt", Course.getLines());
	}

}