package org.acme.travels.services;

import org.acme.travels.Traveller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TravellerAuditService {

    private static final Logger logger = LoggerFactory.getLogger(TravellerAuditService.class);
    
    public Traveller auditTraveller(Traveller traveller) {
     
        logger.info("Traveller {} is being processed", traveller.toString());
        
        return traveller;
    }
}
