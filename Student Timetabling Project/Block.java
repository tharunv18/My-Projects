import java.util.ArrayList;

public class Block {
	private ArrayList <Course> courses = new ArrayList<Course>();
	private String period = "";
	
	public Block () {
	}
	
	public ArrayList <Course> getCourses() {
		return courses;
	}

	public void addCourse(Course newCourse) {
		courses.add(newCourse);
	}
	
	public void addCourse(Course newCourse, int index) {
		courses.add(index, newCourse);
	}

	public String getPeriod() {
		return period;
	}
	
	public String toString() {
		String output = "";
		for(Course course: courses) {
			output += course.getName() + "\n"; 
		}
		return output;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
	
	public void removeCourse(int i) {
		courses.remove(i);
	}
}
