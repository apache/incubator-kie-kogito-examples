package org.acme.sw.onboarding.model;

import java.util.List;
import java.util.Objects;

public class SymptomSpecialty {

    public List<String> symptoms;
    public String specialty;

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SymptomSpecialty that = (SymptomSpecialty) o;
        return Objects.equals(symptoms, that.symptoms) &&
                Objects.equals(specialty, that.specialty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symptoms, specialty);
    }

    @Override
    public String toString() {
        return "SymptomSpecialty{" +
                "symptoms=" + symptoms +
                ", specialty='" + specialty + '\'' +
                '}';
    }
}
