package org.acme.travels.services;

import org.acme.travels.Traveller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GreetingTravellerService {

    private static final Logger logger = LoggerFactory.getLogger(GreetingTravellerService.class);
    
    public Traveller greetTraveller(Traveller traveller) {
        logger.info("Sending greeting email to {} on email address {}", traveller.getLastName() +", " + traveller.getFirstName(), traveller.getEmail());

        return traveller;
    }
}
