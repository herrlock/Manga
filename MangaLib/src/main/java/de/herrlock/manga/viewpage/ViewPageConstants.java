package de.herrlock.manga.viewpage;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.annotations.VisibleForTesting;

final class ViewPageConstants {

    @VisibleForTesting
    static String formatManganame( final String foldername ) {
        String mangarawname;
        if ( foldername.matches( ".+_\\d+" ) ) {
            int lastUnderscore = foldername.lastIndexOf( '_' );
            if ( lastUnderscore > -1 ) {
                mangarawname = foldername.substring( 0, lastUnderscore );
            } else {
                mangarawname = foldername;
            }
        } else {
            mangarawname = foldername;
        }
        return mangarawname.replace( '_', ' ' ).trim();
    }

    /**
     * compares two {@code Entry<Integer, ?>}-objects by their keys
     */
    public static final Comparator<Map.Entry<Integer, ?>> INTEGER_ENTRY_COMPARATOR = new Comparator<Entry<Integer, ?>>() {
        @Override
        public int compare( final Entry<Integer, ?> o1, final Entry<Integer, ?> o2 ) {
            return o1.getKey().compareTo( o2.getKey() );
        }
    };

    /**
     * Compares two {@link File}s by their name. Uses {@link NumericStringComparator} for the actual comparison.
     */
    public static final Comparator<File> NUMERIC_FILENAME_COMPARATOR = new Comparator<File>() {
        @Override
        public int compare( final File o1, final File o2 ) {
            String name1 = o1.getName();
            String name2 = o2.getName();
            return NUMERIC_STRING_COMPARATOR.compare( name1, name2 );
        }
    };

    /**
     * Compares two {@link Path}s by their filename. Uses {@link NumericStringComparator} for the actual comparison.
     */
    public static final Comparator<Path> NUMERIC_PATHNAME_COMPARATOR = new Comparator<Path>() {
        @Override
        public int compare( final Path o1, final Path o2 ) {
            String name1 = String.valueOf( o1.getFileName() );
            String name2 = String.valueOf( o2.getFileName() );
            return NUMERIC_STRING_COMPARATOR.compare( name1, name2 );
        }
    };

    /**
     * Compares two {@link String}s numerically.<br>
     * Tries to use {@linkplain Integer#parseInt(String)} on both Strings and returns {@linkplain Integer#compare(int, int)}. If
     * that fails it tries {@linkplain Double#parseDouble(String)} and returns {@linkplain Double#compare(double, double)}
     */
    public static final Comparator<String> NUMERIC_STRING_COMPARATOR = new Comparator<String>() {
        @Override
        public int compare( final String name1, final String name2 ) {
            try {
                int i1 = Integer.parseInt( name1 );
                int i2 = Integer.parseInt( name2 );
                return Integer.compare( i1, i2 );
            } catch ( final NumberFormatException ex ) {
                double d1 = Double.parseDouble( name1 );
                double d2 = Double.parseDouble( name2 );
                return Double.compare( d1, d2 );
            }
        }
    };

    public static final class FileIsDirectoryFilter implements FileFilter {
        @Override
        public boolean accept( final File pathname ) {
            return pathname.isDirectory();
        }
    }

    public static class PathIsDirectoryFilter implements Filter<Path> {
        @Override
        public boolean accept( final Path entry ) throws IOException {
            return Files.isDirectory( entry );
        }
    }

    private ViewPageConstants() {
        // not used
    }

}
