package org.submarine.payroll;

/**
 * This class was automatically generated by the data modeler tool.
 */

public class Address implements java.io.Serializable {

static final long serialVersionUID = 1L;

	private java.lang.String street;
	private java.lang.String city;
	private java.lang.String zipCode;
	private java.lang.String country;

	public Address() {
	}

	public java.lang.String getStreet() {
		return this.street;
	}

	public void setStreet(java.lang.String street) {
		this.street = street;
	}

	public java.lang.String getCity() {
		return this.city;
	}

	public void setCity(java.lang.String city) {
		this.city = city;
	}

	public java.lang.String getZipCode() {
		return this.zipCode;
	}

	public void setZipCode(java.lang.String zipCode) {
		this.zipCode = zipCode;
	}

	public java.lang.String getCountry() {
		return this.country;
	}

	public void setCountry(java.lang.String country) {
		this.country = country;
	}

	public Address(java.lang.String street, java.lang.String city,
			java.lang.String zipCode, java.lang.String country) {
		this.street = street;
		this.city = city;
		this.zipCode = zipCode;
		this.country = country;
	}
}