package de.herrlock.manga.index;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author HerrLock
 */
@XmlAccessorType( XmlAccessType.NONE )
public final class HosterListEntry {

    @XmlAttribute( name = "name" )
    private String name;
    @XmlAttribute( name = "url" )
    private String url;

    public String getName() {
        return this.name;
    }

    public void setName( final String name ) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl( final String url ) {
        this.url = url;
    }

    @Override
    public String toString() {
        return MessageFormat.format( "HosterListEntry ({0}, {1})", this.name, this.url );
    }

    public static final Comparator<HosterListEntry> NAME_COMPARATOR = new Comparator<HosterListEntry>() {
        @Override
        public int compare( final HosterListEntry entry1, final HosterListEntry entry2 ) {
            String name1 = entry1.getName().toUpperCase( Locale.GERMANY );
            String name2 = entry2.getName().toUpperCase( Locale.GERMANY );
            return name1.compareTo( name2 );
        }
    };

}
