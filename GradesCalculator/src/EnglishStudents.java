public class EnglishStudents extends Student    {
	private double termPaperMark = 0;
	private double midterm = 0;
	
	//default constructor
		public EnglishStudents ()    {
			super();
			this.termPaperMark = 0;
			this.midterm = 0;
		}//MathStudents
	
	//initializes a new English student
	public EnglishStudents (String fn, String ln, int [] grades)    {
		super (fn, ln);
		this.termPaperMark = grades[1];
		this.midterm = grades[2];
		this.finalExam = grades[3];
		this.subject = "English";
		this.average = (termPaperMark/100) * 25 + (midterm/100) * 35 + (finalExam/100)*40;
	}//EnglishStudents - constructor
}//EnglishStudents- class