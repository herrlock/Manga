package de.herrlock.manga.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

public enum Logger {
    L;

    static final int NONE = 1;
    static final int ERROR = 10;
    static final int WARNING = 100;
    static final int INFO = 1_000;
    static final int DEBUG = 10_000;
    static final int TRACE = 100_000;
    static final int CURRENT_LEVEL;
    static {
        char current;
        try (InputStream in = Logger.class.getResourceAsStream("log.properties")) {
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
            case 'n':
            case 'N':
                CURRENT_LEVEL = NONE;
                break;
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
            case 'd':
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

    /**
     * logs with level {@link Logger#ERROR}
     * 
     * @param message
     *            the message to log
     * @return {@link Logger#L}, to allow methodChaining
     */
    public Logger error(Object message) {
        log(">>>>> " + message, ERROR);
        return this;
    }

    /**
     * logs with level {@link Logger#WARNING}
     * 
     * @param message
     *            the message to log
     * @return {@link Logger#L}, to allow methodChaining
     */
    public Logger warn(Object message) {
        log(">>>> " + message, WARNING);
        return this;
    }

    /**
     * logs with level {@link Logger#INFO}
     * 
     * @param message
     *            the message to log
     * @return {@link Logger#L}, to allow methodChaining
     */
    public Logger info(Object message) {
        log(">>> " + message, INFO);
        return this;
    }

    /**
     * logs with level {@link Logger#DEBUG}
     * 
     * @param message
     *            the message to log
     * @return {@link Logger#L}, to allow methodChaining
     */
    public Logger debug(Object message) {
        log(">> " + message, DEBUG);
        return this;
    }

    /**
     * logs with level {@link Logger#TRACE}
     * 
     * @param message
     *            the message to log
     * @return {@link Logger#L}, to allow methodChaining
     */
    public Logger trace(Object message) {
        log("> " + message, TRACE);
        return this;
    }

    public Logger trace(String format, Class<?> c, String... parts) {
        Object[] arguments = new String[parts.length + 1];
        arguments[0] = c.getName();
        System.arraycopy(parts, 0, arguments, 1, parts.length);
        log(MessageFormat.format(format, arguments), TRACE);
        return this;
    }

    public Logger trace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length >= 3) {
            log(stackTrace[2].getClassName() + '.' + stackTrace[2].getMethodName(), TRACE);
        }
        return this;
    }

    private static void log(Object message, int level) {
        if (CURRENT_LEVEL >= level) {
            System.out.println(message);
        }
    }

    private Logger() {
        // not called
    }

}
