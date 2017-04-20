package unl.cse; 

public class Person {
	
	protected String code;
	protected String firstName;
	protected String lastName;
	protected Address address;
	protected MyList<String> emails;
	
	public Person(String code, String lastName, String firstName, Address address, MyList<String> emails) {
		super();
		this.code = code;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.emails = emails;
	}

	public String getCode() {
		return code;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Address getAddress() {
		return address;
	}

	public MyList<String> getEmails() {
		return emails;
	}
	
	public String getNameStringLF(){
		return this.lastName + ", " + this.firstName;
	}

}
