package org.acme.travels.services;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class HelloService {

    private static final Logger logger = LoggerFactory.getLogger(GreetingTravellerService.class);
    
    public Object hello(String name, Integer age) {
        logger.info("Saying hello to {} with age {}", name, age);
        
        return "";
    }
}
