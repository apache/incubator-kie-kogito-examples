package org.kie.kogito.examples.hr;

public class Employee implements java.io.Serializable {

  static final long serialVersionUID = 1L;

	private java.lang.String firstName;
	private java.lang.String lastName;
	private java.lang.String personalId;
	private java.util.Date birthDate;

	private org.kie.kogito.examples.hr.Address address;

	public Employee() {
	}

	public java.lang.String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(java.lang.String firstName) {
		this.firstName = firstName;
	}

	public java.lang.String getLastName() {
		return this.lastName;
	}

	public void setLastName(java.lang.String lastName) {
		this.lastName = lastName;
	}

	public java.lang.String getPersonalId() {
		return this.personalId;
	}

	public void setPersonalId(java.lang.String personalId) {
		this.personalId = personalId;
	}

	public java.util.Date getBirthDate() {
		return this.birthDate;
	}

	public void setBirthDate(java.util.Date birthDate) {
		this.birthDate = birthDate;
	}

	public org.kie.kogito.examples.hr.Address getAddress() {
		return this.address;
	}

	public void setAddress(org.kie.kogito.examples.hr.Address address) {
		this.address = address;
	}

	private java.lang.String manager;

    public java.lang.String getManager() {
        return manager;
    }

    public void setManager(java.lang.String manager) {
        this.manager = manager;
    }

    private java.lang.String department;

    public java.lang.String getDepartment() {
        return department;
    }

    public void setDepartment(java.lang.String department) {
        this.department = department;
    }

	public Employee(java.lang.String firstName, java.lang.String lastName,
			java.lang.String personalId, java.util.Date birthDate,
			org.kie.kogito.examples.hr.Address address) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.personalId = personalId;
		this.birthDate = birthDate;
		this.address = address;
	}
}