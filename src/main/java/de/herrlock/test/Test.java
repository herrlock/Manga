package de.herrlock.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;

public class Test {

    private static final boolean verbose = true;

    public static void main( String... args ) {
        System.out.println( findLaunchMethod( "./bin" ) );
    }

    private static URL fileToURL( File file ) throws IOException {
        return file.getCanonicalFile().toURI().toURL();
    }

    private static Method findLaunchMethod( String fxClassPath ) {
        if ( verbose ) {
            System.err.println( "1) Try existing classpath..." );
        }
        Method launchMethod = findLaunchMethod( null, fxClassPath );
        if ( launchMethod != null ) {
            return launchMethod;
        }
        if ( verbose ) {
            System.err.println( "2) Look for cobundled JavaFX ... [java.home=" + System.getProperty( "java.home" ) );
        }
        launchMethod = findLaunchMethodInJar( System.getProperty( "java.home" ), fxClassPath );
        if ( launchMethod != null ) {
            return launchMethod;
        }
        return launchMethod;
    }

    private static Method findLaunchMethodInJar( String jfxRtPathName, String fxClassPath ) {
        File jfxRtPath = new File( jfxRtPathName );

        File jfxRtLibPath = new File( jfxRtPath, "lib" );
        File jfxRtJar = new File( jfxRtLibPath, "jfxrt.jar" );
        if ( !jfxRtJar.canRead() ) {
            if ( verbose ) {
                System.err.println( "Unable to read " + jfxRtJar.toString() );
            }
            return null;
        }
        return findLaunchMethod( jfxRtPath, fxClassPath );
    }

    private static Method findLaunchMethod( File jfxRtPath, String fxClassPath ) {
        try {
            ArrayList<URL> urlList = new ArrayList<>();

            String cp = System.getProperty( "java.class.path" );
            if ( cp != null ) {
                while ( cp.length() > 0 ) {
                    int pathSepIdx = cp.indexOf( File.pathSeparatorChar );
                    if ( pathSepIdx < 0 ) {
                        String pathElem = cp;
                        urlList.add( fileToURL( new File( pathElem ) ) );
                        break;
                    }
                    if ( pathSepIdx > 0 ) {
                        String pathElem = cp.substring( 0, pathSepIdx );
                        urlList.add( fileToURL( new File( pathElem ) ) );
                    }
                    cp = cp.substring( pathSepIdx + 1 );
                }
            }
            cp = fxClassPath;
            if ( cp != null ) {
                File baseDir = null;
                try {
                    String path = Test.class.getProtectionDomain().getCodeSource().getLocation().getPath();

                    String decodedPath = URLDecoder.decode( path, "UTF-8" );
                    baseDir = new File( decodedPath ).getParentFile();
                    if ( !baseDir.exists() ) {
                        baseDir = null;
                    }
                } catch ( Exception e ) {
                    // ignore
                }
                while ( cp.length() > 0 ) {
                    int pathSepIdx = cp.indexOf( " " );
                    if ( pathSepIdx < 0 ) {
                        String pathElem = cp;
                        File f = baseDir == null ? new File( pathElem ) : new File( baseDir, pathElem );

                        urlList.add( fileToURL( f ) );
                        break;
                    }
                    if ( pathSepIdx > 0 ) {
                        String pathElem = cp.substring( 0, pathSepIdx );
                        File f = baseDir == null ? new File( pathElem ) : new File( baseDir, pathElem );

                        urlList.add( fileToURL( f ) );
                    }
                    cp = cp.substring( pathSepIdx + 1 );
                }
            }
            if ( jfxRtPath != null ) {
                File jfxRtLibPath = new File( jfxRtPath, "lib" );
                urlList.add( fileToURL( new File( jfxRtLibPath, "jfxrt.jar" ) ) );
                File deployJar = new File( jfxRtLibPath, "deploy.jar" );
                if ( !deployJar.exists() ) {
                    deployJar = getDeployJarFromJRE();
                }
                if ( deployJar != null ) {
                    URL deployJarURL = fileToURL( deployJar );
                    urlList.add( deployJarURL );
                    urlList.add( new URL( deployJarURL, "plugin.jar" ) );
                    urlList.add( new URL( deployJarURL, "javaws.jar" ) );
                } else {
                    if ( verbose ) {
                        System.err.println( "Skip JavaFX Runtime at " + jfxRtPath + " as no deploy jars found." );
                    }
                    return null;
                }
            }
            URL[] urls = urlList.toArray( new URL[urlList.size()] );
            if ( verbose ) {
                System.err.println( "===== URL list" );
                for ( int i = 0; i < urls.length; i++ ) {
                    System.err.println( "" + urls[i] );
                }
                System.err.println( "=====" );
            }
            ClassLoader urlClassLoader = new URLClassLoader( urls, null );
            @SuppressWarnings( "rawtypes" )
            Class launchClass = Class.forName( "com.sun.javafx.application.LauncherImpl", true, urlClassLoader );

            @SuppressWarnings( "unchecked" )
            Method m = launchClass.getMethod( "launchApplication", Class.class, Class.class, new String[0].getClass() );
            if ( m != null ) {
                Thread.currentThread().setContextClassLoader( urlClassLoader );
                return m;
            }
        } catch ( IOException | ReflectiveOperationException ex ) {
            if ( jfxRtPath != null ) {
                ex.printStackTrace();
            }
        } finally {
            // do nothing
        }
        return null;
    }

    private static File getDeployJarFromJRE() {
        String javaHome = System.getProperty( "java.home" );
        if ( verbose ) {
            System.err.println( "java.home = " + javaHome );
        }
        if ( ( javaHome == null ) || ( javaHome.equals( "" ) ) ) {
            return null;
        }
        File jreLibPath = new File( javaHome, "lib" );
        File deployJar = new File( jreLibPath, "deploy.jar" );
        if ( deployJar.exists() ) {
            return deployJar;
        }
        return null;
    }

}
