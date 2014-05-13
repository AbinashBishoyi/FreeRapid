package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.plugins.exceptions.NotSupportedDownloadByServiceException;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.interfaces.*;
import cz.vity.freerapid.utilities.LogUtils;
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
                if (resource == null) {
                    logger.warning("Icon image for plugin '" + desc.getId() + "' was not found");
                } else {
                    try {
                        image = new ImageIcon(ImageIO.read(resource));
                    } catch (IOException e) {
                        logger.warning("Icon image for plugin '" + desc.getId() + "' reading failed");
                    }
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
        return pattern == null || pattern.matcher(url).matches();
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
     * Shows standard account dialog with given account
     *
     * @param account          account with user name and password
     * @param dialogTitle      title for dialog
     * @param pluginConfigFile file name for storing configuration
     * @return returns account parametr, if user pressed Cancel button, otherwise it returns updated account instance
     */
    protected PremiumAccount showAccountDialog(final PremiumAccount account, String dialogTitle, final String pluginConfigFile) {
        final DialogSupport dialogSupport = getPluginContext().getDialogSupport();
        try {//saving new username/password
            final PremiumAccount pa = dialogSupport.showAccountDialog(account, dialogTitle);//vysledek bude Premium ucet - Rapidshare
            if (pa != null) {
                getPluginContext().getConfigurationStorageSupport().storeConfigToFile(pa, pluginConfigFile);
                return pa;//return new username/password
            }
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
        return account;
    }

    /**
     * Loads PremiumAccount information from file.<br>
     * Returns new PremiumAccount instance if there is no configuration file yet.
     *
     * @param pluginConfigFile file name of configuration file
     * @return instance of PremiumAccount - loaded from file or new instance if there is no configuration on disk yet
     */
    protected PremiumAccount getAccountConfigFromFile(final String pluginConfigFile) {
        if (getPluginContext().getConfigurationStorageSupport().configFileExists(pluginConfigFile)) {
            try {
                return getPluginContext().getConfigurationStorageSupport().loadConfigFromFile(pluginConfigFile, PremiumAccount.class);
            } catch (Exception e) {
                LogUtils.processException(logger, e);
                return new PremiumAccount();
            }
        } else {
            return new PremiumAccount();
        }
    }


    /**
     * Returns new instance of "plugin's worker" - its methods are called from this class
     * Instance should not be cached. It should return always new instance.
     *
     * @return instance of PluginRunner
     */
    protected abstract PluginRunner getPluginRunnerInstance();
}
