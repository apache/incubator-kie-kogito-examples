package org.kie.kogito.quickstart;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.drools.modelcompiler.KieRuntimeBuilder;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

@ApplicationScoped
public class HelloRuleService {

    private KieSession ksession;

    HelloRuleService() { }

    @Inject
    HelloRuleService( KieRuntimeBuilder runtimeBuilder ) {
        ksession = runtimeBuilder.newKieSession();
    }

    public String run() {

        Result result = new Result();
        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        ksession.insert(result);
        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);

        ksession.fireAllRules();

        return result.toString();
    }
}