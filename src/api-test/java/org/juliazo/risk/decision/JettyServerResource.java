package org.juliazo.risk.decision;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.rules.ExternalResource;

public class JettyServerResource extends ExternalResource {

    private Server jettyServer = new Server(8080);

    protected void before() throws Throwable {
        System.out.println("Start jettyServer");
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setResourceBase("src/main/webapp/");
        jettyServer.setHandler(webAppContext);
        jettyServer.start();
        System.out.println("JettyServer started");
    }

    protected void after() {
        if (this.jettyServer.isRunning()) {
            try {
                System.out.println("Stop jettyServer");
                jettyServer.stop();
                System.out.println("JettyServer stopped");
            } catch (Exception e) {
                System.out.println("Exception while stopping JettyServer");
            }
        }
    }
}