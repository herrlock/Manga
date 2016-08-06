package de.herrlock.manga.tomcat;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author HerrLock
 */
public class JerseyApplicationTest {

    private final JerseyApplication jerseyApplication = new JerseyApplication();

    @Test
    public void test() {
        Set<Class<?>> classes = this.jerseyApplication.getClasses();
        Assert.assertEquals( 3, classes.size() );
    }
}
