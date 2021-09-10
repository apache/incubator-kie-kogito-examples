/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.processing;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.taskassigning.model.processing.UserAttributesProcessor;
import org.kie.kogito.taskassigning.user.service.User;

/**
 * User Attributes Processor example.
 * This scaffold class shows how user provided user attributes processors can be implemented.
 * For more information see: https://docs.kogito.kie.org/latest/html_single/#proc-create-custom-user-attributes-processors_kogito-configuring
 */
@ApplicationScoped
public class ExampleUserAttributesProcessor implements UserAttributesProcessor {

    /**
     * Indicates the priority of this processor when multiple user attributes processors are applied, lower priorities
     * executes first.
     */
    @Override
    public int getPriority() {
        return 30;
    }

    /**
     * Indicates if the processor is enabled. Disabled processors are not applied.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Executed when the user information is refreshed from the external user service.
     *
     * @param externalUser User instance returned by the external user service query.
     * @param targetAttributes Attributes to assign to the User counterpart managed by OptaPlanner.
     */
    @Override
    public void process(User externalUser, Map<String, Object> targetAttributes) {
        // custom attribute calculated by using the User information or any other procedure.
        // The user attributes processor can implement for example conversion operation from the external user system
        // attributes into some more proper values to be used in the constraints.
        // Object externalAttribute = externalUser.getAttributes("someAttributeInTheExternalSystem");
        // Object myCustomAttribute = calculate or transform the externalAttribute into some more understandable
        //targetAttributes.put("myCustomAttribute", myCustomAttributeValue);
    }
}