package de.herrlock.manga.util.log;

import java.text.MessageFormat;

public class Logger {
    static final int NONE = 1;
    static final int ERROR = 10;
    static final int WARNING = 100;
    static final int INFO = 1_000;
    static final int DEBUG = 10_000;
    static final int TRACE = 100_000;

    private final int level;

    Logger(String _level) {
        switch ((_level != null ? _level : "error").charAt(0)) {
            case 'n':
            case 'N':
                this.level = NONE;
                break;
            case 'e':
            case 'E':
                this.level = ERROR;
                break;
            case 'w':
            case 'W':
                this.level = WARNING;
                break;
            case 'i':
            case 'I':
                this.level = INFO;
                break;
            case 'd':
            case 'D':
                this.level = DEBUG;
                break;
            case 't':
            case 'T':
                this.level = TRACE;
                break;
            default:
                this.level = TRACE;
                break;
        }
    }

    /**
     * logs with level {@link Logger#NONE}<br>
     * these are essential messages
     * 
     * @param message
     *            the message to log
     * @return {@link LogInitializer#L}, to allow methodChaining
     */
    public Logger none(Object message) {
        log(message, NONE);
        return this;
    }

    /**
     * logs with level {@link Logger#ERROR}<br>
     * there are messages that indicate a thrown error
     * 
     * @param message
     *            the message to log
     * @return {@link LogInitializer#L}, to allow methodChaining
     */
    public Logger error(Object message) {
        log(">>>>> " + message, ERROR);
        return this;
    }

    /**
     * logs with level {@link Logger#WARNING}<br>
     * these are messages that indicate a caught error
     * 
     * @param message
     *            the message to log
     * @return {@link LogInitializer#L}, to allow methodChaining
     */
    public Logger warn(Object message) {
        log(">>>> " + message, WARNING);
        return this;
    }

    /**
     * logs with level {@link Logger#INFO}<br>
     * these are not important messages that are 'nice to read'
     * 
     * @param message
     *            the message to log
     * @return {@link LogInitializer#L}, to allow methodChaining
     */
    public Logger info(Object message) {
        log(">>> " + message, INFO);
        return this;
    }

    /**
     * logs with level {@link Logger#DEBUG}<br>
     * these are messages for debugging that do not have the purpose of tracing
     * 
     * @param message
     *            the message to log
     * @return {@link LogInitializer#L}, to allow methodChaining
     */
    public Logger debug(Object message) {
        log(">> " + message, DEBUG);
        return this;
    }

    /**
     * logs with level {@link Logger#TRACE}<br>
     * these are for tracing the flow of methods
     * 
     * @param message
     *            the message to log
     * @return {@link LogInitializer#L}, to allow methodChaining
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

    private void log(Object message, int l) {
        if (this.level >= l) {
            System.out.println(message);
        }
    }

}
