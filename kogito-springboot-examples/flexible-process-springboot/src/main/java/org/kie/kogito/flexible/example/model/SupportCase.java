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
package org.kie.kogito.flexible.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.kie.kogito.flexible.example.model.State.NEW;

public class SupportCase {

    private Product product;
    private String description;
    private String engineer;
    private String customer;
    private State state = NEW;
    private List<Comment> comments;
    private Questionnaire questionnaire;

    public SupportCase() {
    }

    public SupportCase(SupportCase supportCase) {
        this.customer = supportCase.customer;
        this.product = new Product(supportCase.product);
        this.engineer = supportCase.engineer;
        this.state = supportCase.state;
        this.description = supportCase.description;
        if (supportCase.comments != null) {
            this.comments = supportCase.comments.stream().map(Comment::new).collect(Collectors.toList());
        }
        if (supportCase.questionnaire != null) {
            this.questionnaire = new Questionnaire(supportCase.questionnaire);
        }
    }

    public Product getProduct() {
        return product;
    }

    public SupportCase setProduct(Product product) {
        this.product = product;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public SupportCase setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getEngineer() {
        return engineer;
    }

    public SupportCase setEngineer(String engineer) {
        this.engineer = engineer;
        return this;
    }

    public String getCustomer() {
        return customer;
    }

    public SupportCase setCustomer(String customer) {
        this.customer = customer;
        return this;
    }

    public State getState() {
        return state;
    }

    public SupportCase setState(State state) {
        this.state = state;
        return this;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public SupportCase addComment(Comment comment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(comment);
        return this;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public SupportCase setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
        return this;
    }

    @Override
    public String toString() {
        return "SupportCase{" + "product=" + product + ", description='" + description + '\'' + ", engineer='"
                + engineer + '\'' + ", customer='" + customer + '\'' + ", state=" + state + ", comments=" + comments
                + ", questionnaire=" + questionnaire + '}';
    }
}
