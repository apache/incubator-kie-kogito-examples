package org.acme.sw.onboarding.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE("male"),
    FEMALE("female");

    private final String key;

    Gender(final String key) {
        this.key = key;
    }

    @JsonCreator
    public static Gender fromString(final String key) {
        if (key != null) {
            return Gender.valueOf(key);
        }
        return null;
    }

    @JsonValue
    public String getKey() {
        return this.key;
    }
}
