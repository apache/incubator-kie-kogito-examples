package com.myspace.onboarding;

import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.drools.core.util.KieFunctions;

@javax.inject.Singleton()
@javax.inject.Named("onboarding.onboarding")
public class OnboardingProcess extends org.kie.kogito.process.impl.AbstractProcess<com.myspace.onboarding.OnboardingModel> {

    com.myspace.Application app;

    public OnboardingProcess() {
        this(new com.myspace.Application());
    }

    public OnboardingProcess(com.myspace.Application app) {
        super(app.config().process());
        this.app = app;
    }

    public com.myspace.onboarding.OnboardingProcessInstance createInstance(com.myspace.onboarding.OnboardingModel value) {
        return new com.myspace.onboarding.OnboardingProcessInstance(this, value, this.createLegacyProcessRuntime());
    }

    protected org.kie.api.definition.process.Process legacyProcess() {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("onboarding.onboarding");
        factory.variable("employee", new ObjectDataType("com.myspace.onboarding.Employee"));
        factory.variable("payroll", new ObjectDataType("com.myspace.onboarding.Payroll"));
        factory.variable("employeeId", new ObjectDataType("java.lang.String"));
        factory.variable("email", new ObjectDataType("java.lang.String"));
        factory.variable("status", new ObjectDataType("java.lang.String"));
        factory.variable("message", new ObjectDataType("java.lang.String"));
        factory.variable("manager", new ObjectDataType("java.lang.String"));
        factory.variable("department", new ObjectDataType("java.lang.String"));
        factory.name("onboarding");
        factory.packageName("com.myspace.onboarding");
        factory.dynamic(false);
        factory.version("1.0");
        factory.visibility("Public");
        factory.metaData("TargetNamespace", "http://www.omg.org/bpmn20");
        org.jbpm.ruleflow.core.factory.SplitFactory splitNode1 = factory.splitNode(1);
        splitNode1.name("Split");
        splitNode1.type(1);
        splitNode1.metaData("UniqueId", "_EEA9F9A8-BC9D-4DC3-B89A-28F79A0C6A48");
        splitNode1.metaData("x", 801);
        splitNode1.metaData("width", 56);
        splitNode1.metaData("y", 232);
        splitNode1.metaData("height", 56);
        splitNode1.done();
        org.jbpm.ruleflow.core.factory.StartNodeFactory startNode2 = factory.startNode(2);
        startNode2.name("Start");
        startNode2.done();
        org.jbpm.ruleflow.core.factory.HumanTaskNodeFactory humanTaskNode3 = factory.humanTaskNode(3);
        humanTaskNode3.name("Manager confirmation");
        humanTaskNode3.workParameter("TaskName", "ManagerConfirmation");
        humanTaskNode3.workParameter("NodeName", "Manager confirmation");
        humanTaskNode3.workParameter("Skippable", "false");
        humanTaskNode3.workParameter("ActorId", "manager");
        humanTaskNode3.inMapping("depeartment", "department");
        humanTaskNode3.inMapping("employeeId", "employeeId");
        humanTaskNode3.inMapping("payroll", "payroll");
        humanTaskNode3.inMapping("employee", "employee");
        humanTaskNode3.inMapping("email", "email");
        humanTaskNode3.outMapping("message", "message");
        humanTaskNode3.outMapping("status", "status");
        humanTaskNode3.done();
        org.jbpm.ruleflow.core.factory.SplitFactory splitNode4 = factory.splitNode(4);
        splitNode4.name("Split");
        splitNode4.type(2);
        splitNode4.metaData("UniqueId", "_32221BB1-BAE5-4431-8003-3FEF0E04D9C7");
        splitNode4.metaData("x", 431);
        splitNode4.metaData("width", 56);
        splitNode4.metaData("y", 232);
        splitNode4.metaData("Default", "_A65F4850-A93F-4703-B5AA-2EF5F649BC86");
        splitNode4.metaData("height", 56);
        splitNode4.constraint(10, "_5F4F4E2D-B4EC-4542-91DF-69FC30C5E346", "DROOLS_DEFAULT", "java", kcontext -> {
            com.myspace.onboarding.Employee employee = (com.myspace.onboarding.Employee) kcontext.getVariable("employee");
            com.myspace.onboarding.Payroll payroll = (com.myspace.onboarding.Payroll) kcontext.getVariable("payroll");
            java.lang.String employeeId = (java.lang.String) kcontext.getVariable("employeeId");
            java.lang.String email = (java.lang.String) kcontext.getVariable("email");
            java.lang.String status = (java.lang.String) kcontext.getVariable("status");
            java.lang.String message = (java.lang.String) kcontext.getVariable("message");
            java.lang.String manager = (java.lang.String) kcontext.getVariable("manager");
            java.lang.String department = (java.lang.String) kcontext.getVariable("department");
            {
                return KieFunctions.equalsTo(status, "exists");
            }
        }, 0);
        splitNode4.constraint(11, "_A65F4850-A93F-4703-B5AA-2EF5F649BC86", "DROOLS_DEFAULT", "java", kcontext -> {
            com.myspace.onboarding.Employee employee = (com.myspace.onboarding.Employee) kcontext.getVariable("employee");
            com.myspace.onboarding.Payroll payroll = (com.myspace.onboarding.Payroll) kcontext.getVariable("payroll");
            java.lang.String employeeId = (java.lang.String) kcontext.getVariable("employeeId");
            java.lang.String email = (java.lang.String) kcontext.getVariable("email");
            java.lang.String status = (java.lang.String) kcontext.getVariable("status");
            java.lang.String message = (java.lang.String) kcontext.getVariable("message");
            java.lang.String manager = (java.lang.String) kcontext.getVariable("manager");
            java.lang.String department = (java.lang.String) kcontext.getVariable("department");
            {
                return KieFunctions.equalsTo(status, "new");
            }
        }, 0);
        splitNode4.done();
        org.jbpm.ruleflow.core.factory.SubProcessNodeFactory subProcessNode5 = factory.subProcessNode(5);
        subProcessNode5.name("Setup HR");
        subProcessNode5.processId("onboarding.setupHR");
        subProcessNode5.processName("");
        subProcessNode5.waitForCompletion(true);
        subProcessNode5.independent(false);
        subProcessNode5.subProcessFactory(new org.jbpm.workflow.core.node.SubProcessFactory<SetupHRModel>() {

            public SetupHRModel bind(org.kie.api.runtime.process.ProcessContext kcontext) {
                com.myspace.onboarding.SetupHRModel model = new com.myspace.onboarding.SetupHRModel();
                com.myspace.onboarding.Employee employee = (com.myspace.onboarding.Employee) kcontext.getVariable("employee");
                model.setEmployee((com.myspace.onboarding.Employee) (employee));
                return model;
            }

            public org.kie.kogito.process.ProcessInstance<SetupHRModel> createInstance(SetupHRModel model) {
                return app.createSetupHRProcess().createInstance(model);
            }

            public void unbind(org.kie.api.runtime.process.ProcessContext kcontext, SetupHRModel model) {
                java.lang.String manager = (java.lang.String) model.getManager();
                kcontext.setVariable("manager", manager);
                java.lang.String department = (java.lang.String) model.getDepartment();
                kcontext.setVariable("department", department);
            }
        });
        subProcessNode5.done();
        org.jbpm.ruleflow.core.factory.EndNodeFactory endNode6 = factory.endNode(6);
        endNode6.name("End");
        endNode6.terminate(false);
        endNode6.done();
        org.jbpm.ruleflow.core.factory.WorkItemNodeFactory workItemNode7 = factory.workItemNode(7);
        workItemNode7.name("Validate employee");
        workItemNode7.workName("ValidateEmployee");
        workItemNode7.workParameter("TaskName", "ValidateEmployee");
        workItemNode7.workParameter("Model", "onboarding");
        workItemNode7.workParameter("Decision", "employeeValidation");
        workItemNode7.workParameter("Namespace", "test");
        workItemNode7.inMapping("employee", "employee");
        workItemNode7.outMapping("message", "message");
        workItemNode7.outMapping("status", "status");
        workItemNode7.done();
        org.jbpm.ruleflow.core.factory.SubProcessNodeFactory subProcessNode8 = factory.subProcessNode(8);
        subProcessNode8.name("Setup Payroll");
        subProcessNode8.processId("onboarding.setupPayroll");
        subProcessNode8.processName("");
        subProcessNode8.waitForCompletion(true);
        subProcessNode8.independent(false);
        subProcessNode8.subProcessFactory(new org.jbpm.workflow.core.node.SubProcessFactory<SetupPayrollModel>() {

            public SetupPayrollModel bind(org.kie.api.runtime.process.ProcessContext kcontext) {
                com.myspace.onboarding.SetupPayrollModel model = new com.myspace.onboarding.SetupPayrollModel();
                com.myspace.onboarding.Employee employee = (com.myspace.onboarding.Employee) kcontext.getVariable("employee");
                model.setEmployee((com.myspace.onboarding.Employee) (employee));
                return model;
            }

            public org.kie.kogito.process.ProcessInstance<SetupPayrollModel> createInstance(SetupPayrollModel model) {
                return app.createSetupPayrollProcess().createInstance(model);
            }

            public void unbind(org.kie.api.runtime.process.ProcessContext kcontext, SetupPayrollModel model) {
                com.myspace.onboarding.Payroll payroll = (com.myspace.onboarding.Payroll) model.getPayroll();
                kcontext.setVariable("payroll", payroll);
            }
        });
        subProcessNode8.done();
        org.jbpm.ruleflow.core.factory.JoinFactory joinNode9 = factory.joinNode(9);
        joinNode9.name("Split");
        joinNode9.type(1);
        joinNode9.done();
        org.jbpm.ruleflow.core.factory.FaultNodeFactory faultNode10 = factory.faultNode(10);
        faultNode10.name("Error");
        faultNode10.setFaultName("Rejected");
        faultNode10.done();
        org.jbpm.ruleflow.core.factory.WorkItemNodeFactory workItemNode11 = factory.workItemNode(11);
        workItemNode11.name("Assign id and email");
        workItemNode11.workName("AssignIdAndEmail");
        workItemNode11.workParameter("TaskName", "AssignIdAndEmail");
        workItemNode11.workParameter("Model", "onboarding");
        workItemNode11.workParameter("Decision", "id");
        workItemNode11.workParameter("Namespace", "test");
        workItemNode11.inMapping("employee", "employee");
        workItemNode11.outMapping("employeeId", "employeeId");
        workItemNode11.outMapping("email", "email");
        workItemNode11.done();
        factory.connection(11, 1, "_7DF7D2C1-195D-4C19-BC47-CC4462F9062D");
        factory.connection(5, 3, "_D32B4A20-53D1-46F3-95F6-276A2392931F");
        factory.connection(7, 4, "_5A442C9F-BADA-4D95-9ECA-3BD2898C8377");
        factory.connection(1, 5, "_D5E3808E-4D68-4B8F-939D-17C7643E58FD");
        factory.connection(9, 6, "_3CFB2AC2-17B6-4322-ACD8-C9A149975143");
        factory.connection(2, 7, "_9D72C909-B631-415E-B015-30812A42B8C2");
        factory.connection(1, 8, "_DF6979D3-AFD3-4BC8-A866-8B9214F7F0FB");
        factory.connection(3, 9, "_44ABA3C5-9A26-4843-B03B-99C2C0251707");
        factory.connection(8, 9, "_BB67B9E9-611B-4B17-A6F9-3CD4DCA4B249");
        factory.connection(4, 10, "_5F4F4E2D-B4EC-4542-91DF-69FC30C5E346");
        factory.connection(4, 11, "_A65F4850-A93F-4703-B5AA-2EF5F649BC86");
        factory.validate();
        return factory.getProcess();
    }
}
