package de.herrlock.manga.util;

public final class Constants {
    public static final boolean DEBUG = Math.random() < 2;

    public static final String SETTINGS_FILE = "./downloader.properties";
    public static final String PARAM_URL = "url";
    public static final String PARAM_PATTERN = "pattern";
    public static final String PARAM_LOGLEVEL = "loglevel";
    public static final String PARAM_FILEPATH = "filepath";
    public static final String PARAM_TIMEOUT = "timeout";
    public static final int PARAM_TIMEOUT_DEFAULT = 2_000;

    public static final String PARAM_PROXY_HOST = "proxyHost";
    public static final String PARAM_PROXY_PORT = "proxyPort";
    public static final String PARAM_PROXY_USER = "proxyUser";
    public static final String PARAM_PROXY_PASS = "proxyPass";

    public static final String TARGET_FOLDER = "./download";
    public static final String TRACE_FILE = "./log/trace.log";

    private Constants() {
        // not called
    }
}
