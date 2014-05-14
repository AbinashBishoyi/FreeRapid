package cz.vity.freerapid.plugins;

import org.java.plugin.Plugin;

import java.util.logging.Logger;

/**
 * Basic support impl class for Library plugin
 * @since 0.85
 * @author Vity
 */
public class LibraryPlugin extends Plugin {
    /**
     * Field logger
     */
    private final static Logger logger = Logger.getLogger(LibraryPlugin.class.getName());

    /**
     * Constructor 
     */
    public LibraryPlugin() {
        super();
    }

    @Override
    protected void doStart() throws Exception {
        logger.info("Plugin loaded");
    }

    protected void doStop() throws Exception {
        logger.info("Plugin stopped");
    }
}