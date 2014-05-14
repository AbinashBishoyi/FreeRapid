package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.gui.managers.exceptions.NotSupportedDownloadServiceException;
import cz.vity.freerapid.gui.managers.exceptions.PluginIsNotEnabledException;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.model.PluginMetaData;
import cz.vity.freerapid.plugimpl.StandardDialogSupportImpl;
import cz.vity.freerapid.plugimpl.StandardPluginContextImpl;
import cz.vity.freerapid.plugimpl.StandardStorageSupportImpl;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginContext;
import cz.vity.freerapid.plugins.webclient.interfaces.ShareDownloadService;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import org.java.plugin.ObjectFactory;
import org.java.plugin.Plugin;
import org.java.plugin.PluginManager;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.PluginRegistry;
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
    private final ManagerDirector director;
    private PluginManager pluginManager;
    private PluginMetaDataManager pluginMetaDataManager;
    private static final int MAX_ENTRIES = 10;
//    private Map<String, PluginMetaData> mCache = new ConcurrentHashMap<String, PluginMetaData>(new LinkedHashMap<String, PluginMetaData>(MAX_ENTRIES, .75F, true) {
//        protected boolean removeEldestEntry(Map.Entry eldest) {
//            return size() > MAX_ENTRIES;
//        }
//    });
//

    private Map<String, PluginMetaData> pluginsCache = Collections.synchronizedMap(new LinkedHashMap<String, PluginMetaData>(MAX_ENTRIES, .75F, true) {
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }
    });


    public PluginsManager(ApplicationContext context, ManagerDirector director) {
        this.context = context;
        this.director = director;
        pluginMetaDataManager = new PluginMetaDataManager(context);

        this.context.getApplication().addExitListener(new Application.ExitListener() {
            public boolean canExit(EventObject event) {
                return true;
            }

            public void willExit(EventObject event) {
                if (pluginManager != null) {
                    try {
                        pluginManager.shutdown();
                        File shadowFolder = new File(System.getProperty("java.io.tmpdir"), ".jpf-shadow");
                        IoUtil.emptyFolder(shadowFolder);
                    } catch (Exception e) {
                        //ignore
                    }
                }
            }
        });
        findAndInitNewPlugins();
    }


    private void findAndInitNewPlugins() {

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

        initNewPlugins(searchExistingPlugins());
    }


    public boolean isPluginInUseForUpdates(String id) {
        if (hasPlugin(id)) {
            final PluginRegistry pluginRegistry = pluginManager.getRegistry();
            if (pluginRegistry.isPluginDescriptorAvailable(id)) {
                final PluginDescriptor descr = pluginRegistry.getPluginDescriptor(id);
                return pluginManager.isBadPlugin(descr) || !pluginManager.isPluginEnabled(descr);
            }
        }
        return false;
    }

    public boolean isPluginDisabled(String id) {
        if (hasPlugin(id)) {
            final PluginRegistry pluginRegistry = pluginManager.getRegistry();
            if (pluginRegistry.isPluginDescriptorAvailable(id)) {
                final PluginDescriptor descr = pluginRegistry.getPluginDescriptor(id);
                return pluginManager.isBadPlugin(descr) || !pluginManager.isPluginEnabled(descr);
            }
        }
        return true;
    }

    public void reregisterAll() {
        final PluginRegistry pluginRegistry = pluginManager.getRegistry();
        synchronized (lock) {
            final Collection<PluginDescriptor> desc = pluginRegistry.getPluginDescriptors();
            String[] ids = new String[desc.size()];
            int counter = 0;
            for (PluginDescriptor pluginDescriptor : desc) {
                final String id = pluginDescriptor.getId();
                ids[counter++] = id;
                pluginManager.deactivatePlugin(id);
            }
            pluginsCache.clear();
            pluginRegistry.unregister(ids);
            initNewPlugins(searchExistingPlugins());
        }
    }

//    public void reRegisterPlugins(Collection<WrappedPluginData> updatedPlugins) throws JpfException {
//        if (updatedPlugins.isEmpty())
//            return;
//        List<File> updatedPluginsFiles = new ArrayList<File>(updatedPlugins.size());
//        List<String> newPluginsIds = new ArrayList<String>(updatedPlugins.size());
//        for (WrappedPluginData updatedPlugin : updatedPlugins) {
//            updatedPluginsFiles.add(updatedPlugin.getHttpFile().getOutputFile());
//            newPluginsIds.add(updatedPlugin.getID());
//        }
//        final String[] ids = newPluginsIds.toArray(new String[newPluginsIds.size()]);
//        final PluginRegistry pluginRegistry = pluginManager.getRegistry();
//        synchronized (lock) {
//            logger.info("Unregistering plugins " + Arrays.toString(ids));
//            pluginRegistry.unregister(ids);
//            initNewPlugins(updatedPluginsFiles.toArray(new File[updatedPluginsFiles.size()]));
//        }
//    }

    public void initNewPlugins(final File[] plugins) {
        if (plugins.length == 0) {
            return;
        }
        try {

            pluginManager.publishPlugins(getPluginLocations(plugins));

            final Set<PluginMetaData> datas = pluginMetaDataManager.getItems();
            final Map<String, PluginMetaData> datasId = new HashMap<String, PluginMetaData>(datas.size());
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
            if (this.supportedPlugins == null || supportedPlugins.isEmpty())
                context.getApplication().exit();
        }
    }

    private PluginManager.PluginLocation[] getPluginLocations(File[] plugins) {
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
        return loc;
    }

    private File[] searchExistingPlugins() {
        final File pluginsDir = getPluginsDir();
        logger.info("Plugins dir: " + pluginsDir.getAbsolutePath());

        return pluginsDir.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.toLowerCase(Locale.ENGLISH).endsWith(".frp");
            }

        });
    }

    public File getPluginsDir() {
        final File dir = new File(Utils.getAppPath(), "plugins");
        final String path = System.getProperty("plug-dir", dir.getAbsolutePath());
        final File file = new File(path);
        if (!file.exists())
            file.mkdirs();
        return file;
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
     * @param monitoring if should count with user settings of clipboard monitoring
     * @return vraci v pripade, ze nejaky plugin podporuje dane URL, jinak false
     */
    public boolean isSupported(final URL url) {
        return isSupported(url, false);
    }


    /**
     * Overuje, zda je dane URL podporovane mezi pluginy
     *
     * @param url
     * @param monitoring if should count with user settings of clipboard monitoring 
     * @return vraci v pripade, ze nejaky plugin podporuje dane URL, jinak false
     */
    public boolean isSupported(final URL url, boolean monitoring) {
        final String s = url.toExternalForm();
        synchronized (lock) {
            final PluginMetaData[] datas = getCachedPlugins();
            for (int i = datas.length - 1; i >= 0; i--) {
                PluginMetaData pluginMetaData = datas[i];
                if (pluginMetaData.isSupported(s)) {
                    addToCache(pluginMetaData);
                    logger.info("Cache hit");
                    if (pluginMetaData.isEnabled()) {
                        return !monitoring || pluginMetaData.isClipboardMonitored();
                    }
                }
            }
            for (PluginMetaData pluginMetaData : supportedPlugins.values()) {
                if (pluginMetaData.isSupported(s)) {
                    addToCache(pluginMetaData);
                    if (pluginMetaData.isEnabled()) {
                        return !monitoring || pluginMetaData.isClipboardMonitored();
                    }
                }
            }
            return false;
        }
    }

    private void addToCache(PluginMetaData pluginMetaData) {
        pluginsCache.put(pluginMetaData.getId(), pluginMetaData);
    }

    private PluginMetaData[] getCachedPlugins() {
        final Collection<PluginMetaData> cached = pluginsCache.values();
        final int cacheSize = cached.size();
        final PluginMetaData[] datas = new PluginMetaData[cacheSize];
        cached.toArray(datas);
        return datas;
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
        final String s = url.toExternalForm();
        PluginMetaData disabledPlugin = null;
        final PluginMetaData[] plugins = getCachedPlugins();

        //iterate through last used
        for (int i = plugins.length - 1; i >= 0; i--) {
            PluginMetaData plugin = plugins[i];
            if (plugin.isSupported(s)) {
                addToCache(plugin);
                logger.info("Cache hit");
                if (plugin.isEnabled()) {
                    return plugin.getId();
                }
            }
        }
        //iterate through all plugins
        for (PluginMetaData plugin : this.supportedPlugins.values()) {
            if (plugin.isSupported(s)) {
                addToCache(plugin);
                if (!plugin.isEnabled()) {
                    disabledPlugin = plugin;
                } else
                    return plugin.getId();
            }
        }
        if (disabledPlugin != null)
            throw new PluginIsNotEnabledException(disabledPlugin);
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
            if (!hasPlugin(id)) {
                throw new NotSupportedDownloadServiceException(id);
            }

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


    public PluginManager getPluginManager() {
        return pluginManager;
    }

    private PluginContext createPluginContext() {
        return StandardPluginContextImpl.create(new StandardDialogSupportImpl(context), new StandardStorageSupportImpl(context), director.getDataManager());
    }

    public List<PluginMetaData> getSupportedPlugins() {
        final Collection<PluginMetaData> datas = this.supportedPlugins.values();
        List<PluginMetaData> result = new ArrayList<PluginMetaData>(datas.size());
        for (PluginMetaData data : datas) {
            if (!data.isLibraryPlugin() && data.getMaxParallelDownloads() >= 1) {
                result.add(data);
            }
        }
        return result;
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
        final String id = file.getPluginID();
        try {
            final PluginMetaData data = getPluginMetadata(id);
            if (!data.isEnabled()) {
                try {//aktivni plugin neni zapnuty, zkusim najit alternativu
                    final String newId = getServiceIDForURL(file.getFileUrl());
                    file.setPluginID(newId);
                    return getPluginInstance(newId);
                } catch (NotSupportedDownloadServiceException ex) {
                    //nenasel jsem alternativu pro disablovany plugin, vypisu tedy hlasku o tom, ze neni zapnuty ten puvodni
                    file.setPluginID(id);//v pripade, ze selhalo getPluginInstance(newId);, musim vratit id na stare
                    file.setState(DownloadState.DISABLED);
                    file.setErrorMessage(Swinger.getMessageFromException(Swinger.getResourceMap(), ex));
                }

                return null;
            }
            return getPluginInstance(id);
        } catch (NotSupportedDownloadServiceException e) {
            try {//snazim se najit alternativu
                final String newId = getServiceIDForURL(file.getFileUrl());
                file.setPluginID(newId);
                return getPluginInstance(newId);
            } catch (PluginIsNotEnabledException ex) {
                file.setState(DownloadState.DISABLED);
                file.setErrorMessage(Swinger.getResourceMap().getString("PluginIsNotEnabled", file.getPluginID()));
            }
            catch (NotSupportedDownloadServiceException e1) {//nenasel jsem alternativu
                file.setState(DownloadState.ERROR);
                file.setErrorMessage(Swinger.getMessageFromException(Swinger.getResourceMap(), e));
            }
        }
        return null;
    }

    public boolean hasPlugin(String id) {
        return supportedPlugins.containsKey(id);
    }

}
