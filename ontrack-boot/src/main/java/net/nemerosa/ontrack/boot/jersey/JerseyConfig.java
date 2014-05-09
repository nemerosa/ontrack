package net.nemerosa.ontrack.boot.jersey;

import net.nemerosa.ontrack.boot.Application;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        // Basics
        register(RequestContextFilter.class);

        // Exception handling
        register(JerseyNotFoundExceptionProvider.class);

        // TODO Configurable logging
        // register(LoggingFilter.class);

        // Support for JSON
        register(JerseyJacksonFeature.class);

        // Scanning scope
        packages(Application.class.getPackage().getName());
    }

}
