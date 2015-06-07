package de.herrlock.manga.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A List of Strings that is used to
 * 
 * @author HerrLock
 */
public class ChapterPattern {
    /**
     * the regex for the patterns<br>
     * accepts "a list of strings seperated by semicolons"
     */
    private static final Pattern REGEX = Pattern.compile( "^([^;]+;)*([^;]+)$" );

    private final String patternText;
    private final Collection<String> elements;

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
     *            the pattern to analyze
     */
    public ChapterPattern( String pattern ) {
        this.patternText = pattern;
        Set<String> result = new HashSet<>();
        // accept only if valid
        if ( REGEX.matcher( pattern ).matches() ) {
            // split string at ';'
            for ( String s : pattern.split( ";" ) ) {
                String[] chapter = s.split( "-" );
                if ( chapter.length == 1 ) {
                    // a single chapter
                    result.add( s );
                } else if ( chapter.length == 2 ) {
                    // an interval of chapters
                    int first = Integer.parseInt( chapter[0] );
                    int last = Integer.parseInt( chapter[1] );
                    for ( int i = first; i <= last; i++ ) {
                        result.add( i + "" );
                    }
                } else {
                    throw new RuntimeException( "chapterPattern is invalid" );
                }
            }
        }
        this.elements = Collections.unmodifiableCollection( result );
    }

    public String getPatternText() {
        return this.patternText;
    }

    public boolean contains( String s ) {
        return this.elements.contains( s );
    }
}
