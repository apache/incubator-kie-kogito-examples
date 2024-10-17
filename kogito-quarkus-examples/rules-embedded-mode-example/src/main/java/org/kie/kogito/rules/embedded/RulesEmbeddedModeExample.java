package org.kie.kogito.rules.embedded;

import java.util.Arrays;

import org.drools.commands.SetActiveAgendaGroup;
import org.drools.core.event.DebugRuleRuntimeEventListener;
import org.kie.api.KieServices;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RulesEmbeddedModeExample {

        private static final Logger logger = LoggerFactory.getLogger(RulesEmbeddedModeExample.class);


    public static void main(String[] args) {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();

        logger.info("-----> Now we execute rules in the stateful session <-----");

        KieSession kieSession = kieContainer.newKieSession();
        kieSession.addEventListener(new DebugRuleRuntimeEventListener());

        ExecutionResults executionResults = kieSession.execute(
            CommandFactory.newBatchExecution(Arrays.asList(
                CommandFactory.newInsert(new Applicant("#0001", 20), "applicant"),
                CommandFactory.newInsert(new LoanApplication("#0001"), "application"),
                new SetActiveAgendaGroup("applicationGroup"),
                CommandFactory.newFireAllRules()
            ))
        );

        logger.info("application: " + executionResults.getResults().get("application"));

        kieSession.dispose();

        logger.info("-----> Now we execute rules in the stateless session <-----");

        StatelessKieSession statelessKieSession = kieContainer.newStatelessKieSession();
        statelessKieSession.addEventListener(new DebugRuleRuntimeEventListener());

        ExecutionResults statelessExecutionResults = statelessKieSession.execute(
            CommandFactory.newBatchExecution(Arrays.asList(
                CommandFactory.newInsert(new Applicant("#0001", 20), "applicant"),
                CommandFactory.newInsert(new LoanApplication("#0001"), "application"),
                new SetActiveAgendaGroup("applicationGroup")            ))
        );

        logger.info("application: " + statelessExecutionResults.getResults().get("application"));
    }
}
