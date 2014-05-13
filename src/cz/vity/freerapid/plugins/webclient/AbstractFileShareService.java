package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.plugins.exceptions.NotSupportedDownloadByServiceException;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFileDownloader;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginContext;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;
import cz.vity.freerapid.plugins.webclient.interfaces.ShareDownloadService;
import org.java.plugin.Plugin;
import org.java.plugin.PluginClassLoader;
import org.java.plugin.registry.PluginAttribute;
import org.java.plugin.registry.PluginDescriptor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author Vity
 */
public abstract class AbstractFileShareService extends Plugin implements ShareDownloadService {
    private final static Logger logger = Logger.getLogger(AbstractFileShareService.class.getName());

    private Pattern pattern;
    private PluginContext pluginContext;
    private Icon image;

    public AbstractFileShareService() {
        super();
    }

    protected void doStart() throws Exception {
        final PluginDescriptor desc = this.getDescriptor();
        final PluginAttribute attribute = desc.getAttribute("urlRegex");
        pattern = Pattern.compile(attribute.getValue(), Pattern.CASE_INSENSITIVE);
        final PluginAttribute attr = desc.getAttribute("faviconImage");
        if (attr != null) {
            final PluginClassLoader loader = getManager().getPluginClassLoader(desc);
            if (loader != null) {
                final URL resource = loader.getResource(attr.getValue());
                if (resource == null)
                    logger.warning("Image was not found");
                try {
                    image = new ImageIcon(ImageIO.read(resource));
                } catch (IOException e) {
                    logger.warning("Image reading failed");
                }
            }
        }

    }

    protected void doStop() throws Exception {

    }

    public Icon getFaviconImage() {
        return image;
    }

    public String getId() {
        return this.getDescriptor().getId();
    }

    protected boolean supportURL(String url) {
        if (pattern == null) {
            logger.warning("Pattern for testing url was not initialized.");
            return true;
        }
        return pattern.matcher(url).matches();
    }

    @Override
    public String toString() {
        return getName();
    }

    public void run(HttpFileDownloader downloader) throws Exception {
        checkSupportedURL(downloader);
        final PluginRunner pluginRunner = getPluginRunnerInstance();
        if (pluginRunner != null) {
            pluginRunner.init(this, downloader);
            pluginRunner.run();
        } else throw new NullPointerException("getPluginRunnerInstance must no return null");

    }

    public void runCheck(HttpFileDownloader downloader) throws Exception {
        checkSupportedURL(downloader);
        final PluginRunner pluginRunner = getPluginRunnerInstance();
        if (pluginRunner != null) {
            pluginRunner.init(this, downloader);
            pluginRunner.runCheck();
        } else throw new NullPointerException("getPluginRunnerInstance must no return null");
    }

    public boolean supportsRunCheck() {
        return false;
    }

    public void showOptions() throws Exception {

    }

    public PluginContext getPluginContext() {
        return pluginContext;
    }

    protected void checkSupportedURL(HttpFileDownloader downloader) throws NotSupportedDownloadByServiceException {
        if (!supportURL(downloader.getDownloadFile().getFileUrl().toExternalForm())) {
            throw new NotSupportedDownloadByServiceException();
        }
    }

    public void setPluginContext(PluginContext pluginContext) {
        this.pluginContext = pluginContext;
    }

    protected abstract PluginRunner getPluginRunnerInstance();
}
