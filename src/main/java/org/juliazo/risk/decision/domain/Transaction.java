package org.juliazo.risk.decision.domain;

import java.util.Objects;

/**
 * A class representing a transaction attempted by the customer, might be an accepted or rejected transaction.
 */
public class Transaction {

    /**
     * The primary identifier of the customer.
     */
    private String customerEmail;

    /**
     * The amount used on a given transaction
     */
    private int transactionAmount;

    /**
     * The decision made over a given transaction
     */
    private CreditDecision decision;

    public Transaction() {
        // default
    }

    public Transaction(String customerEmail, int transactionAmount, CreditDecision decision) {
        this.customerEmail = customerEmail;
        this.transactionAmount = transactionAmount;
        this.decision = decision;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public int getTransactionAmount() {
        return transactionAmount;
    }

    public CreditDecision getDecision() {
        return decision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return transactionAmount == that.transactionAmount &&
                Objects.equals(customerEmail, that.customerEmail) &&
                decision == that.decision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerEmail, transactionAmount, decision);
    }
}
