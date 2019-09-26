package org.juliazo.risk.decision.service;

import com.google.common.base.Strings;
import org.juliazo.risk.decision.api.CreditHistoryV1;
import org.juliazo.risk.decision.api.CreditRequestDecisionV1;
import org.juliazo.risk.decision.api.CreditRequestV1;
import org.juliazo.risk.decision.domain.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The public API of the credit decision solution.
 */
@Path("/")
@Singleton
public class CreditDecisionServiceV1 {

    @Inject
    private CustomerDebtRepository customerDebtRepository;

    @Inject
    private CreditDecisionMaker creditDecisionMaker;

    @Inject
    private CreditHistoryRepository creditHistoryRepository;

    /**
     * Handling the credit decision process.
     *
     * @param creditRequestV1 credit request with the amount and the customer's details
     * @return the decision
     */
    @POST
    @Path("/v1/decision")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CreditRequestDecisionV1 handleCreditRequestV1(CreditRequestV1 creditRequestV1) {

        performArgumentChecks(creditRequestV1);

        CustomerDebt customerDebt = customerDebtRepository.fetchCustomerDebtForEmail(creditRequestV1.getEmail());

        CreditDecision creditDecision = creditDecisionMaker.makeCreditDecision(creditRequestV1.getPurchaseAmount(), customerDebt.getDebtAmount());

        creditHistoryRepository.persistTransaction(creditRequestV1.getEmail(), creditRequestV1.getPurchaseAmount(), creditDecision);

        if (creditDecision.isAccepted()) {
            customerDebt.increaseDebtAmount(creditRequestV1.getPurchaseAmount());
            customerDebtRepository.persistCustomerDebt(customerDebt);
        }

        return CreditRequestDecisionV1.from(creditDecision);
    }

    private void performArgumentChecks(CreditRequestV1 creditRequest) {
        checkArgument(creditRequest != null);
        checkArgument(!Strings.isNullOrEmpty(creditRequest.getEmail()));
        checkArgument(!Strings.isNullOrEmpty(creditRequest.getFirstName()));
        checkArgument(!Strings.isNullOrEmpty(creditRequest.getLastName()));
        checkArgument(creditRequest.getPurchaseAmount() > 0);
    }

    /**
     * Looking up the credit history of a given customer.
     *
     * @param email the identifier of the customer
     * @param decision Credit Decision reason used to filter the history (optional)
     * @return the credit history for this customer
     */
    @GET
    @Path("/v1/history/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public CreditHistoryV1 handleCreditHistoryRequestV1(
            @PathParam("email") String email,
            @QueryParam("decision") String decision) {

        checkArgument(!Strings.isNullOrEmpty(email));
        Collection<Transaction> customerTransactions;
        if (decision == null || decision.isEmpty()) {
            customerTransactions = creditHistoryRepository.lookupTransactions(email);
        } else {
            customerTransactions = creditHistoryRepository.lookupTransactions(email, decision);
        }

        return CreditHistoryV1.from(email, customerTransactions);
    }

}
