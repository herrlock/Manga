package de.herrlock.javafx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author HerrLock
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Conf {
    /**
     * enable bidirectional binding in FXML (<code>#{controller.foo}</code>)
     */
    boolean enableBiDirectional() default false;
}
