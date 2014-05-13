package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.plugins.exceptions.NotSupportedDownloadByServiceException;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFileDownloadTask;
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
    /**
     * Field logger
     */
    private final static Logger logger = Logger.getLogger(AbstractFileShareService.class.getName());

    /**
     * Field pattern
     */
    private Pattern pattern;
    /**
     * Field pluginContext
     */
    private PluginContext pluginContext;
    /**
     * Field image
     */
    private Icon image;

    /**
     * Constructor AbstractFileShareService creates a new AbstractFileShareService instance.
     */
    public AbstractFileShareService() {
        super();
    }

    @Override
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

    @Override
    protected void doStop() throws Exception {

    }

    @Override
    public Icon getFaviconImage() {
        return image;
    }

    @Override
    public String getId() {
        return this.getDescriptor().getId();
    }

    /**
     * Method supportURL checks whether active plugin supports given URL
     *
     * @param url given URL to test
     * @return boolean true if plugin supports downloading from this URL
     */
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

    @Override
    public void run(HttpFileDownloadTask downloadTask) throws Exception {
        checkSupportedURL(downloadTask);
        final PluginRunner pluginRunner = getPluginRunnerInstance();
        if (pluginRunner != null) {
            pluginRunner.init(this, downloadTask);
            pluginRunner.run();
        } else throw new NullPointerException("getPluginRunnerInstance must no return null");

    }

    @Override
    public void runCheck(HttpFileDownloadTask downloadTask) throws Exception {
        checkSupportedURL(downloadTask);
        final PluginRunner pluginRunner = getPluginRunnerInstance();
        if (pluginRunner != null) {
            pluginRunner.init(this, downloadTask);
            pluginRunner.runCheck();
        } else throw new NullPointerException("getPluginRunnerInstance must no return null");
    }

    @Override
    public boolean supportsRunCheck() {
        return false;
    }

    @Override
    public void showOptions() throws Exception {

    }

    @Override
    public PluginContext getPluginContext() {
        return pluginContext;
    }

    /**
     * Method checkSupportedURL ...
     *
     * @param downloadTask
     * @throws NotSupportedDownloadByServiceException
     *          when
     */
    protected void checkSupportedURL(HttpFileDownloadTask downloadTask) throws NotSupportedDownloadByServiceException {
        if (!supportURL(downloadTask.getDownloadFile().getFileUrl().toExternalForm())) {
            throw new NotSupportedDownloadByServiceException();
        }
    }

    @Override
    public void setPluginContext(PluginContext pluginContext) {
        this.pluginContext = pluginContext;
    }

    /**
     * Returns new instance of "plugin's worker" - its methods are called from this class
     * Instance should not be cached. It should return always new instance.
     *
     * @return instance of PluginRunner
     */
    protected abstract PluginRunner getPluginRunnerInstance();
}
