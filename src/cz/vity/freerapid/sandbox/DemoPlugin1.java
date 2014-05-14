package cz.vity.freerapid.sandbox;

import org.java.plugin.Plugin;

/**
 * @author Ladislav Vitasek
 */
public class DemoPlugin1 extends Plugin implements DemoPluginTool {

    @Override
    protected void doStart() throws Exception {
        System.out.println("Plugin je inicializovan");
    }

    @Override
    protected void doStop() throws Exception {
        System.out.println("Plugin je ukoncovan");
    }

    @Override
    public String[] getFavoriteURLLinks() {
        return new String[]{"http://google.com", "http://seznam.cz"};
    }
}
