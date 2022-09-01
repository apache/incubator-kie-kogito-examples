package org.kie.kogito.examples.sw.services;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.types.WorkItemTypeHandler;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;

import static org.kie.kogito.examples.sw.custom.RPCCustomWorkItemHandler.NAME;
import static org.kie.kogito.examples.sw.custom.RPCCustomWorkItemHandler.OPERATION;
import static org.kie.kogito.serverless.workflow.parser.FunctionTypeHandlerFactory.trimCustomOperation;

public class RPCCustomTypeHandler extends WorkItemTypeHandler{


    @Override
    public String type() {
        return "rpc";
    }

    @Override
    protected <T extends RuleFlowNodeContainerFactory<T, ?>> WorkItemNodeFactory<T> fillWorkItemHandler(Workflow workflow,
                                                                                                        ParserContext context,
                                                                                                        WorkItemNodeFactory<T> node,
                                                                                                        FunctionDefinition functionDef) {
        return node.workName(NAME).metaData(OPERATION, trimCustomOperation(functionDef));
    }
}
