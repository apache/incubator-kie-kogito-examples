package org.kie.dmn.kogito.springboot.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"org.kie.dmn.kogito.**", "org.kie.kogito.app.**"})
public class KogitoSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(KogitoSpringbootApplication.class, args);
	}
}
