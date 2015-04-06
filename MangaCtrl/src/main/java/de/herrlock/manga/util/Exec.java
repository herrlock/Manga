package de.herrlock.manga.util;

import de.herrlock.manga.downloader.DialogDownloader;
import de.herrlock.manga.host.PrintAllHoster;
import de.herrlock.manga.html.ViewPageMain;
import de.herrlock.manga.jd.JDExport;

public enum Exec {
    DIALOG_DOWNLOADER {
        @Override
        public void execute() {
            DialogDownloader.execute();
        }
    },
    ADD_TO_JD {
        @Override
        public void execute() {
            JDExport.execute();
        }
    },
    VIEW_PAGE_MAIN {
        @Override
        public void execute() {
            ViewPageMain.execute();
        }
    },
    PRINT_ALL_HOSTER {
        @Override
        public void execute() {
            PrintAllHoster.execute();
        }
    },
    DO_NOTHING {
        @Override
        public void execute() {
            // do nothing :)
        }
    };

    public abstract void execute();
}
