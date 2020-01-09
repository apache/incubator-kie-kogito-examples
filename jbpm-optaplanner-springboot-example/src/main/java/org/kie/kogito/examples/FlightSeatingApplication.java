package org.kie.kogito.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"org.kie.kogito.**"})
public class FlightSeatingApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightSeatingApplication.class, args);
	}

}
