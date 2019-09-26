package org.juliazo.risk.decision.application;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.juliazo.risk.decision.domain.*;

/**
 * A class containing interface-to-implementation bindings for dependency injection
 */
public class RiskDecisionBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(CustomerDebtRepositoryImpl.class).to(CustomerDebtRepository.class);
        bind(CreditDecisionMakerImpl.class).to(CreditDecisionMaker.class);
        bind(CreditHistoryRepositoryImpl.class).to(CreditHistoryRepository.class);
    }

}
