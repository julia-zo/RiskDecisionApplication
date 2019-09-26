package org.juliazo.risk.decision;

import org.juliazo.risk.decision.api.CreditHistoryV1;
import org.juliazo.risk.decision.api.CreditRequestDecisionV1;
import org.juliazo.risk.decision.api.CreditRequestV1;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CreditDecisionApiTest {

    private static String SERVICE_URL = "http://localhost:8080/v1/";
    private static String DECISION_ENDPOINT = "decision";
    private static String HISTORY_ENDPOINT = "history";

    @Rule
    public final JettyServerResource server = new JettyServerResource();

    @Test
    public void requestUpTo10ShouldBeAccepted() {
        CreditRequestV1 requestPayload = defaultCreditRequestOfPurchaseAmount(10);

        Response response = ClientBuilder.newClient()
                                         .target(SERVICE_URL+DECISION_ENDPOINT).request()
                                         .post(Entity.entity(requestPayload, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus(), is(200));
        CreditRequestDecisionV1 creditDecision = response.readEntity(CreditRequestDecisionV1.class);
        assertThat(creditDecision.isAccepted(), is(true));
        assertThat(creditDecision.getReason(), is("ok"));
    }

    @Test
    public void requestAbove10ShouldNotBeAccepted() {
        CreditRequestV1 requestPayload = defaultCreditRequestOfPurchaseAmount(11);

        Response response = ClientBuilder.newClient()
                                         .target(SERVICE_URL+DECISION_ENDPOINT).request()
                                         .post(Entity.entity(requestPayload, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus(), is(200));
        CreditRequestDecisionV1 creditDecision = response.readEntity(CreditRequestDecisionV1.class);
        assertThat(creditDecision.isAccepted(), is(false));
        assertThat(creditDecision.getReason(), is("amount"));
    }

    @Test
    public void customerDebtLimitShouldBeExceeded() {
        for (int i = 0; i < 10; i++) {
            ClientBuilder.newClient()
                         .target(SERVICE_URL+DECISION_ENDPOINT).request()
                         .post(Entity.entity(defaultCreditRequestOfPurchaseAmount(10), MediaType.APPLICATION_JSON));
        }

        CreditRequestV1 requestPayload = defaultCreditRequestOfPurchaseAmount(1);

        Response response = ClientBuilder.newClient()
                                         .target(SERVICE_URL+DECISION_ENDPOINT).request()
                                         .post(Entity.entity(requestPayload, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus(), is(200));
        CreditRequestDecisionV1 creditDecision = response.readEntity(CreditRequestDecisionV1.class);
        assertThat(creditDecision.isAccepted(), is(false));
        assertThat(creditDecision.getReason(), is("debt"));
    }

    @Test
    public void getCreditHistoryOfExistingCustomer() {
        CreditRequestV1 requestPayload = defaultCreditRequestOfPurchaseAmount(10);

        Response response = ClientBuilder.newClient()
                .target(SERVICE_URL+DECISION_ENDPOINT).request()
                .post(Entity.entity(requestPayload, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus(), is(200));
        response = ClientBuilder.newClient()
                .target(SERVICE_URL+DECISION_ENDPOINT).request()
                .post(Entity.entity(requestPayload, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus(), is(200));
        response = ClientBuilder.newClient()
                .target(SERVICE_URL+HISTORY_ENDPOINT+"/john@doe.com").request().get();
        assertThat(response.getStatus(), is(200));
        CreditHistoryV1 creditHistory = response.readEntity(CreditHistoryV1.class);
        assertThat(creditHistory.getEmail(), is("john@doe.com"));
        Assert.assertEquals(2, creditHistory.getHistory().size());
    }

    @Test
    public void getCreditHistoryOfNonExistingCustomer() {
        Response response = ClientBuilder.newClient()
                .target(SERVICE_URL+HISTORY_ENDPOINT+"/jessie@doe.com").request().get();
        assertThat(response.getStatus(), is(200));
        CreditHistoryV1 creditHistory = response.readEntity(CreditHistoryV1.class);
        assertThat(creditHistory.getEmail(), is("jessie@doe.com"));
        Assert.assertEquals(0, creditHistory.getHistory().size());
    }

    @Test
    public void getCreditHistoryOfExistingCustomerFiltered() {
        CreditRequestV1 requestPayload = defaultCreditRequestOfPurchaseAmount(10);

        Response response = ClientBuilder.newClient()
                .target(SERVICE_URL+DECISION_ENDPOINT).request()
                .post(Entity.entity(requestPayload, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus(), is(200));
        requestPayload = defaultCreditRequestOfPurchaseAmount(12);
        response = ClientBuilder.newClient()
                .target(SERVICE_URL+DECISION_ENDPOINT).request()
                .post(Entity.entity(requestPayload, MediaType.APPLICATION_JSON));

        assertThat(response.getStatus(), is(200));
        response = ClientBuilder.newClient()
                .target(SERVICE_URL+HISTORY_ENDPOINT+"/john@doe.com?decision=amount").request().get();
        assertThat(response.getStatus(), is(200));
        CreditHistoryV1 creditHistory = response.readEntity(CreditHistoryV1.class);
        assertThat(creditHistory.getEmail(), is("john@doe.com"));
        Assert.assertEquals(1, creditHistory.getHistory().size());
    }


    private CreditRequestV1 defaultCreditRequestOfPurchaseAmount(int purchaseAmount) {
        return new CreditRequestV1("john@doe.com", "john", "doe", purchaseAmount);
    }

}
