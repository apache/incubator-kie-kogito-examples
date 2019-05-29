package org.kie.kogito.examples;

import java.util.Random;

import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.examples.demo.Order;

@ApplicationScoped
public class CalculationService {

    private Random random = new Random();
    
    public Order calculateTotal(Order order) {        
        order.setTotal(random.nextDouble());
        
        return order;
    }
}
