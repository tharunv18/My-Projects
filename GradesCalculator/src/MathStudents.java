public class MathStudents extends Student    {
	private double quizAverage = 0;
	private double test1 = 0;
	private double test2 = 0;
	
	//default constructor
	public MathStudents ()    {
		super();
		this.quizAverage = 0;
		this.test1 = 0;
		this.test2 = 0;
	}//MathStudents
	
	//initializes a new math student
	public MathStudents (String fn, String ln, int [] grades)    {
		super (fn, ln);
		this.quizAverage = (grades[1] + grades[2] + grades[3] + grades[4] + grades[5])/5.0;
		this.test1 = grades[6];
		this.test2 = grades[7];
		this.finalExam = grades[8];
		this.subject = "Math";
		this.average = (quizAverage/100.0) * 15 + (test1/100.0) * 25 + (test2/100.0) * 25 + (finalExam/100.0)*35;
	}//MathStudents - constructor
}//MathStudents - class