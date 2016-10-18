package de.herrlock.manga.index;

import java.text.MessageFormat;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author HerrLock
 */
@XmlRootElement
@XmlAccessorType( XmlAccessType.NONE )
public final class Index {

    @XmlElement( name = "hoster" )
    private Collection<HosterList> hosters;

    public Collection<HosterList> getHosters() {
        return this.hosters;
    }

    public void setHosters( final Collection<HosterList> hosters ) {
        this.hosters = hosters;
    }

    @Override
    public String toString() {
        return MessageFormat.format( "Index ({0})", this.hosters );
    }
}
