package org.kie.kogito.tests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"org.kie.kogito.**","org.acme.travels.**"})
public class KogitoInfinispanSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(KogitoInfinispanSpringbootApplication.class, args);
	}
	

}
