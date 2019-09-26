package org.juliazo.risk.decision.domain;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The implementation of the {@link CreditHistoryRepository} interface.
 */
public class CreditHistoryRepositoryImpl implements CreditHistoryRepository {

    private Map<String, Collection<Transaction>> customerTransactionStorage = Maps.newConcurrentMap();

    @Override
    public Collection<Transaction> lookupTransactions(String email) {
        return customerTransactionStorage.getOrDefault(email, new LinkedList<>());

    }

    @Override
    public Collection<Transaction> lookupTransactions(String email, String reason) {
        Collection<Transaction> transactions = lookupTransactions(email);
        return transactions.stream()
                .filter(t -> t.getDecision().getReason().equals(reason))
                .collect(Collectors.toList());
    }

    @Override
    public void persistTransaction(String email, int transactionAmount, CreditDecision decision){
        Collection<Transaction> transactions = lookupTransactions(email);
        Transaction customerTransaction = new Transaction(email, transactionAmount, decision);
        transactions.add(customerTransaction);
        customerTransactionStorage.put(email, transactions);
    }
}
