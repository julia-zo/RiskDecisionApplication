package org.juliazo.risk.decision.service;

import org.juliazo.risk.decision.api.CreditHistoryV1;
import org.juliazo.risk.decision.api.CreditRequestDecisionV1;
import org.juliazo.risk.decision.api.CreditRequestV1;
import org.juliazo.risk.decision.domain.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.LinkedList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CreditDecisionServiceTest {

    @Mock
    private CustomerDebtRepository customerDebtRepository;

    @Mock
    private CreditDecisionMaker creditDecisionMaker;

    @Mock
    private CreditHistoryRepository creditHistoryRepository;

    @InjectMocks
    private CreditDecisionServiceV1 creditDecisionService;

    @Test
    public void shouldAcceptCreditRequest() {
        CreditRequestV1 creditRequest = defaultCreditRequestOfPurchaseAmount(10);

        when(customerDebtRepository.fetchCustomerDebtForEmail(creditRequest.getEmail()))
                .thenReturn(new CustomerDebt(creditRequest.getEmail(), 7));
        when(creditDecisionMaker.makeCreditDecision(10, 7))
                .thenReturn(CreditDecision.ACCEPTED);

        CreditRequestDecisionV1 decision = creditDecisionService.handleCreditRequestV1(creditRequest);
        assertThat(decision.isAccepted(), is(true));
        assertThat(decision.getReason(), is("ok"));
    }

    @Test
    public void shouldRejectCreditRequest() {
        CreditRequestV1 creditRequest = defaultCreditRequestOfPurchaseAmount(11);

        when(customerDebtRepository.fetchCustomerDebtForEmail(creditRequest.getEmail()))
                .thenReturn(new CustomerDebt(creditRequest.getEmail(), 7));
        when(creditDecisionMaker.makeCreditDecision(11, 7))
                .thenReturn(CreditDecision.MAX_AMOUNT_BREACH);

        CreditRequestDecisionV1 decision = creditDecisionService.handleCreditRequestV1(creditRequest);
        assertThat(decision.isAccepted(), is(false));
        assertThat(decision.getReason(), is("amount"));
    }

    @Test
    public void shouldUpdateCustomerDebtWhenCreditAccepted() {
        CreditRequestV1 creditRequest = defaultCreditRequestOfPurchaseAmount(10);

        when(customerDebtRepository.fetchCustomerDebtForEmail(creditRequest.getEmail()))
                .thenReturn(new CustomerDebt(creditRequest.getEmail(), 7));
        when(creditDecisionMaker.makeCreditDecision(10, 7))
                .thenReturn(CreditDecision.ACCEPTED);

        creditDecisionService.handleCreditRequestV1(creditRequest);

        ArgumentCaptor<CustomerDebt> captor = ArgumentCaptor.forClass(CustomerDebt.class);
        verify(customerDebtRepository).persistCustomerDebt(captor.capture());
        assertThat(captor.getValue().getDebtAmount(), is(17));
    }

    @Test
    public void shouldReturnCreditHistory() {
        CreditRequestV1 creditRequest = defaultCreditRequestOfPurchaseAmount(10);

        when(customerDebtRepository.fetchCustomerDebtForEmail(creditRequest.getEmail()))
                .thenReturn(new CustomerDebt(creditRequest.getEmail(), 7));
        when(creditDecisionMaker.makeCreditDecision(10, 7))
                .thenReturn(CreditDecision.ACCEPTED);

        Collection<Transaction> transactions = new LinkedList<>();
        Transaction customerTransaction = new Transaction(creditRequest.getEmail(),
                creditRequest.getPurchaseAmount(), CreditDecision.ACCEPTED);
        transactions.add(customerTransaction);

        when(creditHistoryRepository.lookupTransactions(creditRequest.getEmail()))
                .thenReturn(transactions);

        CreditRequestDecisionV1 decision = creditDecisionService.handleCreditRequestV1(creditRequest);
        assertThat(decision.isAccepted(), is(true));
        assertThat(decision.getReason(), is("ok"));

        CreditHistoryV1 history =
                creditDecisionService.handleCreditHistoryRequestV1(creditRequest.getEmail(), "");
        assertThat(history.getEmail(), is(creditRequest.getEmail()));
        Assert.assertEquals(1, history.getHistory().size());
    }

    @Test
    public void shouldReturnEmptyCreditHistoryWhenNonExistentTransactionFromSearch() {
        CreditRequestV1 creditRequest = defaultCreditRequestOfPurchaseAmount(10);

        when(customerDebtRepository.fetchCustomerDebtForEmail(creditRequest.getEmail()))
                .thenReturn(new CustomerDebt(creditRequest.getEmail(), 7));
        when(creditDecisionMaker.makeCreditDecision(10, 7))
                .thenReturn(CreditDecision.ACCEPTED);

        when(creditHistoryRepository.lookupTransactions(creditRequest.getEmail(), "DEBT"))
                .thenReturn(new LinkedList<>());

        CreditRequestDecisionV1 decision = creditDecisionService.handleCreditRequestV1(creditRequest);
        assertThat(decision.isAccepted(), is(true));
        assertThat(decision.getReason(), is("ok"));

        CreditHistoryV1 history =
                creditDecisionService.handleCreditHistoryRequestV1(creditRequest.getEmail(), "DEBT");
        assertThat(history.getEmail(), is(creditRequest.getEmail()));
        Assert.assertEquals(0, history.getHistory().size());
    }

    @Test
    public void shouldReturnEmptyCreditHistoryForNonExistentClient() {

        when(creditHistoryRepository.lookupTransactions("chris@doe.com", "DEBT"))
                .thenReturn(new LinkedList<>());

        CreditHistoryV1 history =
                creditDecisionService.handleCreditHistoryRequestV1("chris@doe.com", "");
        assertThat(history.getEmail(), is("chris@doe.com"));
        Assert.assertEquals(0, history.getHistory().size());
    }

    @Test
    public void shouldReturnEmptyCreditHistoryForNonExistentClientSearchTransaction() {

        when(creditHistoryRepository.lookupTransactions("chris@doe.com", "DEBT"))
                .thenReturn(new LinkedList<>());

        CreditHistoryV1 history =
                creditDecisionService.handleCreditHistoryRequestV1("chris@doe.com", "DEBT");
        assertThat(history.getEmail(), is("chris@doe.com"));
        Assert.assertEquals(0, history.getHistory().size());
    }

    private CreditRequestV1 defaultCreditRequestOfPurchaseAmount(int amount) {
        return new CreditRequestV1("john@doe.com", "John", "Doe", amount);
    }

}
