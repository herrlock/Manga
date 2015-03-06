package de.herrlock.manga.host;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

import de.herrlock.manga.host.ChapterList.Chapter;
import de.herrlock.manga.util.Utils;

/**
 * A class the consists of multiple Chapters
 * 
 * @author HerrLock
 */
public abstract class ChapterList extends ArrayList<Chapter> {

    private final ChapterPattern cp;

    /**
     * creates an instance of {@linkplain ChapterList}, gets the right {@linkplain Hoster} from the {@linkplain URL} in
     * {@code Utils.arguments}
     * 
     * @return an instance of {@link ChapterList}; when no suitable Hoster can be detected {@code null}
     * @throws IOException
     *             thrown by {@link Hoster#getChapterList(URL)}
     */
    public static ChapterList getInstance() throws IOException {
        URL url = Utils.getMangaURL();
        Hoster h = Hoster.getHostByURL( url );
        if ( h == null ) {
            throw new IllegalArgumentException( url + " could not be resolved to a registered host." );
        }
        return h.getChapterList( url );
    }

    /**
     * creates a new ChapterList. reads the ChapterPattern from the central arguments in Utils
     */
    protected ChapterList() {
        String pattern = Utils.getPattern();
        boolean patternIsEmpty = pattern == null || "".equals( pattern );
        this.cp = patternIsEmpty ? null : new ChapterPattern( pattern );
    }

    /**
     * adds a chapter to this list if the ChapterPattern is null (none defined) or the given number is contained in the
     * ChapterPattern
     * 
     * @param number
     *            the chapter's number
     * @param chapterUrl
     *            the chapter's URL
     */
    protected void addChapter( String number, URL chapterUrl ) {
        if ( this.cp == null || this.cp.contains( number ) ) {
            super.add( new Chapter( number, chapterUrl ) );
        }
    }

    /**
     * returns the name of the manga. used for the target-folder
     */
    public abstract String getMangaName();

    /**
     * returns the {@link URL} of one page's image
     */
    public abstract URL imgLink( URL url ) throws IOException;

    /**
     * returns all page-{@link URL}s of one chapter
     */
    public abstract Map<Integer, URL> getAllPageURLs( URL url ) throws IOException;

    /**
     * gets all URLs for one Chapter
     */
    public Map<Integer, URL> getAllPageURLs( Chapter c ) throws IOException {
        return getAllPageURLs( c.chapterUrl );
    }

    /**
     * a class to
     * 
     * @author HerrLock
     */
    public static class Chapter {
        final String number;
        final URL chapterUrl;

        Chapter( String number, URL url ) {
            this.number = number;
            this.chapterUrl = url;
        }

        public final String getNumber() {
            return this.number;
        }

        @Override
        public String toString() {
            return MessageFormat.format( "{0}: {1}", this.number, this.chapterUrl );
        }
    }

    /**
     * A List of Strings that is used to
     * 
     * @author HerrLock
     */
    private static class ChapterPattern extends ArrayList<String> {
        private static final long serialVersionUID = 1L;
        /**
         * the regex for the patterns<br>
         * accepts "a list of strings seperated by semicolons"
         */
        private static final Pattern REGEX = Pattern.compile( "([^;]+;)+[^;]+" );

        /**
         * a valid pattern consists of the chapter-numbers seperated by semicolon, or an interval of chapters, defined by the
         * first chapter, a hyphen and the last chapter<br>
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
        ChapterPattern( String pattern ) {
            // accept only if valid
            if ( REGEX.matcher( pattern ).matches() ) {
                // split string at ';'
                for ( String s : pattern.split( ";" ) ) {
                    String[] chapter = s.split( "-" );
                    if ( chapter.length == 1 ) {
                        // a single chapter
                        super.add( s );
                    } else if ( chapter.length == 2 ) {
                        // an interval of chapters
                        int first = Integer.parseInt( chapter[0] );
                        int last = Integer.parseInt( chapter[1] );
                        for ( int i = first; i <= last; i++ ) {
                            super.add( i + "" );
                        }
                    } else {
                        throw new RuntimeException( "chapterPattern is invalid" );
                    }
                }
            }
        }
    }

}
