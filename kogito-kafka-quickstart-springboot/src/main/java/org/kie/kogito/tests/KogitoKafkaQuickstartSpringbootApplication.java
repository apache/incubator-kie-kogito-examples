package org.kie.kogito.tests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"org.kie.kogito.**","org.kie.kogito.tests.**", "org.drools.project.model.**"})
public class KogitoKafkaQuickstartSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(KogitoKafkaQuickstartSpringbootApplication.class, args);
	}

}
