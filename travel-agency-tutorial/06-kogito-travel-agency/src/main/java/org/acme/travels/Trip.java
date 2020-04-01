package org.acme.travels;

import java.util.Date;

public class Trip {

	private String city;
	private String country;
	private Date begin;
	private Date end;
	private boolean visaRequired;
	
	public Trip() {
		
	}

	public Trip(String city, String country, Date begin, Date end) {
		super();
		this.city = city;
		this.country = country;
		this.begin = begin;
		this.end = end;
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

	public Date getBegin() {
		return begin;
	}

	public void setBegin(Date begin) {
		this.begin = begin;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public boolean isVisaRequired() {
		return visaRequired;
	}

	public void setVisaRequired(boolean visaRequired) {
		this.visaRequired = visaRequired;
	}

	@Override
	public String toString() {
		return "Trip [city=" + city + ", country=" + country + ", begin=" + begin + ", end=" + end + ", visaRequired="
				+ visaRequired + "]";
	}

}
