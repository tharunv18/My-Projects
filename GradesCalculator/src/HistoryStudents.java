public class HistoryStudents extends Student    {
	private double attendance = 0;
	private double project = 0;
	private double midterm = 0;
	
	//default constructor
	public HistoryStudents ()    {
		super();
		this.attendance = 0;
		this.project = 0;
		this.midterm = 0;
	}//MathStudents
	
	//initializes a new history student
	public HistoryStudents (String fn, String ln, int [] grades)    {
		super (fn, ln);
		this.attendance = grades[1];
		this.project = grades[2];
		this.midterm = grades[3];
		this.finalExam = grades[4];
		this.subject = "History";
		this.average = (attendance/100) * 10 + (project/100) * 30 + (midterm/100) * 30 + (finalExam/100) * 30;
	}//historyStudents - constructor
}//historyStudents - Class