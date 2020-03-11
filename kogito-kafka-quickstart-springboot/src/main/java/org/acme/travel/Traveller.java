package org.acme.travel;

public class Traveller {

	private String firstName;
	private String lastName;
	private String email;
	private String nationality;

	private boolean processed;

	public Traveller() {

	}

	public Traveller(String firstName, String lastName, String email, String nationality) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.nationality = nationality;
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

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	@Override
	public String toString() {
		return "Traveller [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", nationality="
				+ nationality + ", processed=" + processed + "]";
	}

}
