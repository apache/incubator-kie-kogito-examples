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

import org.kie.kogito.flexible.example.model.Comment;
import org.kie.kogito.flexible.example.model.State;
import org.kie.kogito.flexible.example.model.SupportCase;

import static org.kie.kogito.flexible.example.model.State.WAITING_FOR_CUSTOMER;
import static org.kie.kogito.flexible.example.model.State.WAITING_FOR_OWNER;

@ApplicationScoped
public class CommentService {

    public SupportCase addCustomerComment(SupportCase supportCase, String comment, String author) {
        return addComment(supportCase, author, comment, WAITING_FOR_OWNER);
    }

    public SupportCase addSupportComment(SupportCase supportCase, String comment, String author) {
        return addComment(supportCase, author, comment, WAITING_FOR_CUSTOMER);
    }

    private SupportCase addComment(SupportCase supportCase, String author, String comment, State newState) {
        SupportCase sCase = new SupportCase(supportCase).addComment(new Comment().setAuthor(author).setText(comment));
        if (State.NEW.equals(supportCase.getState())) {
            return sCase;
        }
        return sCase.setState(newState);
    }

}
