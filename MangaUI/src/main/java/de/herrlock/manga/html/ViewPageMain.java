package de.herrlock.manga.html;

import java.io.File;

public final class ViewPageMain {
    public static void main( String[] args ) {
        ViewPage.execute( new File( "./download/log_horizon_150305201040" ) );
    }

    private ViewPageMain() {
        // nothing to do
    }
}
