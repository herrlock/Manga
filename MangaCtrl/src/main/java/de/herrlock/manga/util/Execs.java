package de.herrlock.manga.util;

import java.io.IOException;
import java.net.URISyntaxException;

import de.herrlock.javafx.handler.Exec;
import de.herrlock.manga.downloader.DialogDownloader;
import de.herrlock.manga.downloader.SettingsFileDownloader;
import de.herrlock.manga.exceptions.MDException;
import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.html.ViewPageMain;
import de.herrlock.manga.http.ServerMain;
import de.herrlock.manga.jd.JDExport;

public enum Execs implements Exec {
    DIALOG_DOWNLOADER() {
        @Override
        public void execute() {
            DialogDownloader.execute();
        }
    },
    SETTINGS_FILE_DOWNLOADER() {
        @Override
        public void execute() {
            SettingsFileDownloader.execute();
        }
    },
    START_SERVER() {
        @Override
        public void execute() {
            try {
                ServerMain.execute( true );
            } catch ( MDException | IOException | URISyntaxException ex ) {
                throw new MDRuntimeException( ex );
            }
        }
    },
    ADD_TO_JD_W_FILE() {
        @Override
        public void execute() {
            JDExport.executeGetFileProperties();
        }
    },
    VIEW_PAGE_MAIN() {
        @Override
        public void execute() {
            ViewPageMain.execute();
        }
    },
    DO_NOTHING() {
        @Override
        public void execute() {
            // do nothing :)
        }
    };

}
