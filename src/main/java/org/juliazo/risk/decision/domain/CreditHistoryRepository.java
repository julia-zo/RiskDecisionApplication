package org.juliazo.risk.decision.domain;

import java.util.Collection;

/**
 * An interface to log the credit decisions.
 */
public interface CreditHistoryRepository {

    /**
     * Lookup all of the transactions based on the customer's email.
     *
     * @param email the customer's email
     * @return the customer's history
     */
    Collection<Transaction> lookupTransactions(String email);

    /**
     * Lookup all of the transactions based on the customer's email and the reason.
     *
     * @param email  the customer's email
     * @param reason the credit decision reason
     * @return the customer's history
     */
    Collection<Transaction> lookupTransactions(String email, String reason);

    /**
     * Persisting a customer's transaction.
     *
     * @param email the customer's email
     * @param transactionAmount the amount of this transaction
     * @param decision the Credit Decision related to this transaction
     */
    void persistTransaction(String email, int transactionAmount, CreditDecision decision);

}
