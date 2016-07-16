package de.herrlock.manga.html;

import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

final class ViewPageConstants {
    /**
     * a {@linkplain FileFilter} that filters directories.<br>
     * {@linkplain FileFilter#accept(File)} returns true, if {@linkplain File#isDirectory()} is {@code true}
     */
    static final FileFilter isDirectoryFilter = new FileFilter() {
        @Override
        public boolean accept( final File pathname ) {
            return pathname.isDirectory();
        }
    };

    /**
     * copmares two {@link File}s by their name.<br>
     * tries to use {@linkplain Integer#parseInt(String)} on both names and returns {@linkplain Integer#compare(int, int)}. If
     * that fails it tries {@linkplain Double#parseDouble(String)} and returns {@linkplain Double#compare(double, double)}
     */
    static final Comparator<File> numericFilenameComparator = new Comparator<File>() {
        @Override
        public int compare( final File o1, final File o2 ) {
            String name1 = o1.getName();
            String name2 = o2.getName();
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

    /**
     * compares two {@code Entry<Integer, ?>}-objects by their keys
     */
    static final Comparator<Entry<Integer, ?>> integerEntryComparator = new Comparator<Map.Entry<Integer, ?>>() {
        @Override
        public int compare( final Entry<Integer, ?> o1, final Entry<Integer, ?> o2 ) {
            return o1.getKey().compareTo( o2.getKey() );
        }
    };

    private ViewPageConstants() {
        // not used
    }
}
