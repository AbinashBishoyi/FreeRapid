package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.plugins.exceptions.NotSupportedDownloadByServiceException;
import org.java.plugin.Plugin;
import org.java.plugin.registry.PluginAttribute;
import org.java.plugin.registry.PluginDescriptor;

import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author Vity
 */
public abstract class AbstractFileShareService extends Plugin implements ShareDownloadService {
    private final static Logger logger = Logger.getLogger(AbstractFileShareService.class.getName());

    private Pattern pattern;
    private PluginContext pluginContext;

    public AbstractFileShareService() {
        super();
    }

    protected void doStart() throws Exception {
        final PluginDescriptor desc = this.getDescriptor();
        final PluginAttribute attribute = desc.getAttribute("urlRegex");
        pattern = Pattern.compile(attribute.getValue(), Pattern.CASE_INSENSITIVE);
    }

    protected void doStop() throws Exception {

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
        if (!supportURL(downloader.getDownloadFile().getFileUrl().toExternalForm())) {
            throw new NotSupportedDownloadByServiceException();
        }
    }

    public boolean supportsRunCheck() {
        return false;
    }

    public void runCheck(HttpFileDownloader downloader) throws Exception {

    }

    public void showOptions() throws Exception {

    }

    public PluginContext getPluginContext() {
        return pluginContext;
    }

    public void setPluginContext(PluginContext pluginContext) {
        this.pluginContext = pluginContext;
    }
}
