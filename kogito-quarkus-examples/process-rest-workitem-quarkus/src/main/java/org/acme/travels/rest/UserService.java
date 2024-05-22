package org.acme.travels.rest;

import org.acme.travels.User;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserService {

    public User getUser(String name) {
        if ("test".equals(name)) {
            User user = new User();
            user.setLastName(name);
            return user;
        }
        return null;
    }
}
