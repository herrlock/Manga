package de.herrlock.manga.host;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author HerrLock
 */
@Target( value = ElementType.TYPE )
@Retention( RetentionPolicy.RUNTIME )
public @interface ProxyDetails {
    Class<? extends ChapterList> proxiedClass();
}
