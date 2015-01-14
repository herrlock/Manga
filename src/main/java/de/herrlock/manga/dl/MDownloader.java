package de.herrlock.manga.dl;

import static de.herrlock.manga.util.log.LogInitializer.L;

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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import de.herrlock.manga.host.ChapterList;
import de.herrlock.manga.host.ChapterList.Chapter;
import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.Utils;
import de.herrlock.manga.util.log.LogInitializer;

public class MDownloader {

    /**
     * a Scanner to {@link System.in}
     */
    private static Scanner sc;
    /**
     * the properties read from downloader.properties
     */
    private static Map<String, String> arguments;

    public static void execute(Map<String, String> arg) {
        MDownloader.arguments = arg;
        Utils.init(arg);
        LogInitializer.init(arg.get(Constants.PARAM_LOGLEVEL));

        try {
            L.trace();
            try (Scanner _sc = new Scanner(System.in, "UTF-8")) {
                sc = _sc;
                new MDownloader().run();
            }
        }
        catch (RuntimeException ex) {
            L.error(ex);
            throw ex;
        }
        catch (Exception ex) {
            L.error(ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * the URL containing the
     */
    private final URL url;
    /**
     * the pattern of chapters to download
     */
    private final String pattern;

    /**
     * the parent-folder to write the pages into
     */
    private File path;
    /**
     * a {@link ChapterList}-Instance containing the {@link URL}s of the {@link Chapter}s
     */
    private ChapterList cl;
    /**
     * a {@link Map} containing the {@link URL}s of all the pages
     */
    private Map<String, Map<Integer, URL>> pm;
    /**
     * the chapters that failed the download
     */
    private List<DoLaterChapter> doAfterwards = new ArrayList<>(0);

    MDownloader() {
        this.url = Utils.getURL(arguments);

        L.none("Read URL " + this.url.toExternalForm());
        this.pattern = arguments.get(Constants.PARAM_PATTERN);
    }

    private void run() {
        L.trace();
        try {
            createChapterList();
            if (goon1()) {
                createPictureLinks();
                if (goon2()) {
                    downloadAll();
                }
                else {
                    L.none("bye");
                }
            }
            else {
                L.none("bye");
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean goon1() {
        int noOfChapters = this.cl.size();
        if (noOfChapters > 0) {
            L.none(noOfChapters + " chapter" + (noOfChapters > 1 ? "s" : "") + " availabile.");
            return goon();
        }
        L.warn("no chapters availabile, exiting");
        return false;
    }

    private boolean goon2() {
        int noOfPictures = 0;
        for (Map<Integer, URL> m : this.pm.values()) {
            noOfPictures += m.size();
        }
        if (noOfPictures > 0) {
            L.none(noOfPictures + " page" + (noOfPictures > 1 ? "s" : "") + " availabile.");
            return goon();
        }
        L.warn("no pictures availabile, exiting");
        return false;
    }

    private static boolean goon() {
        L.none("go on? y|n");
        try {
            char c = sc.next(".+").charAt(0);
            return c == 'y' || c == 'Y';
        }
        catch (NoSuchElementException ex) {
            return false;
        }
    }

    private void createChapterList() throws IOException {
        L.trace();
        this.cl = ChapterList.getInstance(this.url, this.pattern);

        String mangaName = this.cl.getMangaName().toLowerCase(Locale.ENGLISH).replace(' ', '_');
        this.path = new File(Constants.TARGET_FOLDER, mangaName);
        L.none("Save to: " + this.path.getAbsolutePath());

    }

    private void createPictureLinks() throws IOException {
        L.trace();
        if (this.cl != null) {
            this.pm = new HashMap<>(this.cl.size());
            for (Chapter chapter : this.cl) {
                Map<Integer, URL> pictureMap = this.cl.getAllPageURLs(chapter);
                this.pm.put(chapter.number, pictureMap);
            }
        }
        else {
            String message = "ChapterList not initialized";
            L.error(message);
            throw new RuntimeException(message);
        }
    }

    private void downloadAll() throws IOException {
        L.trace();
        if (this.pm != null) {
            List<String> keys = new ArrayList<>(this.pm.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                downloadChapter(key);
            }
        }
        else {
            String message = "PageMap not initialized";
            L.error(message);
            throw new RuntimeException(message);
        }
    }

    private void downloadChapter(String key) throws IOException {
        Map<Integer, URL> urlMap = this.pm.get(key);
        L.none("Download chapter " + key + " - " + urlMap.size() + " pages");
        File chapterFolder = new File(this.path, key);
        if (chapterFolder.exists() || chapterFolder.mkdirs()) {
            for (Map.Entry<Integer, URL> e : urlMap.entrySet()) {
                dlPic(e.getValue(), chapterFolder, e.getKey());
            }
            downloadFailedPages();
        }
        L.none("finished chapter " + key);
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
        URL imageUrl = this.cl.imgLink(pageUrl);
        URLConnection con = Utils.getConnection(imageUrl);
        try (InputStream in = con.getInputStream()) {
            L.debug("read image " + imageUrl);
            BufferedImage image = ImageIO.read(in);
            File output = new File(chapterFolder, pageNumber + ".jpg");
            L.debug("write to " + output);
            ImageIO.write(image, "jpg", output);
            L.info("Chapter " + chapterFolder.getName() + ", Page " + pageNumber + " - finished");
        }
        catch (SocketException | SocketTimeoutException ex) {
            L.warn("Chapter " + chapterFolder.getName() + ", Page " + pageNumber + " - " + ex.getMessage());
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
