package org.acme.travels.services;

import java.util.HashMap;
import java.util.Map;

import org.acme.travels.Traveller;
import org.springframework.stereotype.Component;

@Component
public class StoreTravellerService {

    private Map<String, Traveller> store = new HashMap<>();
    
    
    public boolean storeTraveller(Traveller traveller) {
        Traveller stored = store.putIfAbsent(traveller.getEmail(), traveller);
        
        if (stored == null) {
            return true;
        }
        
        return false;
    }
}
