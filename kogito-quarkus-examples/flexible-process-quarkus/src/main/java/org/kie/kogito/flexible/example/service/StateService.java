/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.flexible.example.service;

import jakarta.enterprise.context.ApplicationScoped;

import org.kie.kogito.flexible.example.model.Questionnaire;
import org.kie.kogito.flexible.example.model.State;
import org.kie.kogito.flexible.example.model.SupportCase;

@ApplicationScoped
public class StateService {

    public void resolve() {
    }

    public SupportCase resolve(SupportCase supportCase) {
        return new SupportCase(supportCase).setState(State.RESOLVED);
    }

    public SupportCase close(SupportCase supportCase, Integer evaluation, String comment) {
        return new SupportCase(supportCase)
                .setQuestionnaire(new Questionnaire().setComment(comment).setEvaluation(evaluation))
                .setState(State.CLOSED);
    }

}
