package de.herrlock.manga.util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A List of Strings that is used to
 * 
 * @author HerrLock
 */
public class ChapterPattern {
    private static final Logger logger = LogManager.getLogger();
    /**
     * the regex for the patterns<br>
     * accepts "a list of strings seperated by semicolons"
     */
    public static final Pattern REGEX = Pattern.compile( "^([^;]+;)*([^;]+)$" );

    /**
     * the Interval-elements to search
     */
    private final Collection<Interval> elements;
    /**
     * stores, if the Collection elements contains any entries. True, if the Collection is empty, so when using
     * {@link ChapterPattern#contains(String)} true can be returned when no element is availabile
     */
    private final boolean fallback;

    /**
     * a valid pattern consists of the chapter-numbers seperated by semicolon, or an interval of chapters, defined by the first
     * chapter, a hyphen and the last chapter<br>
     * eg:
     * <table>
     * <tr>
     * <th>pattern</th>
     * <th>matched chapters</th>
     * </tr>
     * <tr>
     * <td>42</td>
     * <td>chapter 42</td>
     * </tr>
     * <tr>
     * <td>42;45</td>
     * <td>chapters 42 and 45</td>
     * </tr>
     * <tr>
     * <td>42-46</td>
     * <td>chapters 42 to 46 (42, 43, 44, 45, 46)</td>
     * </tr>
     * <tr>
     * <td>42-46;50-52</td>
     * <td>chapters 42 to 46 and 50 to 52 (42, 43, 44, 45, 46, 50, 51, 52)</td>
     * </tr>
     * </table>
     * 
     * @param pattern
     *            the pattern to analyze, in case of {@code null} or an empty string an empty collection is used
     */
    public ChapterPattern( String pattern ) {
        logger.entry( pattern );
        Set<Interval> result = new HashSet<>();
        // accept only if valid
        if ( pattern != null && REGEX.matcher( pattern ).matches() ) {
            // split string at ';'
            for ( String s : pattern.split( ";" ) ) {
                result.add( new Interval( s.split( "-" ) ) );
            }
        }
        this.elements = Collections.unmodifiableCollection( result );
        this.fallback = this.elements.isEmpty();
    }

    /**
     * Checks, if a given Chapternumber is contained in the ChapterPattern
     * 
     * @param s
     *            the number of the Chapter to check
     * @return true, if any Interval contains the given Chapter's number
     */
    public boolean contains( String s ) {
        // check if any Interval contains the chapter
        for ( Interval i : this.elements ) {
            if ( i.contains( s ) ) {
                return true;
            }
        }
        // return true, if the ChapterPattern is empty, false otherwise
        return this.fallback;
    }

    static class Interval {
        private final BigDecimal intervalStart;
        private final BigDecimal intervalEnd;

        /**
         * @param parts
         *            the start and end of the Interval as Varargs
         * @throws IllegalArgumentException
         *             in case {@code parts.length} is 0
         */
        public Interval( String... parts ) {
            if ( parts.length >= 1 ) {
                this.intervalStart = new BigDecimal( parts[0] );
                if ( parts.length >= 2 ) {
                    // multiple arguments, ignore all after the second :)
                    this.intervalEnd = new BigDecimal( parts[1] );
                } else {
                    // one argument, so start and end are the same
                    this.intervalEnd = new BigDecimal( parts[0] );
                }
            } else {
                // parts.length == 0
                throw new IllegalArgumentException( "invalid, did not pass any arguments" );
            }

        }

        public boolean contains( String s ) {
            return contains( new BigDecimal( s ) );
        }

        public boolean contains( BigDecimal d ) {
            if ( d == null ) {
                return false;
            }
            int compareToStart = d.compareTo( this.intervalStart );
            int compareToEnd = d.compareTo( this.intervalEnd );
            boolean biggerThanStart = compareToStart >= 0;
            boolean smallerThanEnd = compareToEnd <= 0;
            return biggerThanStart && smallerThanEnd;
        }

        @Override
        public String toString() {
            return this.intervalStart + " - " + this.intervalEnd;
        }

    }
}
