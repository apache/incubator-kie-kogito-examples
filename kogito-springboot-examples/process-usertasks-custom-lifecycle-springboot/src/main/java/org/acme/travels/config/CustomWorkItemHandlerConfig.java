/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
 * <li>Start</li>
 * <li>Complete - an extension to default Complete phase that will allow only completion from started tasks</li>
 * </ul>
 *
 */
@Component
public class CustomWorkItemHandlerConfig extends DefaultWorkItemHandlerConfig {
    {
        register("Human Task", new HumanTaskWorkItemHandler(new CustomHumanTaskLifeCycle()));
    }
}