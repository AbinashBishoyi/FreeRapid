package cz.vity.freerapid.plugins.directdownload;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * @author ntoskrnl
 */
public class DirectDownloadServiceImpl extends AbstractFileShareService {
    private final static String NAME = "direct";

    @Override
    public String getName() {
        return NAME;
    }

    public static String getNameStatic() {
        return NAME;
    }

    @Override
    public int getMaxDownloadsFromOneIP() {
        return 100;
    }

    @Override
    public boolean supportsRunCheck() {
        return false;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new DirectDownloadRunner();
    }

}