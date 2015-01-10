package de.herrlock.manga.std;

import static de.herrlock.manga.util.Logger.L;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import de.herrlock.manga.host.ChapterList;
import de.herrlock.manga.host.ChapterList.Chapter;
import de.herrlock.manga.util.Constants;

public class Ctrl {
    private static final String f = "C:\\Users\\Jan\\Desktop\\mangatest\\";
    private static final int urlIndex = 1;

    public static void main(String[] args) {
        L.trace();
        new Ctrl().run();
    }

    /**
     * the parent-folder to write the pages into
     */
    private File path;
    /**
     * the URL containing the
     */
    private URL url;
    /**
     * a Scanner to {@link System.in}
     */
    private Scanner sc;

    /**
     * a {@link ChapterList}-Instance containing the {@link URL}s of the {@link Chapter}s
     */
    private ChapterList cl;
    /**
     * a {@link Map} containing the {@link URL}s of all the pages
     */
    private Map<String, List<URL>> pm;

    /**
     * the chapters that failed the download
     */
    private List<DoLaterChapter> doAfterwards = new ArrayList<>();

    public Ctrl() {
        try {
            this.path = new File(f);
            this.url = new URL(Constants.EXAMPLE_URLS[urlIndex]);
        }
        catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void run() {
        L.trace();
        try (Scanner scx = new Scanner(System.in)) {
            this.sc = scx;
            createChapterList();
            if (goon1()) {
                createPictureLinks();
                if (goon2()) {
                    downloadAll();
                }
                else {
                    System.out.println("bye");
                }
            }
            else {
                System.out.println("bye");
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean goon1() {
        int noOfChapters = this.cl.size();
        if (noOfChapters > 0) {
            System.out.println(noOfChapters + " chapter" + (noOfChapters > 1 ? "s" : "") + " availabile.");
            return goon();
        }
        L.warn("no chapters availabile, exiting");
        return false;
    }

    private boolean goon2() {
        int noOfPictures = 0;
        for (Collection<URL> c : this.pm.values()) {
            noOfPictures += c.size();
        }
        if (noOfPictures > 0) {
            System.out.println(noOfPictures + " page" + (noOfPictures > 1 ? "s" : "") + " availabile.");
            return goon();
        }
        L.warn("no pictures availabile, exiting");
        return false;
    }

    private boolean goon() {
        System.out.println("go on? y|n");
        char c = this.sc.next(".+").charAt(0);
        return c == 'y' || c == 'Y';
    }

    private void createChapterList() throws IOException {
        L.trace();
        this.cl = ChapterList.getInstance(this.url, null);
    }

    private void createPictureLinks() throws IOException {
        L.trace();
        if (this.cl != null) {
            this.pm = new HashMap<>(this.cl.size());
            for (Chapter chapter : this.cl) {
                List<URL> pictureList = new ArrayList<>(this.cl.getAllPageURLs(chapter).values());
                this.pm.put(chapter.number, pictureList);
            }
        }
        else {
            L.error("ChapterList not initialized");
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
            L.error("ChapterList not initialized");
        }
    }

    private void downloadChapter(String key) throws IOException {
        L.trace("dl chapter " + key);
        File chapterFolder = new File(this.path, key);
        if (chapterFolder.exists() || chapterFolder.mkdirs()) {
            List<URL> list = this.pm.get(key);
            for (int i = 0, l = list.size(); i < l; i++) {
                dlPic(list.get(i), chapterFolder, i);
            }
            downloadFailed();
        }
        L.info("finished chapter " + key);
    }

    private void downloadFailed() throws IOException {
        List<DoLaterChapter> list = new ArrayList<>(this.doAfterwards.size());
        Collections.copy(list, this.doAfterwards);
        this.doAfterwards = new ArrayList<>();
        for (DoLaterChapter c : list) {
            dlPic(c.pageUrl, c.chapterFolder, c.pageNumber);
        }
        if (!this.doAfterwards.isEmpty()) {
            downloadFailed();
        }
    }

    private void dlPic(URL pageUrl, File chapterFolder, int pageNumber) throws IOException {
        try {
            URL imageUrl = this.cl.imgLink(pageUrl);
            URLConnection con = imageUrl.openConnection();
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            InputStream in = con.getInputStream();
            L.trace("read image " + imageUrl);
            BufferedImage image = ImageIO.read(in);
            File output = new File(chapterFolder, pageNumber + ".jpg");
            L.trace("write to " + output);
            ImageIO.write(image, "jpg", output);
        }
        catch (SocketException | SocketTimeoutException ex) {
            L.warn(ex.getMessage());
            L.warn(chapterFolder.getName() + " / " + pageNumber);
            this.doAfterwards.add(new DoLaterChapter(pageUrl, chapterFolder, pageNumber));
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
}
