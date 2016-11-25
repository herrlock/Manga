package de.herrlock.manga.index.entity;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author HerrLock
 */
@XmlAccessorType( XmlAccessType.NONE )
public final class HosterList {

    @XmlAttribute( name = "name" )
    private String hosterName;
    @XmlElement( name = "manga" )
    private Collection<HosterListEntry> mangas;

    public String getHosterName() {
        return this.hosterName;
    }

    public void setHosterName( final String hosterName ) {
        this.hosterName = hosterName;
    }

    public Collection<HosterListEntry> getMangas() {
        return this.mangas;
    }

    public void setMangas( final Collection<HosterListEntry> mangas ) {
        this.mangas = mangas;
    }

    @Override
    public String toString() {
        return MessageFormat.format( "HosterList @{0} ({1})", this.hosterName, this.mangas == null ? null : this.mangas.size() );
    }

    public static final Comparator<HosterList> HOSTER_NAME_COMPARATOR = new Comparator<HosterList>() {
        @Override
        public int compare( final HosterList list1, final HosterList list2 ) {
            String name1 = list1.getHosterName().toUpperCase( Locale.GERMANY );
            String name2 = list2.getHosterName().toUpperCase( Locale.GERMANY );
            return name1.compareTo( name2 );
        }
    };

}
