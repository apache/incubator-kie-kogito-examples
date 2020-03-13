package org.acme.travels.services;

import org.acme.travels.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class UserService {

    @Autowired
    private RestTemplate restTemplate;
    
    public User get(String username) {
        
        try {
            return restTemplate.getForObject("https://petstore.swagger.io/v2/user/{username}", User.class, username);
        } catch (RestClientException e) {
            return null;
        }
    }
}
