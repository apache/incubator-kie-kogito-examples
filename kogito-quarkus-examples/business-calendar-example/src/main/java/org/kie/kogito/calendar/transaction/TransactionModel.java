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
