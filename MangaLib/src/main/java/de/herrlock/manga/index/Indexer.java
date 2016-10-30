package de.herrlock.manga.index;

import java.util.Collection;
import java.util.TreeSet;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableSet;

import de.herrlock.manga.host.Hoster;
import de.herrlock.manga.host.Hosters;
import de.herrlock.manga.util.configuration.IndexerConfiguration;

/**
 * @author HerrLock
 */
public final class Indexer {
    private static final Logger logger = LogManager.getLogger();

    public static JsonArray createJsonIndex( final IndexerConfiguration conf ) {
        Index index = createIndex( conf );
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for ( HosterList hosterList : index.getHosters() ) {
            for ( HosterListEntry hosterListEntry : hosterList.getMangas() ) {
                arrayBuilder.add( Json.createObjectBuilder() //
                    .add( "hosterName", hosterList.getHosterName() ) //
                    .add( "name", hosterListEntry.getName() ) //
                    .add( "url", hosterListEntry.getUrl() ) );
            }
        }
        return arrayBuilder.build();
    }

    public static Index createIndex( final IndexerConfiguration conf ) {
        logger.traceEntry( "({})", conf );
        Iterable<Hoster> values;
        if ( conf.getUrl() == null ) {
            values = Hosters.values();
        } else {
            values = ImmutableSet.of( Hosters.tryGetHostByURL( conf.getUrl() ) );
        }
        Collection<HosterList> hosterEntries = new TreeSet<>( HosterList.HOSTER_NAME_COMPARATOR );
        for ( Hoster hoster : values ) {
            HosterList indexFor = createIndexFor( hoster, conf );
            hosterEntries.add( indexFor );
        }
        Index index = new Index();
        index.setHosters( hosterEntries );
        return index;
    }

    private static HosterList createIndexFor( final Hoster hoster, final IndexerConfiguration conf ) {
        logger.traceEntry( "({})", hoster );
        Collection<HosterListEntry> elements = hoster.getAvailabile( conf );
        Collection<HosterListEntry> mangas = new TreeSet<>( HosterListEntry.NAME_COMPARATOR );
        mangas.addAll( elements );
        // select random elements from the list
        // final int AMOUNT = 10;
        // final java.util.Random random = new java.util.Random(
        // Long.parseLong( new java.text.SimpleDateFormat( "yyMMdd" ).format( new java.util.Date() ) ) );
        // for ( int i = 0; i < Math.min( elements.size(), AMOUNT ); i++ ) {
        // int rnd = random.nextInt( elements.size() );
        // HosterListEntry entry = com.google.common.collect.Iterables.get( elements, rnd );
        // mangas.add( entry );
        // }

        HosterList hosterList = new HosterList();
        hosterList.setHosterName( hoster.getName() );
        hosterList.setMangas( mangas );
        return hosterList;
    }

    private Indexer() {
        // avoid instantiation
    }
}
