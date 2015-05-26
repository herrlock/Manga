package de.herrlock.manga.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.herrlock.manga.util.Utils;

@RunWith( value = Parameterized.class )
public class UtilsReadStream {

    private final String string;

    @Parameters
    public static Collection<Object[]> getParams() {
        Object[][] strings = {
            {
                new String[] {
                    "HerrLock1", "HerrLock2"
                }
            }, {
                new String[] {
                    "Whatever1", "Whatever2"
                }
            }, {
                new String[] {
                    ""
                }
            }, {
                new String[] {}
            }
        };
        return Arrays.asList( strings );
    }

    public UtilsReadStream( String... strings ) {
        this.string = join( strings );
    }

    @Test
    public void readStream() throws IOException {
        byte[] buf = this.string.getBytes( StandardCharsets.UTF_8 );
        try ( ByteArrayInputStream in = new ByteArrayInputStream( buf ) ) {
            List<String> readStream = Utils.readStream( in );
            String[] arr = readStream.toArray( new String[readStream.size()] );
            String read = join( arr );
            Assert.assertEquals( this.string, read );
        }
    }

    public String join( String... arr ) {
        if ( arr.length > 0 ) {
            StringBuilder sb = new StringBuilder( arr[0] );
            for ( int i = 1; i < arr.length; i++ ) {
                sb.append( '\n' + arr[i] );
            }
            return sb.toString();
        }
        return "";
    }
}
