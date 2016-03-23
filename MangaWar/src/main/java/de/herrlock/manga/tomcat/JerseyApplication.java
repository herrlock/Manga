package de.herrlock.manga.tomcat;

import org.glassfish.jersey.server.ResourceConfig;

public class JerseyApplication extends ResourceConfig {

    public JerseyApplication() {
        packages( true, getClass().getPackage().getName() );
    }
}
