import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Course {
	private String id = "";
	private String type = "";
	private String name = "";
	private int maxEnroll;
	private int enrolled = 0;
	private int sections = 0;
	private static ArrayList <Course> allPossibleCourses = new ArrayList <Course> ();
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxEnroll() {
		return maxEnroll;
	}

	public void setMaxEnroll(int maxEnroll) {
		this.maxEnroll = maxEnroll;
	}
	
	public void decrementMaxEnroll() {
		this.maxEnroll--;
	}

	public int getEnrolled() {
		return enrolled;
	}

	public void setEnrolled(int enrolled) {
		this.enrolled = enrolled;
	}

	public int getSections() {
		return sections;
	}

	public void setSections(int sections) {
		this.sections = sections;
	}

	public static ArrayList <Course> getAllPossibleCourses() {
		return allPossibleCourses;
	}

	public static void setAllPossibleCourses(ArrayList <Course> allPossibleCourses) {
		Course.allPossibleCourses = allPossibleCourses;
	}
}