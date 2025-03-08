import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Course implements Comparable<Course>{
	private String id = "";
	private String type = "";
	private String name = "";
	private int block = 9;
	private int maxEnroll;
	private int enrolled = 0;
	private int sections = 0;
	private static ArrayList <Course> allPossibleCourses = new ArrayList <Course> ();
	private static ArrayList <Block> blocks = new ArrayList <Block>();
	private static Block semOneBlockA = new Block();
	private static Block semOneBlockB = new Block();
	private static Block semOneBlockC = new Block();
	private static Block semOneBlockD = new Block();
	private static Block semTwoBlockA = new Block();
	private static Block semTwoBlockB = new Block();
	private static Block semTwoBlockC = new Block();
	private static Block semTwoBlockD = new Block();
	private static Block linearCourses = new Block();
	private  ArrayList <Student> students = new ArrayList <Student> ();
	private static ArrayList<Block> bestTimetable = new ArrayList<Block>();
	private static ArrayList <String> lines = new ArrayList<String>();
	
	public String getId() {
		return id;
	}
	public void addStudent(Student student) {
		students.add(student);
	}
	public ArrayList<Student> getStudents () {
		return students;
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
	public void incrementEnrolled() {
		enrolled++;
	}
	public void setEnrolled(int enrolled) {
		this.enrolled = enrolled;
	}
	public int getSections() {
		return sections;
	}
	public void decrementSections() {
		sections--;
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
	
	public int assignBlock(String usedNums) {
		int randomNum = 0;
//		if (this.getName().contains("Linear") || this.getName().contains("LINEAR") || this.getName().contains("GEOMETRY 12")) {
//			linearCourses.addCourse(this);
//			return 9;
//		}
		do {
		 randomNum = (int)(Math.random() * 8) + 1;
		} while (usedNums.contains(randomNum + ""));
		//System.out.println("normal usedNums are: " + usedNums + " and the block assigned was: " + randomNum);
		switch (randomNum) {
			case 1: 
				semOneBlockA.addCourse(this);
				this.block = 1;
				break;
			case 2: 
				semOneBlockB.addCourse(this);
				this.block = 2;
				break;
			case 3:
				semOneBlockC.addCourse(this);
				this.block = 3;
				break;
			case 4:
				semOneBlockD.addCourse(this);
				this.block = 4;
				break;
			case 5: 
				semTwoBlockA.addCourse(this);
				this.block = 5;
				break;
			case 6:
				semTwoBlockB.addCourse(this);	
				this.block = 6;
				break;
			case 7:
				semTwoBlockC.addCourse(this);
				this.block = 7;
				break;
			case 8:
				semTwoBlockD.addCourse(this);
				this.block = 8;
				break;
		}
		return randomNum;
	}
	
	public void addLinearCourse () {
		linearCourses.addCourse(this);
	}
	
	public int assignBlockAlphabetically(String usedNums) {
		int randomNum = 0;
//		if (this.getName().contains("Linear") || this.getName().contains("LINEAR") || this.getName().contains("GEOMETRY 12")) {
//			this.block = 9;
//			linearCourses.addCourse(this);
//			return 9;
//		}
		do {
			randomNum = (int)(Math.random() * 8) + 1;
		} while (usedNums.contains(randomNum + ""));
		int index;
		switch (randomNum) {
			case 1: 
				index = findInsertionIndex(semOneBlockA.getCourses(), this.getId());
				semOneBlockA.addCourse(this, index);
				this.block = 1;
				break;
			case 2: 
				index = findInsertionIndex(semOneBlockB.getCourses(), this.getId());
				semOneBlockB.addCourse(this, index);
				this.block = 2;
				break;
			case 3:
				index = findInsertionIndex(semOneBlockC.getCourses(), this.getId());
				semOneBlockC.addCourse(this, index);
				this.block = 3;
				break;
			case 4:
				index = findInsertionIndex(semOneBlockD.getCourses(), this.getId());
				semOneBlockD.addCourse(this, index);
				this.block = 4;
				break;
			case 5: 
				index = findInsertionIndex(semTwoBlockA.getCourses(), this.getId());
				semTwoBlockA.addCourse(this, index);
				this.block = 5;
				break;
			case 6:
				index = findInsertionIndex(semTwoBlockB.getCourses(), this.getId());
				semTwoBlockB.addCourse(this, index);
				this.block = 6;
				break;
			case 7:
				index = findInsertionIndex(semTwoBlockC.getCourses(), this.getId());
				semTwoBlockC.addCourse(this, index);
				this.block = 7;
				break;
			case 8:
				index = findInsertionIndex(semTwoBlockD.getCourses(), this.getId());
				semTwoBlockD.addCourse(this, index);
				this.block = 8;
				break;
		}
		return randomNum;
	}
	
	private int findInsertionIndex(ArrayList<Course> a, String id) {
		for (int i = 1; i < a.size() - 1; i++) {
			if (a.get(i - 1).getId().compareTo(id) >= 0 && a.get(i).getId().compareTo(id) <= 0) {
				return i;
			}
		}
		return a.size() - 1;
	}
	
	public static void initializeBlocks() {
		blocks = new ArrayList <Block>();
		getBlocks().add(semOneBlockA);
		getBlocks().add(semOneBlockB);
		getBlocks().add(semOneBlockC);
		getBlocks().add(semOneBlockD);
		getBlocks().add(semTwoBlockA);
		getBlocks().add(semTwoBlockB);
		getBlocks().add(semTwoBlockC);
		getBlocks().add(semTwoBlockD);
		getBlocks().add(linearCourses);
	}
	public static void clearBlocks() {
		semOneBlockA = new Block();
		semOneBlockB = new Block();
		semOneBlockC = new Block();
		semOneBlockD = new Block();
		semTwoBlockA = new Block();
		semTwoBlockB = new Block();
		semTwoBlockC = new Block();
		semTwoBlockD = new Block();
		linearCourses = new Block();
	}
	public static void printBlocks() {
		int maxLine = 0;
		System.out.println("Course Table: ");
		lines.add("Course Table: ");
		System.out.println("S1 A						                                                          S1 B						                                                            S1 C						                                                      S1 D						                                                        S2 A					 	                                                          S2 B						                                                            S2 C						                                                      S2 D						                                                        Linear");
		lines.add("S1 A                                                        S1 B                                                        S1 C                                                        S1 D                                                        S2 A                                                        S2 B                                                        S2 C                                                        S2 D                                                         Linear");
		System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		lines.add("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		String line = "";
		
		for (int i = 0; i < blocks.size(); i++) {
			if (blocks.get(i).getCourses().size() > maxLine) {
				maxLine = blocks.get(i).getCourses().size();
			}
		}
		for (int i = 0; i < maxLine; i++) {
			line = "";
			for (int j = 0; j < blocks.size(); j++) {
				if (i >= blocks.get(j).getCourses().size()) {
					 
				} else {
					System.out.print(blocks.get(j).getCourses().get(i).getName() + ":" + blocks.get(j).getCourses().get(i).getEnrolled() + "/" + (blocks.get(j).getCourses().get(i).getMaxEnroll() + blocks.get(j).getCourses().get(i).getEnrolled()));
					line += blocks.get(j).getCourses().get(i).getName() + ":" + blocks.get(j).getCourses().get(i).getEnrolled() + "/" + (blocks.get(j).getCourses().get(i).getMaxEnroll() + blocks.get(j).getCourses().get(i).getEnrolled());
					for (int k = 0; k < (100-blocks.get(j).getCourses().get(i).getName().length()); k++) {
						System.out.print(" ");
						line += " ";
					}
				}
			}
			line += "\n";
			System.out.println();
			lines.add(line);
		}
		System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		lines.add("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
	
		
	}
	public int getBlock() {
		return block;
	}
	public void setBlock(int block) {
		this.block = block;
	}
	public String toString() {
		return this.sections + "";
	}
	public int compareTo(Course a) {
		if (this.sections < a.sections)
			return -1;
		else if (a.sections < this.sections)
			return 1;
		return 0;
	}
	public static ArrayList <Block> getBlocks() {
		return blocks;
	}
	public static void setBlocks(ArrayList <Block> blocks) {
		Course.blocks = blocks;
	}
	public static void setBestTimetable () {
//		bestTimetable = (ArrayList<Block>) blocks.clone();
	}
	public int assignBlockSecond(String usedNums) {
		int randomNum = 0;
//		if (this.getName().contains("Linear") || this.getName().contains("LINEAR") || this.getName().contains("GEOMETRY 12")) {
//			this.block = 9;
//			linearCourses.addCourse(this);
//			return 9;
//		}
		do {
		 randomNum = (int)(Math.random() * 8) + 1;
		} while (usedNums.contains(randomNum + ""));
		//System.out.println("second usedNums are: " + usedNums + " and the block assigned was: " + randomNum);
		switch (randomNum) {
			case 1: 
				semOneBlockA.addCourse(this);
				this.block = 1 + 4;
				break;
			case 2: 
				semOneBlockB.addCourse(this);
				this.block = 2 + 4;
				break;
			case 3:
				semOneBlockC.addCourse(this);
				this.block = 3 + 4;
				break;
			case 4:
				semOneBlockD.addCourse(this);
				this.block = 4 + 4;
				break;
			case 5: 
				semTwoBlockA.addCourse(this);
				this.block = 5;
				break;
			case 6:
				semTwoBlockB.addCourse(this);	
				this.block = 6;
				break;
			case 7:
				semTwoBlockC.addCourse(this);
				this.block = 7;
				break;
			case 8:
				semTwoBlockD.addCourse(this);
				this.block = 8;
				break;
		}
		return randomNum;
	}
	public int assignBlockFirst(String usedNums) {
		int randomNum = 0;
//		if (this.getName().contains("Linear") || this.getName().contains("LINEAR") || this.getName().contains("GEOMETRY 12")) {
//			this.block = 9;
//			linearCourses.addCourse(this);
//			return 9;
//		}
		do {
		 randomNum = (int)(Math.random() * 8) + 1;
		} while (usedNums.contains(randomNum + ""));
		//System.out.println("first usedNums are: " + usedNums + " and the block assigned was: " + randomNum);
		switch (randomNum) {
			case 1: 
				semOneBlockA.addCourse(this);
				this.block = 1;
				break;
			case 2: 
				semOneBlockB.addCourse(this);
				this.block = 2;
				break;
			case 3:
				semOneBlockC.addCourse(this);
				this.block = 3;
				break;
			case 4:
				semOneBlockD.addCourse(this);
				this.block = 4;
				break;
			case 5: 
				semTwoBlockA.addCourse(this);
				this.block = 5 - 4;
				break;
			case 6:
				semTwoBlockB.addCourse(this);	
				this.block = 6 - 4;
				break;
			case 7:
				semTwoBlockC.addCourse(this);
				this.block = 7 - 4;
				break;
			case 8:
				semTwoBlockD.addCourse(this);
				this.block = 8 - 4;
				break;
		}
		return randomNum;
	}
	public static void printBestTimetable() {
		int maxLine = 0;
		
		System.out.println("Course Table: ");
		System.out.println("S1 A                                                        S1 B                                                        S1 C                                                        S1 D                                                        S2 A                                                        S2 B                                                        S2 C                                                        S2 D                                                         Linear");
		System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		for (int i = 0; i < bestTimetable.size(); i++) {
			if (bestTimetable.get(i).getCourses().size() > maxLine) {
				maxLine = bestTimetable.get(i).getCourses().size();
			}
		}
		for (int i = 0; i < maxLine; i++) {
			for (int j = 0; j < bestTimetable.size(); j++) {
				if (i >= bestTimetable.get(j).getCourses().size()) {
					 
				} else {
					System.out.print(bestTimetable.get(j).getCourses().get(i).getName() + ":" + bestTimetable.get(j).getCourses().get(i).getEnrolled() + "/" + (bestTimetable.get(j).getCourses().get(i).getMaxEnroll() + bestTimetable.get(j).getCourses().get(i).getEnrolled()));
					for (int k = 0; k < (60-bestTimetable.get(j).getCourses().get(i).getName().length()); k++) {
						System.out.print(" ");
					}
				}
			}
			System.out.println();
		}
		System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
	}
	
	//  reads fileName and returns the contents as String array
    //  with each line of the file as an element of the array
    public static String[] getFileContents(String fileName) {
        String[] content = null;
        try {
            // Read the lines from the file and collect them into a list
            List < String > lines = Files.lines(Paths.get(fileName))
                .collect(Collectors.toList());

            // copy the lines from the list into a 1D array
            content = lines.toArray(new String[0]);

        } catch (IOException e) {
            System.out.println("File Read Error");
            e.printStackTrace();
        }

        return content;

    } // getFileContents
	
	 // writes the array a to fileName, one array element per line in the file
    public static void writeArrayToFile(String fileName, ArrayList<String> a) {

        // this is called a try-with-resources
        // it automatically closes the resource when
        // the program is done with it
        try (FileWriter writer = new FileWriter(fileName)) {

            // Write each line followed by a newline character
            for (String line: a) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            System.out.println("File Write Error");
            e.printStackTrace();
        }
    } // writeArrayToFile
	public static ArrayList <String> getLines() {
		return lines;
	}
	public static void setLines(ArrayList <String> lines) {
		Course.lines = lines;
	}
	public static void addLines(String[] a) {
		for (int i = 0; i < a.length; i++) {
			lines.add(a[i]);
		} // for
	}
}