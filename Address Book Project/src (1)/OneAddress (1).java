
public class OneAddress implements Comparable<OneAddress> {
	private String lastName = "";
	private String firstName = "";
	private String streetAddress = "";
	private String secondAddress = "";
	private String city = "";
	private String province = "";
	private String country = "";
	private String postalCode = "";
	
	
	public OneAddress () {
		this.firstName = "";
		this.lastName = "";
		this.streetAddress = "";
		this.secondAddress = "";
		this.province = "";
		this.city = "";
		this.country = "";
		this.postalCode = "";
	}

	public OneAddress(String ln, String fn, String street, String address2, String prov, String town, String nation, String code){
		this.firstName = fn;
		this.lastName = ln;
		this.streetAddress = street;
		this.secondAddress = address2;
		this.province = prov;
		this.city = town;
		this.country = nation;
		this.postalCode = code;
		
	}
	public String getAddress() {
		return this.streetAddress;
	}
	public String getFirstName() {
		return this.firstName;
	}
	public String getLastName() {
		return this.lastName;
	}
	public String getCountry() {
		return this.country;
	}
	public String getCity() {
		return this.streetAddress;
	}
	public String getPostalCode(String q) {
		return this.postalCode;
	}
	
	public void setAddress(String q) {
		 this.streetAddress = q;
	}
	public void setFirstName(String q) {
		 this.firstName = q;
	}
	public void setLastName(String q) {
		 this.lastName = q;
	}
	public void setCountry(String q) {
		 this.country = q;
	}
	public void setCity(String q) {
		 this.streetAddress = q;
	}
	public void setPostalCode(String q) {
		 this.postalCode = q;
	}
	
	public String toString () {
		String label = "";
		label += this.firstName + " " + this.lastName + "\n";
		if (secondAddress != null) {
		label += this.streetAddress + ", " + this.secondAddress + "\n";
		}else {
		   label += this.streetAddress + "\n";
		}
		label += this.city + ", " + this.province + "\n";
		label +=this.postalCode;
		
		
		
		
		return label;
	}
	
		 
	    // Method
	    // Sorting in ascending order of name
	    public int compareTo(OneAddress a)    {
	        if (lastName.compareToIgnoreCase(a.getLastName()) == 0 && firstName.compareToIgnoreCase(a.getFirstName()) == 0) {
	        	return 0;
	        }else if ((lastName.compareToIgnoreCase(a.getLastName()) > 0) || lastName.compareToIgnoreCase(a.getLastName()) == 0 && firstName.compareToIgnoreCase(a.getFirstName()) > 0) {
	        	return 1;
	        } else {
	        	return -1;
	        } //else
	    }//compareTo
	
	
}//oneAddress

