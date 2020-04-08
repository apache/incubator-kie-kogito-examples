package org.kie.kogito.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"org.kie.kogito.**","org.acme.travels.**"})
public class KogitoSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(KogitoSpringbootApplication.class, args);
	}
	

}
