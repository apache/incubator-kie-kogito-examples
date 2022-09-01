package org.kie.kogito.examples.sw.services;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.kie.kogito.serverless.workflow.functions.WorkItemFunctionNamespace;
import org.kie.kogito.serverless.workflow.parser.ParserContext;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionRef;

import static org.kie.kogito.examples.sw.custom.RPCCustomWorkItemHandler.NAME;
import static org.kie.kogito.examples.sw.custom.RPCCustomWorkItemHandler.OPERATION;
import static org.kie.kogito.serverless.workflow.parser.FunctionNamespaceFactory.getFunctionName;

public class RPCCustomFunctionNamespace extends WorkItemFunctionNamespace{

    @Override
    public String namespace() {
        return "rpc";
    }

    @Override
    protected <T extends RuleFlowNodeContainerFactory<T, ?>> WorkItemNodeFactory<T> fillWorkItemHandler(Workflow workflow,
                                                                                                        ParserContext context,
                                                                                                        WorkItemNodeFactory<T> node,
                                                                                                        FunctionRef functionRef) {
        return node.workName(NAME).metaData(OPERATION,  getFunctionName(functionRef));
    }
}
