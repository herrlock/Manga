package de.herrlock.manga.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.Assert;

/**
 * Contains utility-methods for the tests
 * 
 * @author HerrLock
 */
public final class StaticMethods {

    /**
     * Calls the private (no-args) constructor of a class.
     * 
     * @param clazz
     *            the clazz to retrieve the constructor from
     * @return the just created instance of the class
     * @throws ReflectiveOperationException
     *             from {@link Class#getDeclaredConstructor(Class...)} and {@link #callPrivateConstructor(Constructor)}
     */
    public static <T> T callPrivateConstructor( final Class<T> clazz ) throws InstantiationException, IllegalAccessException,
        IllegalArgumentException, NoSuchMethodException, SecurityException, ReflectiveOperationException {
        return callPrivateConstructor( clazz.getDeclaredConstructor() );
    }

    /**
     * Calls the private constructor of a class.
     * 
     * @param constructor
     *            the constructor to use
     * @return the just created instance of the class
     * @throws ReflectiveOperationException
     *             from {@link Constructor#setAccessible(boolean)} and {@link Constructor#newInstance(Object...)}
     */
    public static <T> T callPrivateConstructor( final Constructor<T> constructor )
        throws InstantiationException, IllegalAccessException, IllegalArgumentException, ReflectiveOperationException {
        Assert.assertNotNull( "Passed constructor must be non-null", constructor );
        Assert.assertTrue( "Constructor is not private", Modifier.isPrivate( constructor.getModifiers() ) );
        constructor.setAccessible( true );
        return constructor.newInstance();
    }

    private StaticMethods() {
        // not used
    }
}
