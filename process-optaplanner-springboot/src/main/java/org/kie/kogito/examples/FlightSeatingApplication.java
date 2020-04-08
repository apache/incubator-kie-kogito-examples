package org.kie.kogito.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.kie.kogito.**"})
public class FlightSeatingApplication {

    public static void main(String[] args) {
        // Spring-boot dev tools class loader cannot detect generated sources
        // so we need to disable restart if we want to take advantage of live-reload
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(FlightSeatingApplication.class, args);
    }
}
