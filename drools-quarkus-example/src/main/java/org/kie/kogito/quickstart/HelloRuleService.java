package org.kie.kogito.quickstart;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import org.kie.kogito.rules.impl.SessionData;
import org.kie.kogito.rules.impl.SessionUnit;

@ApplicationScoped
public class HelloRuleService {

    @Named("simpleKS")
    SessionUnit ruleUnit;

    public String run() {

        Result result = new Result();
        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        SessionData data = new SessionData();
        data.add(result);
        data.add(mark);
        data.add(edson);
        data.add(mario);

        ruleUnit.evaluate(data);

        return result.toString();
    }
}