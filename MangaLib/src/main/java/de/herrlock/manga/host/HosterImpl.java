package de.herrlock.manga.host;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import de.herrlock.manga.host.annotations.Details;
import de.herrlock.manga.index.entity.HosterListEntry;
import de.herrlock.manga.util.configuration.DownloadConfiguration;
import de.herrlock.manga.util.configuration.IndexerConfiguration;

/**
 * @author HerrLock
 */
public abstract class HosterImpl {
    public abstract ChapterList getChapterList( DownloadConfiguration conf ) throws IOException;

    public Collection<HosterListEntry> getAvailabile( @SuppressWarnings( "unused" ) final IndexerConfiguration conf ) {
        HosterListEntry hosterListEntry = new HosterListEntry();
        hosterListEntry.setName( "&lt;no&nbsp;entries&gt;" );
        hosterListEntry.setUrl( getDetails().baseUrl() );
        return Arrays.asList( hosterListEntry );
    }

    public Details getDetails() {
        Details details = getClass().getAnnotation( Details.class );
        if ( details == null ) {
            throw new IllegalStateException( "No annotation @Details given. Consider adding it or override this method." );
        }
        return details;
    }

}
