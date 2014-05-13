package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.model.PluginMetaData;
import cz.vity.freerapid.plugimpl.PluginContextImpl;
import cz.vity.freerapid.plugimpl.StandardDialogSupport;
import cz.vity.freerapid.plugins.exceptions.NotSupportedDownloadServiceException;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginContext;
import cz.vity.freerapid.plugins.webclient.interfaces.ShareDownloadService;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import org.java.plugin.ObjectFactory;
import org.java.plugin.Plugin;
import org.java.plugin.PluginManager;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.standard.ShadingPathResolver;
import org.java.plugin.standard.StandardPluginLocation;
import org.java.plugin.util.ExtendedProperties;
import org.java.plugin.util.IoUtil;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class PluginsManager {
    private final static Logger logger = Logger.getLogger(PluginsManager.class.getName());

    //private Map<String, ShareDownloadService> loadedPlugins = new Hashtable<String, ShareDownloadService>();
    //  private Map<String, Pattern> supportedURLs = new HashMap<String, Pattern>();

    private Map<String, PluginMetaData> supportedPlugins = new HashMap<String, PluginMetaData>();

    private final Object lock = new Object();

    private final ApplicationContext context;
    private PluginManager pluginManager;
    private PluginMetaDataManager pluginMetaDataManager;


    public PluginsManager(ApplicationContext context) {
        this.context = context;
        pluginMetaDataManager = new PluginMetaDataManager(context);

        this.context.getApplication().addExitListener(new Application.ExitListener() {
            public boolean canExit(EventObject event) {
                return true;
            }

            public void willExit(EventObject event) {
                if (pluginManager != null) {
                    try {
                        pluginManager.shutdown();
                        //TODO neco co smaze soubory z tempu? framework na to sere!, ale evidentne 
                        File shadowFolder = new File(System.getProperty("java.io.tmpdir"), ".jpf-shadow");
                        IoUtil.emptyFolder(shadowFolder);
                    } catch (Exception e) {
                        //ignore
                    }
                }
            }
        });
        loadPlugins();
    }


    private void loadPlugins() {

        logger.info("Init Plugins Manager");
//        final ExtendedProperties config = new ExtendedProperties(Utils.loadProperties("jpf.properties", true));
        final ObjectFactory objectFactory = ObjectFactory.newInstance();
        final ShadingPathResolver resolver = new ShadingPathResolver();
        try {
            resolver.configure(new ExtendedProperties());
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
        //    pluginManager = objectFactory.createManager(objectFactory.createRegistry(), resolver);
        pluginManager = objectFactory.createManager(objectFactory.createRegistry(), resolver);

        initPlugins();
    }

    private void initPlugins() {
        final File pluginsDir = getPluginsDir();
        logger.info("Plugins dir: " + pluginsDir.getAbsolutePath());

        File[] plugins = pluginsDir.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".frp");
            }

        });

        try {
            if (plugins == null)
                throw new IllegalStateException("Plugins directory does not exists");
            final int length = plugins.length;
            final PluginManager.PluginLocation[] loc = new PluginManager.PluginLocation[length];

            for (int i = 0; i < length; i++) {

                try {
                    final String path = fileToUrl(plugins[i]).toExternalForm();
                    logger.info("Plugins path:" + path);
                    final URL context = new URL("jar:" + path + "!/");
                    final URL manifest = new URL("jar:" + path + "!/plugin.xml");

                    //loc[i] = StandardPluginLocation.create(plugins[i]);
                    loc[i] = new StandardPluginLocation(context, manifest);
                } catch (MalformedURLException e) {
                    LogUtils.processException(logger, e);
                }

                //loc[i] = StandardPluginLocation.create(plugins[i]);
                //logger.info("Plugin location: " + loc);
            }

            pluginManager.publishPlugins(loc);

            final Set<PluginMetaData> datas = pluginMetaDataManager.getItems();
            final HashMap<String, PluginMetaData> datasId = new HashMap<String, PluginMetaData>(datas.size());
            for (PluginMetaData data : datas) {
                datasId.put(data.getId(), data);
            }

            final Collection<PluginDescriptor> pluginDescriptorCollection = pluginManager.getRegistry().getPluginDescriptors();
            for (PluginDescriptor pluginDescriptor : pluginDescriptorCollection) {
                final String id = pluginDescriptor.getId();

                if (datasId.containsKey(id)) {
                    final PluginMetaData data = datasId.get(id);
                    data.setPluginDescriptor(pluginDescriptor);
                    supportedPlugins.put(id, data);
                } else {
                    supportedPlugins.put(id, new PluginMetaData(pluginDescriptor));
                }
            }

            disablePluginsInConflict();

        } catch (Exception e) {
            LogUtils.processException(logger, e);
            context.getApplication().exit();
        }
    }

    public File getPluginsDir() {
        return new File(Utils.getAppPath(), "plugins");
    }

    private void disablePluginsInConflict() {
        final Map<String, List<PluginMetaData>> serviceConficts = new HashMap<String, List<PluginMetaData>>();
        for (PluginMetaData pluginMetaData : supportedPlugins.values()) {
            if (pluginMetaData.isEnabled()) {
                final String s = pluginMetaData.getServices();
                if (serviceConficts.containsKey(s)) {
                    final List<PluginMetaData> datas = serviceConficts.get(s);
                    datas.add(pluginMetaData);
                } else {
                    List<PluginMetaData> list = new LinkedList<PluginMetaData>();
                    list.add(pluginMetaData);
                    serviceConficts.put(s, list);
                }
            }
        }
        for (List<PluginMetaData> list : serviceConficts.values()) {
            if (list.size() > 1) {
                for (PluginMetaData data : list) {
                    if (data.isPremium())
                        data.setEnabled(false);
                }
            }
        }
    }

    private static URL fileToUrl(final File plugin) throws MalformedURLException {
        return plugin.toURI().toURL();
    }

    /**
     * Overuje, zda je dane URL podporovane mezi pluginy
     *
     * @param url
     * @return vraci v pripade, ze nejaky plugin podporuje dane URL, jinak false
     */
    public boolean isSupported(final URL url) {
        final String s = url.toExternalForm();
        synchronized (lock) {
            for (PluginMetaData pluginMetaData : supportedPlugins.values()) {
                if (pluginMetaData.isSupported(s) && pluginMetaData.isEnabled())
                    return true;
            }
            return false;
        }
    }

    /**
     * Vraci ID sluzby podle daneho URL
     *
     * @param url
     * @return vraci ID funkcni zapnute sluzby - musi byt enabled
     * @throws NotSupportedDownloadServiceException
     *          pokud zadna zapnuta sluzba neni nalezena
     */
    public String getServiceIDForURL(URL url) throws NotSupportedDownloadServiceException {
        final Set<Map.Entry<String, PluginMetaData>> entries = this.supportedPlugins.entrySet();
        final String s = url.toExternalForm();
        for (Map.Entry<String, PluginMetaData> entry : entries) {
            final PluginMetaData value = entry.getValue();
            if (value.isSupported(s) && value.isEnabled())
                return entry.getKey();
        }
        throw new NotSupportedDownloadServiceException();
    }

    /**
     * Vraci samotny plugin z registry podle jeho ID.
     * Provadi jeho dynamickou alokaci.
     *
     * @param id ID pluginu
     * @return nacteny plugin - tato hodnota neni nikdy null
     * @throws NotSupportedDownloadServiceException
     *          pokud doslo k chybe pri ziskani pluginu podle daneho ID
     */
    public ShareDownloadService getPluginInstance(final String id) throws NotSupportedDownloadServiceException {

        synchronized (lock) {
            Plugin p;
            try {
                logger.info("Loading plugin with ID=" + id);
                p = pluginManager.getPlugin(id);
            } catch (Exception e) {
                LogUtils.processException(logger, e);
                throw new NotSupportedDownloadServiceException(id);
            }
            if (!(p instanceof ShareDownloadService))
                throw new NotSupportedDownloadServiceException(id);
            final ShareDownloadService plugin = (ShareDownloadService) p;
            if (plugin.getPluginContext() == null)
                plugin.setPluginContext(createPluginContext());
            logger.info("Plugin with ID=" + id + " was loaded");
            return plugin;
        }
    }

    private PluginContext createPluginContext() {
        return PluginContextImpl.create(new StandardDialogSupport(context), null);
    }

    public List<PluginMetaData> getSupportedPlugins() {
        return new ArrayList<PluginMetaData>(this.supportedPlugins.values());
    }

    public void updatePluginSettings() {
        pluginMetaDataManager.saveToFile(new HashSet<PluginMetaData>(supportedPlugins.values()));
    }

    public PluginMetaData getPluginMetadata(String serviceID) throws NotSupportedDownloadServiceException {
        if (!this.supportedPlugins.containsKey(serviceID))
            throw new NotSupportedDownloadServiceException(serviceID);
        return this.supportedPlugins.get(serviceID);
    }

    public ShareDownloadService getService(final DownloadFile file) {
        synchronized (lock) {
            return getActiveService(file);
        }
    }

    private ShareDownloadService getActiveService(DownloadFile file) {
        final String id = file.getShareDownloadServiceID();
        try {
            final PluginMetaData data = getPluginMetadata(id);
            if (!data.isEnabled()) {
                try {//aktivni plugin neni zapnuty, zkusim najit alternativu
                    final String newId = getServiceIDForURL(file.getFileUrl());
                    file.setShareDownloadServiceID(newId);
                    return getPluginInstance(newId);
                } catch (NotSupportedDownloadServiceException e1) {
                    //nenasel jsem alternativu pro disablovany plugin, vypisu tedy hlasku o tom, ze neni zapnuty ten puvodni
                    file.setShareDownloadServiceID(id);//v pripade, ze selhalo getPluginInstance(newId);, musim vratit id na stare 
                    file.setState(DownloadState.DISABLED);
                    file.setErrorMessage("Plugin is not enabled - " + file.getShareDownloadServiceID()); //TODO I18N
                }

                return null;
            }
            return getPluginInstance(id);
        } catch (NotSupportedDownloadServiceException e) {
            try {//snazim se najit alternativu
                final String newId = getServiceIDForURL(file.getFileUrl());
                file.setShareDownloadServiceID(newId);
                return getPluginInstance(newId);
            } catch (NotSupportedDownloadServiceException e1) {//nenasel jsem alternativu
                file.setState(DownloadState.ERROR);
                file.setErrorMessage("Not supported service - " + file.getShareDownloadServiceID()); //TODO I18N
            }
        }
        return null;
    }

    public boolean hasPlugin(String id) {
        return supportedPlugins.containsKey(id);
    }
}
