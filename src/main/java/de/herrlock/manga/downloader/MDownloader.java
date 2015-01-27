package de.herrlock.manga.downloader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import de.herrlock.log.Logger;
import de.herrlock.manga.util.Utils;

public class MDownloader {

    /**
     * a Scanner to {@link System.in}
     */
    private static Scanner sc;
    /**
     * the Logger
     */
    private static Logger log;

    public static void execute(InputStream in) {
        log = Utils.getLogger();
        log.trace();
        MDownloader md = new MDownloader();
        try (Scanner _sc = new Scanner(in, "UTF-8")) {
            sc = _sc;
            md.run();
        }
        catch (RuntimeException ex) {
            log.error(ex);
            throw ex;
        }
        catch (Exception ex) {
            log.error(ex);
            throw new RuntimeException(ex);
        }
    }

    ChapterListContainer clc;
    PictureMapContainer pmc;

    /**
     * the chapters that failed the download
     */
    private List<DoLaterChapter> doAfterwards = new ArrayList<>(0);

    MDownloader() {
        // nothing to init
    }

    private void run() {
        log.trace();
        try {
            this.clc = new ChapterListContainer();
            if (goon1()) {
                this.pmc = new PictureMapContainer(this.clc);
                if (goon2()) {
                    downloadAll();
                }
                else {
                    log.none("bye");
                }
            }
            else {
                log.none("bye");
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean goon1() {
        int noOfChapters = this.clc.getSize();
        if (noOfChapters > 0) {
            log.none(noOfChapters + " chapter" + (noOfChapters > 1 ? "s" : "") + " availabile.");
            return goon();
        }
        log.warn("no chapters availabile, exiting");
        return false;
    }

    private boolean goon2() {
        int noOfPictures = this.pmc.getSize();
        if (noOfPictures > 0) {
            log.none(noOfPictures + " page" + (noOfPictures > 1 ? "s" : "") + " availabile.");
            return goon();
        }
        log.warn("no pictures availabile, exiting");
        return false;
    }

    private static boolean goon() {
        log.none("go on? y|n");
        try {
            char c = sc.next(".+").charAt(0);
            return c == 'y' || c == 'Y';
        }
        catch (NoSuchElementException ex) {
            return false;
        }
    }

    private void downloadAll() throws IOException {
        log.trace();
        Map<String, Map<Integer, URL>> picturemap = this.pmc.getPictureMap();
        if (picturemap != null) {
            List<String> keys = new ArrayList<>(picturemap.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                downloadChapter(key);
            }
        }
        else {
            String message = "PageMap not initialized";
            log.error(message);
            throw new RuntimeException(message);
        }
    }

    private void downloadChapter(String key) throws IOException {
        Map<Integer, URL> urlMap = this.pmc.getUrlMap(key);
        log.none("Download chapter " + key + " - " + urlMap.size() + " pages");
        File chapterFolder = new File(this.clc.getPath(), key);
        if (chapterFolder.exists() || chapterFolder.mkdirs()) {
            for (Map.Entry<Integer, URL> e : urlMap.entrySet()) {
                dlPic(e.getValue(), chapterFolder, e.getKey());
            }
            downloadFailedPages();
        }
        log.none("finished chapter " + key);
    }

    private void downloadFailedPages() throws IOException {
        List<DoLaterChapter> list = new ArrayList<>(this.doAfterwards);
        this.doAfterwards.clear();
        for (DoLaterChapter c : list) {
            dlPic(c.pageUrl, c.chapterFolder, c.pageNumber);
        }
        if (!this.doAfterwards.isEmpty()) {
            downloadFailedPages();
        }
    }

    private void dlPic(URL pageUrl, File chapterFolder, int pageNumber) throws IOException {
        URL imageUrl = this.clc.getImageLink(pageUrl);
        URLConnection con = Utils.getConnection(imageUrl);
        try (InputStream in = con.getInputStream()) {
            log.debug("read image " + imageUrl);
            BufferedImage image = ImageIO.read(in);
            File output = new File(chapterFolder, pageNumber + ".jpg");
            log.debug("write to " + output);
            ImageIO.write(image, "jpg", output);
            log.info("Chapter " + chapterFolder.getName() + ", Page " + pageNumber + " - finished");
        }
        catch (SocketException | SocketTimeoutException ex) {
            log.warn("Chapter " + chapterFolder.getName() + ", Page " + pageNumber + " - " + ex.getMessage());
            this.doAfterwards.add(new DoLaterChapter(pageUrl, chapterFolder, pageNumber));
        }
    }
}

class DoLaterChapter {
    final URL pageUrl;
    final File chapterFolder;
    final int pageNumber;

    public DoLaterChapter(URL pageUrl, File chapterFolder, int pageNumber) {
        this.pageUrl = pageUrl;
        this.chapterFolder = chapterFolder;
        this.pageNumber = pageNumber;
    }
}
