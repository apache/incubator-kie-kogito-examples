package org.kie.kogito.serverless.examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class ClassifierServiceController {
    @Autowired
    ClassifierService classifierService;

    @GetMapping("/countryclassifier")
    public Set<Country> getClassifiedCountries() {
        return classifierService.getClassifiedCountries();
    }

}
