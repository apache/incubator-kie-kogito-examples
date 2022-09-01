package org.kie.kogito.examples.sw.custom;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.examples.sw.custom.CalculatorClient.OperationId;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.serverless.workflow.WorkflowWorkItemHandler;


@ApplicationScoped
public class RPCCustomWorkItemHandler extends WorkflowWorkItemHandler {

    public final static String NAME = "RPCCustomWorkItemHandler";
    public final static String HOST = "host";
    public final static String PORT = "port";
    public final static String OPERATION = "operation";
    
    @Override
    protected Object internalExecute(KogitoWorkItem workItem, Map<String, Object> parameters)  {
        try {
            Iterator<?> iter = parameters.values().iterator();
            Map<String, Object> metadata = workItem.getNodeInstance().getNode().getMetaData();
            String operationId = (String) metadata.get(OPERATION);
            if (operationId == null) {
                throw new IllegalArgumentException ("Operation is a mandatory parameter");
            }
            return CalculatorClient.invokeOperation((String)metadata.getOrDefault(HOST,"localhost"), (int) metadata.getOrDefault(PORT, 8082), 
                    OperationId.valueOf(OperationId.class, operationId.toUpperCase()), (Integer)iter.next(), (Integer)iter.next());
        } catch (IOException io ) {
            throw new UncheckedIOException(io);
        }
    }
    
    @Override
    public String getName() {
        return NAME;
    }
}
