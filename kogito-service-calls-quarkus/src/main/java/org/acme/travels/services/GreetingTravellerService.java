package org.acme.travels.services;

import javax.enterprise.context.ApplicationScoped;

import org.acme.travels.Traveller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class GreetingTravellerService {

    private static final Logger logger = LoggerFactory.getLogger(GreetingTravellerService.class);
    
    public Traveller greetTraveller(Traveller traveller) {
        logger.info("Sending greeting email to {} on email address {}", traveller.getLastName() +", " + traveller.getFirstName(), traveller.getEmail());
        
        return traveller;
    }
}
