package de.herrlock.manga.tomcat.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;

import de.herrlock.manga.tomcat.service.DownloadService.MDObject;
import de.herrlock.manga.tomcat.service.DownloadService.ProgressObject;

/**
 * @author HerrLock
 */
public class DownloadServiceTest {
    // private final DownloadService downloadServlet = new DownloadService();

    @Test
    public void testProgressObject() {
        UUID randomUUID = UUID.randomUUID();
        ProgressObject progressObject = new ProgressObject( randomUUID, "url", true, 42, 1337 );
        assertEquals( randomUUID, progressObject.getUuid() );
        assertEquals( "url", progressObject.getUrl() );
        assertTrue( progressObject.getStarted() );
        assertEquals( 42, progressObject.getProgress() );
        assertEquals( 1337, progressObject.getMaxProgress() );
    }

    @Test
    public void testMDObject() {
        MDObject mdObject = new MDObject( "", null );
        assertEquals( "", mdObject.getUrl() );
        assertNull( mdObject.getMdownloader() );
    }

}
