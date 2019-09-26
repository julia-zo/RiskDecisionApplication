package org.juliazo.risk.decision.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

/**
 * A class representing the credit history of a given customer.
 */
public class CreditHistoryV1 {

    /**
     * The email of the customer.
     */
    @JsonProperty(value = "email", required = true)
    private String email;

    /**
     * The current credit history of this customer.
     */
    @JsonProperty(value = "history", required = true)
    private Collection history;

    private CreditHistoryV1() {
        // Required by Jackson
    }

    public CreditHistoryV1(String email, Collection history) {
        this.email = email;
        this.history = history;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Collection getHistory() {
        return history;
    }

    public void setHistory(Collection history) {
        this.history = history;
    }

    public static CreditHistoryV1 from(String email, Collection history) {
        return new CreditHistoryV1(email, history);
    }

    @Override
    public String toString() {
        return String.format("CreditHistoryV1 [email=%s, history=%s]", email, history.toString());
    }

}
