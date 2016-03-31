package de.herrlock.manga.host;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * provides information about {@link Hoster}s
 * 
 * @author HerrLock
 */
@Target( value = ElementType.TYPE )
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

    /**
     * @return true if the list is reversed (highest chapter first). Defaults to false
     */
    boolean reversed() default false;
}
