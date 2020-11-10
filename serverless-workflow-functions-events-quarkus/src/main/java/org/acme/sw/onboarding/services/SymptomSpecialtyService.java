package org.acme.sw.onboarding.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.acme.sw.onboarding.model.SymptomSpecialty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class SymptomSpecialtyService {

    private static final String SYMPTOMS_DATA_PATH = "data/symptom_specialty.json";
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorService.class);
    private final List<SymptomSpecialty> symptomSpecialties;

    @Inject
    ObjectMapper mapper;

    public SymptomSpecialtyService() {
        this.symptomSpecialties = new ArrayList<>();
        this.populate();
    }

    private void populate() {
        try {
            List<SymptomSpecialty> symptomSpecialties = mapper.readValue(this.getClass().getResourceAsStream(SYMPTOMS_DATA_PATH), new TypeReference<>() {
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
