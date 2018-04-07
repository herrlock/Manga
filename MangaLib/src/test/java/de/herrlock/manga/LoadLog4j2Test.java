package de.herrlock.manga;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class LoadLog4j2Test {

    @Test
    public void loadLog4j2Xml() {
        URL resource = LoadLog4j2Test.class.getResource( "/log4j2-test.xml" );
        Assert.assertNotNull( resource );
    }

}
