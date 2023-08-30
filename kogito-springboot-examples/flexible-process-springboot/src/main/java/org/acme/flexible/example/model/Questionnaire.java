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
package org.acme.flexible.example.model;

import java.time.ZonedDateTime;

public class Questionnaire {

    private ZonedDateTime date = ZonedDateTime.now();
    private int evaluation;
    private String comment;

    public Questionnaire() {
    }

    public Questionnaire(Questionnaire q) {
        this.comment = q.comment;
        this.date = q.date;
        this.evaluation = q.evaluation;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public int getEvaluation() {
        return evaluation;
    }

    public Questionnaire setEvaluation(int evaluation) {
        this.evaluation = evaluation;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public Questionnaire setComment(String comment) {
        this.comment = comment;
        return this;
    }

    @Override
    public String toString() {
        return "Questionnaire{" + "date=" + date + ", evaluation=" + evaluation + ", comment='" + comment + '\'' + '}';
    }
}
