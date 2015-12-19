package de.herrlock.manga.util;

import de.herrlock.javafx.handler.Exec;
import de.herrlock.manga.downloader.DialogDownloader;
import de.herrlock.manga.downloader.PlainDownloader;
import de.herrlock.manga.host.PrintAllHoster;
import de.herrlock.manga.html.ViewPageMain;
import de.herrlock.manga.jd.JDExport;

public enum Execs implements Exec {
    DIALOG_DOWNLOADER() {
        @Override
        public void execute() {
            DialogDownloader.execute();
        }
    },
    PLAIN_DOWNLOADER() {
        @Override
        public void execute() {
            PlainDownloader.execute();
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
    PRINT_ALL_HOSTER() {
        @Override
        public void execute() {
            PrintAllHoster.execute();
        }
    },
    DO_NOTHING() {
        @Override
        public void execute() {
            // do nothing :)
        }
    };

}
