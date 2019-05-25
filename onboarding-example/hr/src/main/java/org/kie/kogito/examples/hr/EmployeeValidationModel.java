package org.kie.kogito.examples.hr;

public class EmployeeValidationModel {
   
    private org.kie.kogito.examples.hr.Employee employee;

    public org.kie.kogito.examples.hr.Employee getEmployee() {
        return employee;
    }

    public void setEmployee(org.kie.kogito.examples.hr.Employee employee) {
        this.employee = employee;
    }

    private java.lang.String status;

    public java.lang.String getStatus() {
        return status;
    }

    public void setStatus(java.lang.String status) {
        this.status = status;
    }

    private java.lang.String message;

    public java.lang.String getMessage() {
        return message;
    }

    public void setMessage(java.lang.String message) {
        this.message = message;
    }

    private java.lang.Boolean exists;

    public java.lang.Boolean getExists() {
        return exists;
    }

    public void setExists(java.lang.Boolean exists) {
        this.exists = exists;
    }
}
