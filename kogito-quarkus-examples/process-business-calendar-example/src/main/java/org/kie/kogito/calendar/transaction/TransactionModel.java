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

package org.kie.kogito.calendar.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionModel {
    private String transactionId;
    private Double amount;
    private String sourceAccount;
    private String beneficiaryAccount;
    private String status = "Amount debited, transaction is pending";

    public TransactionModel() {
    }

    public TransactionModel(String transactionId, Double amount, String sourceAccount, String beneficiaryAccount) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.sourceAccount = sourceAccount;
        this.beneficiaryAccount = beneficiaryAccount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Double getAmount() {
        return amount;
    }

    public String getSourceAccount() {
        return sourceAccount;
    }

    public String getBeneficiaryAccount() {
        return beneficiaryAccount;
    }

    public String getStatus() {
        return status;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setSourceAccount(String sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public void setBeneficiaryAccount(String beneficiaryAccount) {
        this.beneficiaryAccount = beneficiaryAccount;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
