package de.herrlock.manga.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Joiner;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import de.herrlock.manga.exceptions.MDRuntimeException;

/**
 * A central class for static methods
 * 
 * @author HerrLock
 */
public final class Utils {
    private static final Logger logger = LogManager.getLogger();
    // private static final Logger debugLogger = LogManager.getLogger( "com.example.java.debug.Utils" );

    private static final Joiner COMMA_JOINER = Joiner.on( "," );
    private static final ExecutorService THREAD_POOL;

    static {
        final Properties p = new Properties();

        Path confFile = Paths.get( "conf", "settings.conf" );
        if ( Files.exists( confFile ) ) {
            try ( InputStream in = Files.newInputStream( confFile ) ) {
                p.load( in );
            } catch ( IOException ex ) {
                throw new RuntimeException( ex );
            }
        }

        int count = Integer.parseInt( p.getProperty( "thread.count", "10" ) );
        THREAD_POOL = Executors.newFixedThreadPool( count,
            new ThreadFactoryBuilder().setNameFormat( "Droggelb%03dcher" ).setDaemon( true ).build() );
    }

    /**
     * Queue all given Callables in the ExecutorService and returns the resulting {@link Future}s
     * 
     * @param <T>
     *            The generic type of the given callables, the generic type of the Future-objects in the result
     * @param callables
     *            the {@link Callable}s to execute
     * @return the result of {@link ExecutorService#invokeAll(Collection)}
     */
    public static <T> List<Future<T>> callCallables( final Collection<? extends Callable<T>> callables ) {
        try {
            return THREAD_POOL.invokeAll( callables );
        } catch ( final InterruptedException ex ) {
            throw new MDRuntimeException( ex );
        }
    }

    /**
     * Queue the Callable in the ExecutorService and returns the resulting {@link Future}.
     * 
     * @param callable
     *            the Callable to run
     * @param <T>
     *            result-type of the passed Callable
     * @return the Future that results from this Callable
     * @see ExecutorService#submit(Callable)
     */
    public static <T> Future<T> callCallable( final Callable<T> callable ) {
        return THREAD_POOL.submit( callable );
    }

    /**
     * Queue the Runnable in the ExecutorService and returns the resulting {@link Future}.
     * 
     * @param runnable
     *            the Runnable to run
     * @return the Future always returns {@code null}
     * @see ExecutorService#submit(Runnable)
     */
    public static Future<?> callCallable( final Runnable runnable ) {
        return THREAD_POOL.submit( runnable );
    }

    public static PropertiesBuilder newPropertiesBuilder() {
        return new PropertiesBuilder();
    }

    public static PropertiesBuilder newPropertiesBuilder( final Properties defaults ) {
        return new PropertiesBuilder( defaults );
    }

    /**
     * Register the MBean at the ObjectName constructed by the given Strings.
     * 
     * @param mbean
     *            The MBean to register.
     * @param domain
     *            The domain for the ObjectName.
     * @param params
     *            An array of parameters. These parameters are joined by a comma ({@code ,}) to represent the path of the
     *            ObjectName.
     * @see #registerMBean(Object, String, String)
     */
    public static void registerMBean( final Object mbean, final String domain, final String... params ) {
        String paramString = COMMA_JOINER.join( params );
        registerMBean( mbean, domain, paramString );
    }

    /**
     * Registers the given MBean at the ObjectName constructed by the given Strings. The domain and the path are concatenated by a
     * colon ({@code :}) to represent the ObjectName.
     * 
     * @param mbean
     *            The MBean to register.
     * @param domain
     *            The domain for the ObjectName.
     * @param paramString
     *            The path of the ObjectName.
     * @see #registerMBean(Object, String)
     */
    public static void registerMBean( final Object mbean, final String domain, final String paramString ) {
        String name = domain + ":" + paramString;
        registerMBean( mbean, name );
    }

    /**
     * Registers the given MBean at the ObjectName constructed by the given String.
     * 
     * @param mbean
     *            The MBean to register.
     * @param name
     *            The String representing the ObjectName of the MBean. Passed to {@link ObjectName#ObjectName(String) new
     *            ObjectName(String)}.
     */
    public static void registerMBean( final Object mbean, final String name ) {
        try {
            ObjectName objectName = new ObjectName( name );
            registerMBean( mbean, objectName );
        } catch ( MalformedObjectNameException ex ) {
            logger.warn( "Could not register MBeans, ignoring this.", ex );
        }
    }

    /**
     * Registers the given MBean at the given ObjectName.
     * 
     * @param mbean
     *            The MBean to register.
     * @param name
     *            The ObjectName of the MBean.
     */
    public static void registerMBean( final Object mbean, final ObjectName name ) {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            mbs.registerMBean( mbean, name );
        } catch ( InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException ex ) {
            logger.warn( "Could not register MBeans, ignoring this.", ex );
        }
    }

    private Utils() {
        // not called
    }

    public static class PropertiesBuilder {
        private final Properties _properties;
        private boolean open = true;

        public PropertiesBuilder() {
            this._properties = new Properties();
        }

        public PropertiesBuilder( final Properties defaults ) {
            this._properties = new Properties( defaults );
        }

        private void assureOpen() {
            if ( !this.open ) {
                throw new IllegalStateException( "Builder is already finished" );
            }
        }

        public PropertiesBuilder setProperty( final String key, final String value ) {
            assureOpen();
            if ( key != null ) {
                if ( value == null ) {
                    if ( this._properties.containsKey( key ) ) {
                        this._properties.remove( key );
                    }
                } else {
                    this._properties.setProperty( key, value );
                }
            }
            return this;
        }

        public PropertiesBuilder load( final Reader reader ) throws IOException {
            assureOpen();
            this._properties.load( reader );
            return this;
        }

        public PropertiesBuilder load( final InputStream inStream ) throws IOException {
            assureOpen();
            this._properties.load( inStream );
            return this;
        }

        public Properties build() {
            assureOpen();
            this.open = false;
            return this._properties;
        }

    }
}
