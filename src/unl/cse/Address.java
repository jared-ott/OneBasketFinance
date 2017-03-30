package unl.cse;

public class Address {
	private String streetAddress;
	private String city;
	private String state;
	private String zip;
	private String country;
	
	public Address(String streetAddress, String city, String state, String zip, String country) {
		super();
		this.streetAddress = streetAddress;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.country = country;
	}
	
	public String getStreetAddress() {
		return streetAddress;
	}
	public String getCity() {
		return city;
	}
	public String getState() {
		return state;
	}
	public String getZip() {
		return zip;
	}
	public String getCountry() {
		return country;
	}
	
}
