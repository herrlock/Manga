package de.herrlock.manga.downloader;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.herrlock.log.Logger;
import de.herrlock.manga.host.ChapterList;
import de.herrlock.manga.host.ChapterList.Chapter;
import de.herrlock.manga.util.Utils;

class PictureMapContainer {

    /**
     * Logger
     */
    private static final Logger log = Utils.getLogger();
    /**
     * a {@link Map} containing the {@link URL}s of all the pages
     */
    private Map<String, Map<Integer, URL>> picturemap;

    public PictureMapContainer(ChapterListContainer clc) throws IOException {
        log.trace();
        ChapterList chapterlist = clc.getChapterlist();
        if (chapterlist != null) {
            this.picturemap = new HashMap<>(chapterlist.size());
            for (Chapter chapter : chapterlist) {
                Map<Integer, URL> pageMap = chapterlist.getAllPageURLs(chapter);
                this.picturemap.put(chapter.getNumber(), pageMap);
            }
        }
        else {
            String message = "ChapterList not initialized";
            log.error(message);
            throw new RuntimeException(message);
        }
    }

    public final Map<String, Map<Integer, URL>> getPictureMap() {
        return this.picturemap;
    }

    public final int getSize() {
        int noOfPictures = 0;
        for (Map<Integer, URL> m : this.picturemap.values()) {
            noOfPictures += m.size();
        }
        return noOfPictures;
    }

    public final Map<Integer, URL> getUrlMap(String key) {
        return this.picturemap.get(key);
    }

}
