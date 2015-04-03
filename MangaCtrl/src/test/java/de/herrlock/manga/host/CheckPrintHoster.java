package de.herrlock.manga.host;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author HerrLock
 */
public class CheckPrintHoster {
    private static final String UTF_8 = "UTF-8";

    @Test
    public void executeTest() throws IOException {
        try ( ByteArrayOutputStream baos = new ByteArrayOutputStream() ) {
            try ( PrintStream ps = new PrintStream( baos, true, UTF_8 ) ) {
                PrintAllHoster.printHoster( ps );
            }
            byte[] outBytes = baos.toByteArray();
            byte[] stringBytes = getHosterText().getBytes( UTF_8 );

            Assert.assertArrayEquals( stringBytes, outBytes );
        }
    }

    private static String getHosterText() throws IOException {
        try ( ByteArrayOutputStream baos = new ByteArrayOutputStream() ) {
            try ( PrintStream ps = new PrintStream( baos, true, UTF_8 ) ) {
                ps.println( "availabile hoster" );
                Hoster[] values = Hoster.sortedValues();
                for ( Hoster h : values ) {
                    ps.println( h );
                }
            }
            return baos.toString( UTF_8 );
        }
    }

}
