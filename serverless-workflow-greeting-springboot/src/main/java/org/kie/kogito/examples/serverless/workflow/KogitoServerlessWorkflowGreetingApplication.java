package org.kie.kogito.examples.serverless.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"org.kie.kogito.**"})
public class KogitoServerlessWorkflowGreetingApplication {
    public static void main(String[] args) {
        SpringApplication.run(KogitoServerlessWorkflowGreetingApplication.class, args);
    }
}
