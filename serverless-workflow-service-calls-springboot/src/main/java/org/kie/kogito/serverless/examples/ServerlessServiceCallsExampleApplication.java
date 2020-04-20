package org.kie.kogito.serverless.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = {"org.kie.kogito.**"})
public class ServerlessServiceCallsExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerlessServiceCallsExampleApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
