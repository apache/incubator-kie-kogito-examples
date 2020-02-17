package org.kie.kogito.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"org.kie.kogito.**"})
public class KogitoSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(KogitoSpringbootApplication.class, args);
	}

}
