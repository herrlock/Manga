package de.herrlock.manga.host;

import java.io.IOException;
import java.util.Collection;

import de.herrlock.manga.host.annotations.Details;
import de.herrlock.manga.index.HosterListEntry;
import de.herrlock.manga.util.configuration.DownloadConfiguration;
import de.herrlock.manga.util.configuration.IndexerConfiguration;

/**
 * @author HerrLock
 */
public abstract class HosterImpl {
    public abstract ChapterList getChapterList( DownloadConfiguration conf ) throws IOException;
    public abstract Collection<HosterListEntry> getAvailabile( IndexerConfiguration conf );

    public Details getDetails() {
        Details details = getClass().getAnnotation( Details.class );
        if ( details == null ) {
            throw new IllegalStateException( "No annotation @Details given. Consider adding it or override this method." );
        }
        return details;
    }

}
