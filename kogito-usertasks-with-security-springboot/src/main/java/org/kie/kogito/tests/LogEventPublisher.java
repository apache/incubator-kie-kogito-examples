package org.kie.kogito.tests;

import java.util.Collection;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class LogEventPublisher implements EventPublisher {

    ObjectMapper json = new ObjectMapper();
    
    @Override
    public void publish(DataEvent<?> event) {
        try {
            System.out.println(json.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void publish(Collection<DataEvent<?>> events) {
        events.forEach(e -> publish(e));
    }

}
