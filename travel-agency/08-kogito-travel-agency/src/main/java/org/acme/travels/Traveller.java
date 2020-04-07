package org.acme.travels;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Traveller {

	@NotBlank(message = "First name must be provided")
	@Size(min = 2, max = 30)	
	private String firstName;

	@NotBlank(message = "Last name must be provided.")
	@Size(min = 2, max = 30)	
	private String lastName;

	@NotNull(message = "Email address must be provided.")
	@Email
	private String email;

	@NotBlank(message = "Nationality can not be blank.")
	private String nationality;

	@NotNull(message = "Address can not be null.")
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
