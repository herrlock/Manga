package de.herrlock.manga.host.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author HerrLock
 */
@Target( value = ElementType.TYPE )
@Retention( RetentionPolicy.RUNTIME )
public @interface ChapterListDetails {
    /**
     * indicates if the chapters are listed in descending order
     * 
     * @return true if the list is reversed (highest chapter first). Defaults to false
     */
    boolean reversed() default false;
}
