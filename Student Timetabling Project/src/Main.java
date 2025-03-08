import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.Calendar;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) throws IOException {
	    SystemManager sm = new SystemManager();
	    sm.initializeCourses();
	  // sm.initializeSequenceRules();
	   // sm.initializeBlockingRules();
	    sm.initializeStudentRequests();
	    sm.initializeTimeTables();
		
	}

}