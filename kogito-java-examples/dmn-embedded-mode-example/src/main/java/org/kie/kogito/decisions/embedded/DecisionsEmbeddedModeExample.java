package org.kie.kogito.decisions.embedded;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecisionsEmbeddedModeExample {

    private static final Logger logger = LoggerFactory.getLogger(DecisionsEmbeddedModeExample.class);

    public static void main(String[] args) {

        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();

        logger.info("-----> Now we execute DMN <-----");

        DMNRuntime dmnRuntime = KieRuntimeFactory.of(kieContainer.getKieBase()).get(DMNRuntime.class);

        String namespace = "https://kie.org/dmn/_C83DFD16-A42A-46BE-A843-370444580E0F";
        String modelName = "loan-application-age-limit";

        DMNModel dmnModel = dmnRuntime.getModel(namespace, modelName);

        DMNContext dmnContext = dmnRuntime.newContext();  
        dmnContext.set("Applicant", new Applicant("#0001", 20));  
        dmnContext.set("Application", new LoanApplication("#0001"));  
        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, dmnContext);  

        for (DMNDecisionResult dr : dmnResult.getDecisionResults()) {  
            logger.info(
                "Decision: '" + dr.getDecisionName() + "', " +
                "Result: " + dr.getResult());        
         }
    }
}
