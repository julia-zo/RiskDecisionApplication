package org.juliazo.risk.decision.domain;

/**
 * The implementation of the {@link CreditDecisionMaker} interface.
 */
public class CreditDecisionMakerImpl implements CreditDecisionMaker {

    private static int MAX_PURCHASE = 10;
    private static int MAX_DEBT = 100;

    @Override
    public CreditDecision makeCreditDecision(int purchaseAmount, int currentCustomerDebt) {
        if (purchaseAmount > MAX_PURCHASE) {
            return CreditDecision.MAX_AMOUNT_BREACH;
        }
        if (purchaseAmount < 1) {
            throw new IllegalArgumentException("purchaseAmount below minimum");
        }

        if (purchaseAmount + currentCustomerDebt > MAX_DEBT) {
            return CreditDecision.DEBT;
        }

        return CreditDecision.ACCEPTED;
    }

}
