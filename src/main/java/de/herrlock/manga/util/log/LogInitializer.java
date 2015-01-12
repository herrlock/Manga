package de.herrlock.manga.util.log;

public class LogInitializer {
    public static Logger L = new Logger(null);

    private static boolean init;

    public static void init(String level) {
        if (!init) {
            L = new Logger(level);
            init = true;
        }
        else {
            System.out.println("Logger already initialized");
        }
    }

}
