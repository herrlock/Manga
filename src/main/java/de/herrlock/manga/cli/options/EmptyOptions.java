package de.herrlock.manga.cli.options;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * A set of {@linkplain Option}s
 * 
 * @author HerrLock
 */
public final class EmptyOptions extends SubOptions {

    private final Options options;

    /**
     * Create new Options
     */
    public EmptyOptions() {
        this.options = new Options();
    }

    /**
     * @return the Options from the class
     */
    @Override
    public Options getOptions() {
        return this.options;
    }

}
