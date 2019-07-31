package org.kie.kogito.rules.alerting;

import java.util.ArrayList;
import java.util.List;

public class Logger {
    List<String> messages = new ArrayList<>();

    public void log(String message) {
        messages.add(message);
    }
    public int size() {
        return messages.size();
    }
}
