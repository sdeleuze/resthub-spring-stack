package org.resthub.web.test;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.resthub.client.ClientFactory;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.context.ContextLoaderListener;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;

/**
 * Base class for your webservice tests, based on Jetty and with preconfigured Spring configuration.
 * Currently, a Jetty instance is launched for each tests in order to reset test conditions.
 * If you want to restart Jetty only once, you can extend it, override setUp() methods and use @BeforeClass instead of @Before annotations (don't forget to call super.setUp())
 * Ususally you will have to redefine @ContextConfiguration to specify your application context file in addition to resthub ones  
 */
@RunWith(JUnit4.class)
public abstract class AbstractWebTest {

    protected int port = 9797;

    protected Server server;
    
    protected String contextLocations = "classpath*:resthubContext.xml classpath*:applicationContext.xml";
    
    public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Server getServer() {
		return this.server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public String getContextLocations() {
		return contextLocations;
	}

	public void setContextLocations(String contextLocations) {
		this.contextLocations = contextLocations;
	}

    @Before
    public void setUp() throws Exception {
        server = new Server(port);

        // On lance le serveur Jetty
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.setDisplayName("resthub test webapp");
        context.setContextPath("/");

        context.getInitParams().put("contextConfigLocation", contextLocations);
        context.addFilter(OpenEntityManagerInViewFilter.class, "/*", 1);
        context.addServlet(SpringServlet.class, "/*");
        context.addEventListener(new ContextLoaderListener());

        server.setHandler(context);
        server.start();

    }

    @After
    public void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    protected WebResource resource() {
    	Client client = ClientFactory.create();
        return client.resource("http://localhost:" + port);
    }

}
