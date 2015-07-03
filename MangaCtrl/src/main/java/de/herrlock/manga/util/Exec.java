package de.herrlock.manga.util;

import de.herrlock.manga.downloader.DialogDownloader;
import de.herrlock.manga.downloader.GUIDownloader;
import de.herrlock.manga.downloader.PlainDownloader;
import de.herrlock.manga.host.PrintAllHoster;
import de.herrlock.manga.html.ViewPageMain;
import de.herrlock.manga.jd.JDExport;

public abstract class Exec {
    public static final Exec DIALOG_DOWNLOADER = new Exec() {
        @Override
        public void execute() {
            DialogDownloader.execute();
        }
    }, PLAIN_DOWNLOADER = new Exec() {
        @Override
        public void execute() {
            PlainDownloader.execute();
        }
    }, GUI_DOWNLOADER = new Exec() {
        @Override
        public void execute() {
            GUIDownloader.execute();
        }
    }, ADD_TO_JD_W_FILE = new Exec() {
        @Override
        public void execute() {
            JDExport.executeGetFileProperties();
        }
    }, ADD_TO_JD_W_GUI = new Exec() {
        @Override
        public void execute() {
            JDExport.executeGetGuiProperties();
        }
    }, VIEW_PAGE_MAIN = new Exec() {
        @Override
        public void execute() {
            ViewPageMain.execute();
        }
    }, PRINT_ALL_HOSTER = new Exec() {
        @Override
        public void execute() {
            PrintAllHoster.execute();
        }
    }, DO_NOTHING = new Exec() {
        @Override
        public void execute() {
            // do nothing :)
        }
    };

    public abstract void execute();
}
