package de.herrlock.manga.tomcat;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * The {@link ResourceConfig} for the MangaWar-jersey-servlet
 * 
 * @author HerrLock
 */
public class JerseyApplication extends ResourceConfig {

    public JerseyApplication() {
        packages( true, getClass().getPackage().getName() );
    }
}
