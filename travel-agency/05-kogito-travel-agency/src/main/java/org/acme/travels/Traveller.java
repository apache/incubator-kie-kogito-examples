package org.acme.travels;

public class Traveller {

	private String firstName;
	private String lastName;
	private String email;
	private String nationality;
	private Address address;

	public Traveller() {

	}

	public Traveller(String firstName, String lastName, String email, String nationality, Address address) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.nationality = nationality;
		this.address = address;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "Traveller [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", nationality="
				+ nationality + ", address=" + address + "]";
	}

}
