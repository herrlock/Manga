package de.herrlock.javafx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to set up different parameters for {@link de.herrlock.javafx.AbstractApplication AbstractApplications}
 * 
 * @author HerrLock
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Conf {
    /**
     * enable bidirectional binding for FXML (<code>#{controller.foo}</code>)
     * 
     * @return if the bidirection-feature must be enabled
     */
    boolean enableBiDirectional() default false;
}
