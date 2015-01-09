package de.herrlock.manga.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum Logger {
    L;

    static final int ERROR = 10;
    static final int WARNING = 100;
    static final int INFO = 1_000;
    static final int DEBUG = 10_000;
    static final int TRACE = 100_000;
    static final int CURRENT_LEVEL;
    static {
        char current;
        try (InputStream in = new FileInputStream("src/main/resources/log.properties")) {
            Properties p = new Properties();
            p.load(in);
            current = p.getProperty("level", "T").charAt(0);
        }
        catch (NullPointerException ex) {
            current = 'T';
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        switch (current) {
            case 'e':
            case 'E':
                CURRENT_LEVEL = ERROR;
                break;
            case 'w':
            case 'W':
                CURRENT_LEVEL = WARNING;
                break;
            case 'i':
            case 'I':
                CURRENT_LEVEL = INFO;
                break;
            case 's':
            case 'D':
                CURRENT_LEVEL = DEBUG;
                break;
            case 't':
            case 'T':
                CURRENT_LEVEL = TRACE;
                break;
            default:
                CURRENT_LEVEL = TRACE;
                break;
        }
    }

    private static String spaces = "";

    public Logger addSpace() {
        spaces = spaces + ' ';
        return this;
    }

    public Logger removeSpace() {
        spaces = spaces.substring(0, spaces.length() - 1);
        return this;
    }

    public Logger error(Object message) {
        log(">>>>> " + message, ERROR);
        return this;
    }

    public Logger warn(Object message) {
        log(">>>> " + message, WARNING);
        return this;
    }

    public Logger info(Object message) {
        log(">>> " + message, INFO);
        return this;
    }

    public Logger debug(Object message) {
        log(">> " + message, DEBUG);
        return this;
    }

    public Logger trace(Object message) {
        log("> " + message, TRACE);
        return this;
    }

    private static void log(Object message, int level) {
        if (CURRENT_LEVEL >= level) {
            System.out.println(spaces + message);
        }
    }

    private Logger() {
        // not called
    }

}
