package org.acme.travels.services;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.acme.travels.User;
import org.acme.travels.rest.UsersRemoteService;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class UserService {
    
    @Inject
    @RestClient
    UsersRemoteService usersRemoteService;

    @Fallback(fallbackMethod = "missingUser")
    public User get(String username) {
        return usersRemoteService.get(username);
    }
    
    public User missingUser(String username) {
        return null;
    }
}
