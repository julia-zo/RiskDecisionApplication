package org.juliazo.risk.decision.application;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * An application configuration class. Provides information about packages
 * and applies dependency injection bindings
 */
public class RiskDecisionApplication extends ResourceConfig {

    public RiskDecisionApplication() {
        register(new RiskDecisionBinder());
        packages(true, "org.juliazo.risk.decision");
    }

}
