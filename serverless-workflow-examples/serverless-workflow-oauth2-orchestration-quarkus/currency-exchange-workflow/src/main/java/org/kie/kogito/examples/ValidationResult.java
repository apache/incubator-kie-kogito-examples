/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.examples;

/**
 * Used to return the validation results to the Currency Exchange Workflow.
 */
public class ValidationResult {

    private String executionStatus;
    private String executionStatusMessage;

    public ValidationResult() {
    }

    public ValidationResult(String executionStatus, String executionStatusMessage) {
        this.executionStatus = executionStatus;
        this.executionStatusMessage = executionStatusMessage;
    }

    public String getExecutionStatus() {
        return executionStatus;
    }

    public String getExecutionStatusMessage() {
        return executionStatusMessage;
    }
}
