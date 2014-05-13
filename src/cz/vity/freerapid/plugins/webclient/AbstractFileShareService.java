package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.plugins.exceptions.NotSupportedDownloadByServiceException;
import org.java.plugin.Plugin;
import org.java.plugin.registry.PluginAttribute;
import org.java.plugin.registry.PluginDescriptor;

import java.util.regex.Pattern;

/**
 * @author Vity
 */
public abstract class AbstractFileShareService extends Plugin implements ShareDownloadService {
    private Pattern pattern;


    public AbstractFileShareService() {
        super();
    }

    protected void doStart() throws Exception {
        final PluginDescriptor desc = this.getDescriptor();
        final PluginAttribute attribute = desc.getAttribute("urlRegex");
        System.out.println("attribute = " + attribute.getValue());
        pattern = Pattern.compile(attribute.getValue(), Pattern.CASE_INSENSITIVE);
    }

    protected void doStop() throws Exception {

    }

    public String getId() {
        return this.getDescriptor().getId();
    }

    protected boolean supportURL(String url) {
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
}
