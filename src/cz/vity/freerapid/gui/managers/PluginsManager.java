package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.plugins.devservices.filefactory.FileFactoryShareServiceImpl;
import cz.vity.freerapid.plugins.exceptions.NotSupportedDownloadServiceException;
import cz.vity.freerapid.plugins.webclient.ShareDownloadService;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import org.java.plugin.ObjectFactory;
import org.java.plugin.PluginManager;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.standard.StandardPluginLocation;
import org.jdesktop.application.ApplicationContext;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class PluginsManager {
    private final static Logger logger = Logger.getLogger(PluginsManager.class.getName());

    private Map<String, ShareDownloadService> loadedPlugins = new Hashtable<String, ShareDownloadService>();
    private final ApplicationContext context;


    public PluginsManager(ApplicationContext context) {
        this.context = context;
        loadPlugins();
    }


    private void loadPlugins() {

        PluginManager pluginManager = ObjectFactory.newInstance().createManager();


        File pluginsDir = new File(Utils.getAppPath(), "plugins");

        File[] plugins = pluginsDir.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".zip");
            }

        });

        try {
            if (plugins == null)
                throw new IllegalStateException("Plugins directory does not exists");
            final PluginManager.PluginLocation[] loc = new PluginManager.PluginLocation[plugins.length];

            for (int i = 0; i < plugins.length; i++) {
                loc[i] = StandardPluginLocation.create(plugins[i]);
            }

            pluginManager.publishPlugins(loc);

            final Collection<PluginDescriptor> pluginDescriptorCollection = pluginManager.getRegistry().getPluginDescriptors();
            for (PluginDescriptor pluginDescriptor : pluginDescriptorCollection) {
                final String id = pluginDescriptor.getId();
                ShareDownloadService service = (ShareDownloadService) pluginManager.getPlugin(id);
                loadedPlugins.put(service.getName(), service);
                logger.info("Loaded support for service  " + id);
            }


        } catch (Exception e) {
            LogUtils.processException(logger, e);
            context.getApplication().exit();
        }
        final FileFactoryShareServiceImpl factoryShareService = new FileFactoryShareServiceImpl();
        loadedPlugins.put(factoryShareService.getName(), factoryShareService);
    }

    public ShareDownloadService getPlugin(String shareDownloadServiceID) throws NotSupportedDownloadServiceException {
        if (!loadedPlugins.containsKey(shareDownloadServiceID))
            throw new NotSupportedDownloadServiceException(shareDownloadServiceID);
        return loadedPlugins.get(shareDownloadServiceID);
    }

    public boolean isSupported(URL s) {
        for (ShareDownloadService service : loadedPlugins.values()) {
            if (service.supportsURL(s.toExternalForm()))
                return true;
        }
        return false;
    }

    public String getServiceIDForURL(URL s) throws NotSupportedDownloadServiceException {
        for (ShareDownloadService service : loadedPlugins.values()) {
            if (service.supportsURL(s.toExternalForm()))
                return service.getName();
        }
        throw new NotSupportedDownloadServiceException();
    }
}
