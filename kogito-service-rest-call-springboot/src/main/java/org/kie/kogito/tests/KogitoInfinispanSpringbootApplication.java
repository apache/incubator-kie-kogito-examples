package org.kie.kogito.tests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages={"org.kie.kogito.**","org.acme.travels.**"})
public class KogitoInfinispanSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(KogitoInfinispanSpringbootApplication.class, args);
	}
	

	@Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
