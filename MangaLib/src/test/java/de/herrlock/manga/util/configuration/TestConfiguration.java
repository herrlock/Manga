package de.herrlock.manga.util.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.junit.Test;

import de.herrlock.manga.exceptions.InitializeException;
import de.herrlock.manga.util.ChapterPattern;

public class TestConfiguration {

    private final Properties p = new Properties();

    @Test
    public void testCreateUrl_valid() throws MalformedURLException {
        this.p.put( Configuration.URL, "http://localhost" );
        URL url = Configuration._createUrl( this.p );
        assertEquals( new URL( "http://localhost" ), url );
    }

    @Test( expected = InitializeException.class )
    public void testCreateUrl_missing1() {
        try {
            Configuration._createUrl( this.p );
        } catch ( InitializeException ex ) {
            assertEquals( "url is not filled but required", ex.getMessage() );
            throw ex;
        }
    }

    @Test( expected = InitializeException.class )
    public void testCreateUrl_missing2() {
        try {
            this.p.put( Configuration.URL, "" );
            Configuration._createUrl( this.p );
        } catch ( InitializeException ex ) {
            assertEquals( "url is not filled but required", ex.getMessage() );
            throw ex;
        }
    }

    @Test( expected = InitializeException.class )
    public void testCreateUrl_malformed() {
        try {
            this.p.put( Configuration.URL, "something that should be a url" );
            Configuration._createUrl( this.p );
        } catch ( InitializeException ex ) {
            assertEquals( "url is malformed", ex.getMessage() );
            assertEquals( MalformedURLException.class, ex.getCause().getClass() );
            throw ex;
        }
    }

    @Test
    public void testCreateChapterPattern() {
        this.p.put( Configuration.PATTERN, "10-12;13;15" );
        ChapterPattern pattern = Configuration._createPattern( this.p );
        assertTrue( pattern.contains( "10" ) );
    }

    @Test
    public void testCreateTimeout_valid() {
        this.p.put( Configuration.TIMEOUT, "5" );
        int timeout = Configuration._createTimeout( this.p );
        assertEquals( 5000, timeout );
    }

    @Test
    public void testCreateTimeout_missing1() {
        int timeout = Configuration._createTimeout( this.p );
        assertEquals( Configuration.TIMEOUT_DEFAULT, timeout );
    }

    @Test
    public void testCreateTimeout_missing2() {
        this.p.put( Configuration.TIMEOUT, "" );
        int timeout = Configuration._createTimeout( this.p );
        assertEquals( Configuration.TIMEOUT_DEFAULT, timeout );
    }

    @Test( expected = NumberFormatException.class )
    public void testCreateTimeout_invalid() {
        this.p.put( Configuration.TIMEOUT, "asd" );
        Configuration._createTimeout( this.p );
    }

    @Test
    public void instantiateConfiguration() {
        Configuration configuration = new ConfigurationExtension();
        assertEquals( Configuration.class, configuration.getClass().getSuperclass() );
        assertEquals( "someString", configuration.toString() );
    }

    private static final class ConfigurationExtension extends Configuration {
        @Override
        public String toString() {
            return "someString";
        }
    }
}
