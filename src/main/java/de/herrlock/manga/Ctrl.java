package de.herrlock.manga;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import de.herrlock.manga.downloader.MDownloader;
import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.Utils;

public class Ctrl {

    public static void main(String[] args) throws IOException {
        new Ctrl().run();
    }

    private final InputStream in;

    public Ctrl() throws IOException {
        Properties p = new Properties();
        try (InputStream fIn = new FileInputStream(Constants.SETTINGS_FILE)) {
            p.load(fIn);
        }
        Map<String, String> args = new HashMap<>();
        for (String name : p.stringPropertyNames()) {
            args.put(name, p.getProperty(name));
        }
        String[] requiredParameters = new String[] {
            Constants.PARAM_URL
        };
        for (String s : requiredParameters) {
            String value = args.get(s);
            if (value == null || "".equals(value)) {
                throw new RuntimeException("Please fill the field \"" + s + "\" in the file " + new File(Constants.SETTINGS_FILE).getAbsolutePath());
            }
        }
        Utils.setArguments(args);
        this.in = System.in;
    }

    public void run() {
        MDownloader.execute(this.in);
    }
}
