package org.acme.sw.onboarding.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.acme.sw.onboarding.model.SymptomSpecialty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SymptomSpecialtyService {

    private static final String SYMPTOMS_DATA_PATH = "/data/symptom_specialty.json";
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorService.class);
    private final List<SymptomSpecialty> symptomSpecialties;

    private static final SymptomSpecialtyService INSTANCE = new SymptomSpecialtyService();

    public static SymptomSpecialtyService get() {
        return INSTANCE;
    }

    private SymptomSpecialtyService() {
        this.symptomSpecialties = new ArrayList<>();
        this.populate();
    }

    private void populate() {
        try {
            List<SymptomSpecialty> symptomSpecialties = new ObjectMapper().readValue(this.getClass().getResourceAsStream(SYMPTOMS_DATA_PATH), new TypeReference<>() {
            });
            this.symptomSpecialties.addAll(symptomSpecialties);
            LOGGER.info("Predefined data  from Doctors have been populated");
        } catch (IOException ex) {
            throw new IllegalStateException("Problem while populating DoctorService with JSON predefined data", ex);
        }
    }

    public List<SymptomSpecialty> getSymptomSpecialties() {
        return this.symptomSpecialties;
    }
}
