package de.herrlock.manga.host.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.herrlock.manga.host.Hoster;

/**
 * provides information about {@link Hoster}s
 * 
 * @author HerrLock
 */
@Target( ElementType.TYPE )
@Retention( RetentionPolicy.RUNTIME )
public @interface Details {
    /**
     * the Hoster's name
     * 
     * @return the hoster's name
     */
    String name();
    /**
     * the hoster's url, only the host-part
     * 
     * @return the hoster's url
     */
    String baseUrl();
}
