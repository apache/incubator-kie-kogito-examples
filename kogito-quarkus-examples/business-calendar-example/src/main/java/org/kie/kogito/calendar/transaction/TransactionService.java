package org.kie.kogito.calendar.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransactionService {

    private Logger logger = LoggerFactory.getLogger(TransactionService.class);

    public TransactionModel processTransaction(String transactionId) {
        logger.info("processing transaction");
        return new TransactionModel(transactionId, 100.0, "SourceAccount", "BeneficiaryAccount");
    }

    public TransactionModel completeTransaction(TransactionModel transactionModel) {
        transactionModel.setStatus("Amount credited, transaction completed");
        logger.info("completing transaction");
        return transactionModel;
    }
}
