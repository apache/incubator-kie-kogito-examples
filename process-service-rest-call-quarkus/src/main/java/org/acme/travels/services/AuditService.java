package org.acme.travels.services;

import javax.enterprise.context.ApplicationScoped;

import org.acme.travels.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    
    public User auditUser(User user) {
     
        logger.info("User {} is being processed", user.toString());
        
        return user;
    }
}
