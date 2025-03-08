
public class AddressBookTestCases {

	 static void addToBook(AddressBook book) {
	        book.addAddress("Vader", "Darth", "Sith Lord Office", null,
	                        "The Death Star", "Galaxy", "Universe", "FFA");
	        book.addAddress("Wong", "George", "308-3221 Village Green Way", null,
	                        "Squamish", "BC", "Canada", "V8B 0R7");
	        book.addAddress("Knop", "Rob", "University of Victoria", "3200 Ring Rd.",
	                         "Victoria", "BC", "Canada", "V8N 1M5");
	        book.addAddress("Marvin", "Neal", "Costco", "799 McCallum Rd.",
	                        "Langford", "BC", "Canada", "V9B 6A2");
	        book.addAddress("Chakrabarty", "Deepto", "Target", "3201 Nw Randall Way",
	                        "Silverdale", "WA", "USA", "98383");
	        book.addAddress("Dog", "Scottie", "Carnegie Mellon University",
	                        "5000 Forbes Ave", "Pittsburgh", "PA", "USA", "15213");
	    }//addToBook
	    
	    public static void main(String args[]) {
	        AddressBook book = new AddressBook();
	        addToBook(book);
	        
	        book.addAddress("Secondary", "Mount Douglas", "3970 Gordon Head Rd", "", "Victoria", "BC", "Canada", "V8N 3X3");
	        book.printTable();
	        book.removeAddress("Darth", "Vader");
	        book.printTable();
	        book.printAddress("Neal", "Marvin");
	        book.printAll();
	    
	    }//main
	    
	}//TestCases

