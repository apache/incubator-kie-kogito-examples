package org.acme.sw.onboarding.model;

import java.util.Date;
import java.util.Objects;

public class Appointment {

    private String doctorId;
    private String patientId;

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "doctorId='" + doctorId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", date=" + date +
                '}';
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Appointment that = (Appointment) o;
        return Objects.equals(doctorId, that.doctorId) &&
                Objects.equals(patientId, that.patientId) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId, patientId, date);
    }
}
