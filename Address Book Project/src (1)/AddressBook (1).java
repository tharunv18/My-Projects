import java.util.ArrayList;
import java.util.Collections; 

public class AddressBook {

	private ArrayList <OneAddress> addressBook = new ArrayList<OneAddress>();
	
	public void addAddress(String lastName, String firstName,String address, String address2,String city, String province,String country, String postalCode) {
		this.addressBook.add(new OneAddress (lastName, firstName, address, address2, city, province, country,postalCode));
	}

	public void removeAddress (String fn, String ln) {
		for (int i = 0; i  < this.addressBook.size(); i++) {
			if (fn == (this.addressBook.get(i).getFirstName())  && ln == (this.addressBook.get(i).getLastName())) {
			this.addressBook.remove(i);
			} //if
		}//for
	} //removeAddress

	public int locationInBook (String fn, String ln) {
	int location = 0;
		for (int i = 0; i  < this.addressBook.size(); i++) {
			if (fn == (this.addressBook.get(i).getFirstName())  && ln == (this.addressBook.get(i).getLastName())) {
			location = i;
			} //if
		}//for
	return location;
	} //locationInBook

	public void printAddress (String fn, String ln) {
		for (int i = 0; i  < this.addressBook.size(); i++) {
			if (fn == (this.addressBook.get(i).getFirstName())  && ln == (this.addressBook.get(i).getLastName())) {
				System.out.println(this.addressBook.get(i));
			} //if
		}//for
	}//toString
	
	private void sort () {
		Collections.sort(this.addressBook);
	}
	
	
	
	public void printAll () {
		sort();
		
		for (int i = 0; i < this.addressBook.size(); i++) {
			System.out.println(this.addressBook.get(i));
		} //for
		
	}//printAll
	
	public void printTable () {
		String output = "";
		String title = "";
		sort();
		
		title = String.format("|%-20s|", "First Name") + String.format("|%-20s|", "Last Name") + String.format("|%-20s|", "Country") + "\n";
		output += title;
		
		for (int i = 0; i < title.length(); i++) {
			output+="-";
		}
		output += "\n";
		String.format("|%-20s|", output);
		
		for (int i = 0; i < this.addressBook.size(); i++) {
			output += String.format("|%-20s|", this.addressBook.get(i).getFirstName()) + String.format("|%-20s|", this.addressBook.get(i).getLastName()) + String.format("|%-20s|", this.addressBook.get(i).getCountry()) + "\n";
		}
		
		for (int i = 0; i < this.addressBook.size(); i++) {
		String.format("|%20s|", this.addressBook.get(i));
		}
		System.out.println(output);
	}//printTable


} //AddressBook
