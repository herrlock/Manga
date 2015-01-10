package de.herrlock.manga;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import de.herrlock.manga.dl.MDownloader;
import de.herrlock.manga.util.Constants;

public class Ctrl {
    private static final File FILE = new File(Constants.SETTINGS_FILE);

    public static void main(String[] args) throws IOException {
        // read properties
        Properties p = new Properties();
        try (InputStream in = new FileInputStream(FILE)) {
            p.load(in);
        }

        // copy properties to map
        Map<String, String> arguments = new HashMap<>();
        for (String name : p.stringPropertyNames())
            arguments.put(name, p.getProperty(name));

        // validate arguments
        String[] requiredParameters = new String[] {
            Constants.PARAM_URL
        };
        for (String s : requiredParameters) {
            String value = arguments.get(s);
            if (value == null || "".equals(value)) {
                throw new RuntimeException("Please fill the field \"" + s + "\" in the file " + FILE.getAbsolutePath());
            }
        }

        // execute downloader
        MDownloader.execute(arguments);
    }
}
