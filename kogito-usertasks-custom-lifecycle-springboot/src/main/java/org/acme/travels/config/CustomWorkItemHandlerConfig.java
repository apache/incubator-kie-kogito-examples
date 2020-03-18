package org.acme.travels.config;

import org.acme.travels.usertasks.CustomHumanTaskLifeCycle;
import org.jbpm.process.instance.impl.humantask.HumanTaskWorkItemHandler;
import org.kie.kogito.process.impl.DefaultWorkItemHandlerConfig;
import org.springframework.stereotype.Component;


/**
 * Custom work item handler configuration to change default work item handler for user tasks
 * to take into account custom phases
 * 
 * <ul>
 *  <li>Start</li>
 *  <li>Complete - an extension to default Complete phase that will allow only completion from started tasks</li>
 * </ul>
 *
 */
@Component
public class CustomWorkItemHandlerConfig extends DefaultWorkItemHandlerConfig {{
    register("Human Task", new HumanTaskWorkItemHandler(new CustomHumanTaskLifeCycle()));
}}