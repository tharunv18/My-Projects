public class Student  implements Comparable<Student> {
     protected String firstName = "";
	 protected String lastName = "";
	 protected String subject = "";
	 
	 protected double finalExam = 0;
	 protected double average = 0;
	 protected String letterGrade = "";
	 private int [] gradesArray;
	
	//default constructor
	public Student ()    {
		this.firstName = "";
		this.lastName = "";
		this.subject = "";
		this.finalExam = 0;
		this.average = 0;
	}//Student
	
	//initializes a new student
	public Student (String fn, String ln, String topic, int [] grades)    {
		gradesArray = new int [grades.length];
		this.firstName = fn;
		this.lastName = ln;
		this.subject = topic;
		
		//stores grades in an array
		for (int i = 0; i < grades.length; i++)    {
			gradesArray[i] = grades[i];
		}//for
	}//Student
	
	public Student (String fn, String ln)    {
		this.firstName = fn;
		this.lastName = ln;
	}//Student
	
	
	
	//get methods
	
	public String getFirstName ()    {
		return this.firstName;
	}//getFirstName
	
	
	public String getLastName ()    {
		return this.lastName;
	}//getLastName
	
	
	public String getSubject ()    {
		return this.subject;
	}//getSubject
	
	
	public double  getFinalExamMark ()    {
		return this.finalExam;
	}//getFinalExamMark
	
	
	public double getFinalAverage()    {
		average = Math.round(average * 100.0) / 100.0;
		return this.average;
	}//getFinalAverage
	
	public String getLetterGrade()    {
		
		//calculates the grade of the student
		if (this.average >= 90)    {
			this.letterGrade = "A";
		} else if (this.average < 90 && this.average >=80)    {
			this.letterGrade = "B";
		} else if (this.average <= 79 && this.average >= 70)    {
			this.letterGrade = "C";
		} else if (this.average <= 69 && this.average >= 60)   {
			this.letterGrade = "D";
		} else    {
			this.letterGrade = "F";
		}//else
		
		return this.letterGrade;
	}//getLetterGrade
	
	// Method
    // Sorting in ascending order of name
    public int compareTo(Student a)    {
        if (lastName.compareToIgnoreCase(a.getLastName()) == 0 && firstName.compareToIgnoreCase(a.getFirstName()) == 0) {
        	return 0;
        } else if ((lastName.compareToIgnoreCase(a.getLastName()) > 0) || lastName.compareToIgnoreCase(a.getLastName()) == 0 && firstName.compareToIgnoreCase(a.getFirstName()) > 0) {
        	return 1;
        } else {
        	return -1;
        } //else  
    }//compareTo	
}//Student