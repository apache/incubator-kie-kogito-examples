package org.acme.travels;

public class VisaApplication {

	private String firstName;
	private String lastName;
	private String city;
	private String country;
	private int duration;
	private String passportNumber;
	private String nationality;
	
	private boolean approved;

	public VisaApplication() {

	}

	public VisaApplication(String firstName, String lastName, String city, String country, int duration,
			String passportNumber) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.city = city;
		this.country = country;
		this.duration = duration;
		this.passportNumber = passportNumber;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getPassportNumber() {
		return passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	@Override
	public String toString() {
		return "VisaApplication [firstName=" + firstName + ", lastName=" + lastName + ", city=" + city + ", country="
				+ country + ", duration=" + duration + ", passportNumber=" + passportNumber + ", approved=" + approved
				+ "]";
	}

}
