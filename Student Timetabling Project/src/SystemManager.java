
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SystemManager {
	private static ArrayList <Course> allPossibleCourses = new ArrayList <Course> ();
	private static ArrayList <String> blockingRules = new ArrayList <String> ();
	private static ArrayList <String> sequenceRules = new ArrayList <String> ();
	private static ArrayList <Student> students = new ArrayList <Student> ();
	
	public static void initializeBlockingRules () throws IOException{
		BufferedReader data = new BufferedReader(new FileReader("src/files/Course Blocking Rules.csv"));
		String [] blockRules = data.lines().toArray(String[]::new);
		
		for (int i = 5; i < blockRules.length; i++) {
			String[] line = blockRules[i].split(",");
			blockingRules.add(line[2]);
			System.out.println("Blocking Rule: " + line[2]);
		}
	}
	
	public static void initializeSequenceRules () throws IOException{
		BufferedReader data = new BufferedReader(new FileReader("src/files/Course Sequencing Rules.csv"));
		String [] seqRules = data.lines().toArray(String[]::new);
		
		for (int i = 5; i < seqRules.length; i++) {
			String[] line = seqRules[i].split(",");
			sequenceRules.add(line[2]);
			System.out.println("Sequence Rule: " + line[2]);
		}
		
	}

	public static void initializeCourses () throws IOException{
		BufferedReader data = new BufferedReader(new FileReader("src/files/Course Information (tally).csv"));
		String [] courseInfo = data.lines().toArray(String[]::new);
		
		for (int i = 2; i < courseInfo.length; i+=2) {
			Course newCourse = new Course();
			String[] line = courseInfo[i].split(",");
			newCourse.setId(line[0]);
			newCourse.setName(line[1]);
			newCourse.setType(line[2]);
			newCourse.setSections(Integer.parseInt(line[8]));
			newCourse.setMaxEnroll(Integer.parseInt(line[7]) * newCourse.getSections());
			allPossibleCourses.add(newCourse);
			//System.out.println("Course Added: " + newCourse.getName() + " " + line[0]);
		}
	}
	
	public static void initializeStudentRequests() throws IOException {
		Student currentStudent = new Student ();
		BufferedReader data = new BufferedReader(new FileReader("src/files/cleanedstudentrequests.csv"));
		String [] studentRequests = data.lines().toArray(String[]::new);
		for (int i = 0; i < studentRequests.length; i++) {
			String[] line = studentRequests[i].split(",");
			if (line[0].equals("ID")) {
				currentStudent.getPrimaryCourses().sort(null);
				currentStudent.getAlternateCourses().sort(null);
				students.add(currentStudent);
				currentStudent = new Student (Integer.parseInt(line[1]));
				i++;
			} else { 
				int courseIndex = binCourseSearch(0, allPossibleCourses.size() - 1, line[0]);
				if (courseIndex == -1) { // if course does not exist in course list, generate a new course based on
					Course nullCourse = new Course();
					nullCourse.setId(line[0]);
					nullCourse.setName(line[2]);
					nullCourse.setType("null");
					nullCourse.setMaxEnroll(0);
					nullCourse.setSections(0);
					if (line[3].equals("Y")) {
						currentStudent.addAlternateCourses(nullCourse);
					} else {
						currentStudent.addPrimaryCourses(nullCourse);
					} // else 
					allPossibleCourses.add(nullCourse);
				} else {
					if (line[3].equals("Y")) {
						currentStudent.addAlternateCourses(allPossibleCourses.get(courseIndex));
					} else {
						currentStudent.addPrimaryCourses(allPossibleCourses.get(courseIndex));
					} // else 
				} // else 
			} // else
			//System.out.println("Student Added: " + currentStudent.getId());
		} // for
	
		//System.out.println(	Arrays.deepToString(students.get(99).getPrimaryCourses().toArray()));
	} // initializeStudentRequests
	
	public static void initializeTimeTables () {
		for (int i = 0; i < students.size(); i++) {
			for (int j = 0; j < students.get(i).getPrimaryCourses().size(); j++) {
				if (students.get(i).getPrimaryCourses().get(j).getMaxEnroll() > 0) {
					students.get(i).getPrimaryCourses().get(j).decrementMaxEnroll();
					students.get(i).addCourseToTimetable(students.get(i).getPrimaryCourses().get(j));
				} else {
					for (int k = 0; k < students.get(i).getAlternateCourses().size(); k++) {
						if (students.get(i).getAlternateCourses().get(k).getMaxEnroll() > 0) {
							students.get(i).getAlternateCourses().get(k).decrementMaxEnroll();
							students.get(i).addCourseToTimetable(students.get(i).getAlternateCourses().get(k));
						}
						students.get(i).removeAlternateCourse();
						break;
					}
					
				}
			}
			if (i > 700 ) {
				students.get(i).printTimetable();
			}
		}
	}
	
	private static int binCourseSearch(int l, int r, String id) {
		if (r >= l) {
			int m = l + (r - l) / 2;
			
			if (allPossibleCourses.get(m).getId().equals(id)) {
				return m;
			}
			
			if (allPossibleCourses.get(m).getId().compareTo(id) > 0) {
				return binCourseSearch(l, m - 1, id);
			}
			
			return binCourseSearch(m + 1, r, id);
		}
		
		return -1;
	}
	
	private static int binStudentCourseSearch(int l, int r, String id,ArrayList<Course> list) {
		if (r >= l) {
			int m = l + (r - l) / 2;
			
			if (list.get(m).getId().equals(id)) {
				return m;
			}
			
			if (list.get(m).getId().compareTo(id) > 0) {
				return binStudentCourseSearch(l, m - 1, id,list);
			}
			
			return binStudentCourseSearch(m + 1, r, id,list);
		}
		
		return -1;
	}
	
	public static void getMetrics() {
		int applicable = students.size();
		int right = 0;
		int full = 0;
		int close = 0;
		int overall = 0;
		int overallR = 0;
		for(Student student: students) {
			for(int i = 0; i < student.getPrimaryCourses().size(); i++) {
				if(binStudentCourseSearch(0, student.getTimetable().size() - 1, student.getPrimaryCourses().get(i).getId(),student.getTimetable()) != -1){
					right++;
					overallR++;
				} // if
				overall++;
				
			} // for
			if(student.getPrimaryCourses().size() < 8) {
				applicable--;
				right = 0;
				continue;
			} // if
			
			if(right >= student.getPrimaryCourses().size()) {
				close++;
				full++;
			} // if
			
			if(right >= student.getPrimaryCourses().size() -1) {
				close++;
			} // if
			
			right = 0;
		} // for

		System.out.println("The overall percent of requested courses that were placed: " + (double) overallR/overall * 100 + "%");
		System.out.println("Percent of Students who got 8/8 of the courses they requested: " + (double)full/applicable * 100+ "%");
		System.out.println("Percent of Students who got 8/8 or 7/8 of their requested courses: " + (double)close/applicable * 100 + "%");
		
	} // getMetrics
}
