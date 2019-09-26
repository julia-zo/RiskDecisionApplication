package org.juliazo.risk.decision.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;

public class CreditHistoryRepositoryTest {

    private CreditHistoryRepository creditHistoryRepository;

    @Before
    public void before() {
        creditHistoryRepository = new CreditHistoryRepositoryImpl();
    }

    @After
    public void after() {
        creditHistoryRepository = null;
    }

    @Test
    public void newCustomerTransaction() {
        creditHistoryRepository.persistTransaction("john@doe.com", 10, CreditDecision.ACCEPTED);
        Collection<Transaction> transactions = creditHistoryRepository.lookupTransactions("john@doe.com");
        Transaction customerTransaction =
                new Transaction("john@doe.com", 10, CreditDecision.ACCEPTED);

        Assert.assertEquals(1,transactions.size());
        Assert.assertEquals(customerTransaction, transactions.iterator().next());
    }

    @Test
    public void returningCustomerTransaction() {
        Collection<Transaction> expectedTransactions = new LinkedList<>();
        creditHistoryRepository.persistTransaction("johnny@doe.com", 10, CreditDecision.ACCEPTED);
        expectedTransactions.add(new Transaction("johnny@doe.com", 10, CreditDecision.ACCEPTED));
        creditHistoryRepository.persistTransaction("johnny@doe.com", 10, CreditDecision.ACCEPTED);
        expectedTransactions.add(new Transaction("johnny@doe.com", 10, CreditDecision.ACCEPTED));

        Collection<Transaction> transactions = creditHistoryRepository.lookupTransactions("johnny@doe.com");

        Assert.assertEquals(2, transactions.size());
        Assert.assertEquals(expectedTransactions, transactions);
    }

    @Test
    public void returningCustomerWithRejectedDebtTransaction() {
        Collection<Transaction> expectedTransactions = new LinkedList<>();
        creditHistoryRepository.persistTransaction("jane@doe.com", 2, CreditDecision.DEBT);
        expectedTransactions.add(new Transaction("jane@doe.com", 2, CreditDecision.DEBT));

        creditHistoryRepository.persistTransaction("jane@doe.com", 10, CreditDecision.ACCEPTED);

        Collection<Transaction> transactions = creditHistoryRepository.lookupTransactions("jane@doe.com", "debt");

        Assert.assertEquals(1,transactions.size());
        Assert.assertEquals(expectedTransactions, transactions);
    }

    @Test
    public void multipleReturningCustomersWithRejectedDebtTransaction() {
        Collection<Transaction> expectedTransactionsJolie = new LinkedList<>();
        creditHistoryRepository.persistTransaction("jolie@doe.com", 5, CreditDecision.DEBT);
        expectedTransactionsJolie.add(new Transaction("jolie@doe.com", 5, CreditDecision.DEBT));
        creditHistoryRepository.persistTransaction("jolie@doe.com", 10, CreditDecision.DEBT);
        expectedTransactionsJolie.add(new Transaction("jolie@doe.com", 10, CreditDecision.DEBT));

        creditHistoryRepository.persistTransaction("joanne@doe.com", 8, CreditDecision.DEBT);
        Collection<Transaction> expectedTransactionsJoanne = new LinkedList<>();
        expectedTransactionsJoanne.add(new Transaction("joanne@doe.com", 8, CreditDecision.DEBT));
        creditHistoryRepository.persistTransaction("joanne@doe.com", 11, CreditDecision.MAX_AMOUNT_BREACH);

        Collection<Transaction> transactionsJolie =
                creditHistoryRepository.lookupTransactions("jolie@doe.com", "debt");
        Collection<Transaction> transactionsJoanne =
                creditHistoryRepository.lookupTransactions("joanne@doe.com", "debt");

        Assert.assertEquals(2,transactionsJolie.size());
        Assert.assertEquals(expectedTransactionsJolie, transactionsJolie);

        Assert.assertEquals(1,transactionsJoanne.size());
        Assert.assertEquals(expectedTransactionsJoanne, transactionsJoanne);
    }
}
