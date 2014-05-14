package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.dialogs.WrappedPluginData;
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
import cz.vity.freerapid.utilities.FileUtils;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import jlibs.core.util.ReverseComparator;
import org.java.plugin.JpfException;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class PluginsManager {
    private final static Logger logger = Logger.getLogger(PluginsManager.class.getName());

    private final static String ID_DIRECT = "direct";

    private final Map<String, PluginMetaData> supportedPlugins = new HashMap<String, PluginMetaData>();

    private final Object lock = new Object();

    private final ApplicationContext context;
    private final ManagerDirector director;
    private PluginManager pluginManager;
    private PluginMetaDataManager pluginMetaDataManager;

    private final static int MAX_ENTRIES = 10;
    private final Map<String, PluginMetaData> pluginsCache = Collections.synchronizedMap(new LinkedHashMap<String, PluginMetaData>(MAX_ENTRIES, .75F, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }
    });

    public PluginsManager(ApplicationContext context, ManagerDirector director, final CountDownLatch countDownLatch) {
        this.context = context;
        this.director = director;
        pluginMetaDataManager = new PluginMetaDataManager(director);

        this.context.getApplication().addExitListener(new Application.ExitListener() {
            @Override
            public boolean canExit(EventObject event) {
                return true;
            }

            @Override
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    findAndInitNewPlugins();
                } finally {
                    countDownLatch.countDown();
                }
            }
        }).start();
    }

    private void findAndInitNewPlugins() {
        logger.info("Init Plugins Manager");

        synchronized (lock) {
            final ObjectFactory objectFactory = ObjectFactory.newInstance();
            final ShadingPathResolver resolver = new ShadingPathResolver() {
                @Override
                protected URL maybeJarUrl(URL url) throws MalformedURLException {
                    /*
                     * This method is overridden to add support for .frp plugins.
                     * Also, the original method uses toLowerCase(Locale.getDefault()).
                     * All classes with these issues: StandardPathResolver, ShadingPathResolver, StandardPluginLocation
                     */
                    if ("jar".equalsIgnoreCase(url.getProtocol())) {
                        return url;
                    }
                    File file = IoUtil.url2file(url);
                    if ((file == null) || !file.isFile()) {
                        return url;
                    }
                    String fileName = file.getName().toLowerCase(Locale.ROOT);
                    if (fileName.endsWith(".jar")
                            || fileName.endsWith(".zip")
                            || fileName.endsWith(".frp")) {
                        return new URL("jar:" + IoUtil.file2url(file).toExternalForm() + "!/");
                    }
                    return url;
                }
            };
            try {
                resolver.configure(new ExtendedProperties());
            } catch (Exception e) {
                LogUtils.processException(logger, e);
            }
            pluginManager = objectFactory.createManager(objectFactory.createRegistry(), resolver);
        }
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

    public void updateNewPlugins(Collection<WrappedPluginData> updatedPlugins) throws JpfException {
        if (updatedPlugins.isEmpty())
            return;
        final PluginRegistry pluginRegistry = pluginManager.getRegistry();
        Set<String> updatedIds = new HashSet<String>();
        List<File> updatedPluginsFiles = new ArrayList<File>(updatedPlugins.size());
        synchronized (lock) {
            for (WrappedPluginData updatedPlugin : updatedPlugins) {
                final String id = updatedPlugin.getID();
                if (pluginRegistry.isPluginDescriptorAvailable(id)) {
                    final PluginDescriptor descriptor = pluginRegistry.getPluginDescriptor(id);
                    if (updatedPlugin.isToBeDeleted()) {
                        try {
                            final File file = urlToFile(descriptor.getLocation());
                            if (!file.delete()) {
                                logger.severe("Failed to delete plugin file " + file.getAbsolutePath());
                            }
                            logger.info(id + " " + updatedPlugin.getVersion() + " should be deleted");
                            updatedIds.add(id);
                            supportedPlugins.remove(id);
                        } catch (Exception e) {
                            LogUtils.processException(logger, e);
                            updatedPlugin.getHttpFile().setState(DownloadState.ERROR);
                        }
                        continue;
                    }
                    updatedIds.add(id);
                    //this is a plugin update, we have to select also all dependants to unregister and register them again

                    final Collection<PluginDescriptor> dependencies = pluginRegistry.getDependingPlugins(descriptor);
                    for (PluginDescriptor dependency : dependencies) {
                        logger.info("Found dependency update for plugin " + id + " - dependency plugin " + dependency.getId());
                        updatedIds.add(dependency.getId());
                    }
                } else {
                    //this is a quite new plugin
                    updatedPluginsFiles.add(updatedPlugin.getHttpFile().getOutputFile());
                }
            }
            for (String id : updatedIds) {
                if (pluginRegistry.isPluginDescriptorAvailable(id)) {
                    final PluginDescriptor descriptor = pluginRegistry.getPluginDescriptor(id);
                    try {
                        final URL location = descriptor.getLocation();
                        logger.info("Location " + location.toExternalForm());
                        final File file = urlToFile(location);
                        if (file.exists()) {
                            updatedPluginsFiles.add(file);
                        }
                    } catch (URISyntaxException e) {
                        //it happened to me once, but not reproducible
                        logger.severe("Descriptor location " + descriptor.getLocation() + " cannot be converted to URI!");
                        LogUtils.processException(logger, e);
                    } catch (MalformedURLException e) {
                        LogUtils.processException(logger, e);
                    }
                }
            }


            final String[] ids = updatedIds.toArray(new String[updatedIds.size()]);
            logger.info("Unregistering plugins " + Arrays.toString(ids));
            pluginRegistry.unregister(ids);
            logger.info("Clearing plugin cache");
            pluginsCache.clear();
            initNewPlugins(updatedPluginsFiles.toArray(new File[updatedPluginsFiles.size()]));
        }
    }

    private void initNewPlugins(final File[] plugins) {
        if (plugins.length == 0) {
            return;
        }
        try {

            pluginManager.publishPlugins(getPluginLocations(plugins));

            final Collection<PluginMetaData> datas = pluginMetaDataManager.getItems();
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
            updatePluginSettings();

        } catch (Exception e) {
            LogUtils.processException(logger, e);
            if (supportedPlugins.isEmpty())
                context.getApplication().exit();
        }
    }

    private PluginManager.PluginLocation[] getPluginLocations(File[] plugins) {
        if (plugins == null)
            throw new IllegalStateException("Plugin directory does not exist");
        final int length = plugins.length;
        final PluginManager.PluginLocation[] loc = new PluginManager.PluginLocation[length];

        for (int i = 0; i < length; i++) {
            try {
                final String path = fileToUrl(plugins[i]).toExternalForm();
                if (logger.isLoggable(Level.FINE))
                    logger.fine("Plugins path:" + path);
                final URL context = new URL("jar:" + path + "!/");
                final URL manifest = new URL("jar:" + path + "!/plugin.xml");
                loc[i] = new StandardPluginLocation(context, manifest);
            } catch (MalformedURLException e) {
                LogUtils.processException(logger, e);
            }
        }
        logger.info("Found " + length + " plugins.");

        return loc;
    }

    private File[] searchExistingPlugins() {
        final File pluginsDir = getPluginsDir();
        logger.info("Plugins dir: " + pluginsDir.getAbsolutePath());
        initiatePluginsIfNeccessary(pluginsDir);
        return pluginsDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase(Locale.ENGLISH).endsWith(".frp");
            }
        });
    }

    public File getPluginsDir() {
        final File parentDir;
        if (System.getProperties().containsKey("portable")) {
            parentDir = new File(Utils.getAppPath());
        } else {
            parentDir = context.getLocalStorage().getDirectory();
        }
        final File pluginsDir = new File(parentDir, Consts.PLUGINS_DIR);
        if (pluginsDir.exists() && !pluginsDir.isDirectory()) {
            logger.warning("Deleting file with same name as plugin directory: " + pluginsDir);
            if (!pluginsDir.delete()) {
                logger.severe("Failed to delete file with same name as plugin directory: " + pluginsDir);
            }
        }

        if (!pluginsDir.exists()) {
            if (!pluginsDir.mkdirs()) {
                logger.severe("Failed to create plugin directory: " + pluginsDir);
            }
        }
        return pluginsDir;
    }

    private void initiatePluginsIfNeccessary(final File pluginsDir) {
        boolean extractPlugins = true;
        if (isPluginsDirForCorrectVersion(pluginsDir)) {
            extractPlugins = false;
        } else {
            logger.info("Deleting old plugins");
            if (!IoUtil.emptyFolder(pluginsDir)) {
                logger.severe("Failed to empty plugin directory");//never mind, we give a chance to rewrite plugins
            }
        }
        if (extractPlugins) {
            logger.info("Extracting dist plugins");
            final File pluginsDistFile = new File(Utils.getAppPath(), Consts.PLUGINS_DIST_FILE_NAME);
            FileUtils.extractZipFileInto(pluginsDistFile, pluginsDir);
            final File file = new File(pluginsDir, Consts.PLUGINS_VERSION_FILE_NAME);
            //write plugins dir Version file
            FileUtils.writeFileWithValue(file, String.valueOf(MainApp.PLUGINS_VERSION));
        }
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean isPluginsDirForCorrectVersion(final File pluginsDir) {
        final File versionFile = new File(pluginsDir, Consts.PLUGINS_VERSION_FILE_NAME);
        if (!versionFile.exists() || versionFile.length() <= 0) {
            return false;
        }
        return Integer.valueOf(MainApp.PLUGINS_VERSION).equals(Integer.valueOf(Utils.loadFile(versionFile)));
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

    /**
     * Overuje, zda je dane URL podporovane mezi pluginy
     *
     * @param url url
     * @return vraci v pripade, ze nejaky plugin podporuje dane URL, jinak false
     */
    public boolean isSupported(final URL url) {
        return isSupported(url, false);
    }

    /**
     * Overuje, zda je dane URL podporovane mezi pluginy
     *
     * @param url        url
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
            return (!monitoring || AppPrefs.getProperty(UserProp.ENABLE_CLIPBOARD_MONITORING_FOR_DIRECT_DOWNLOADS, UserProp.ENABLE_CLIPBOARD_MONITORING_FOR_DIRECT_DOWNLOADS_DEFAULT))
                    && isDirectDownloadEnabled();
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
     * @param url url
     * @return vraci ID funkcni zapnute sluzby - musi byt enabled
     * @throws NotSupportedDownloadServiceException
     *          pokud zadna zapnuta sluzba neni nalezena
     */
    public String getServiceIDForURL(URL url) throws NotSupportedDownloadServiceException {
        final String s = url.toExternalForm();
        PluginMetaData disabledPlugin = null;

        final boolean priorityFirst = AppPrefs.getProperty(UserProp.PLUGIN_WITH_PRIORITY_PRECEDENCE, UserProp.PLUGIN_WITH_PRIORITY_PRECEDENCE_DEFAULT);
        if (priorityFirst) {
            //iterate through all plugins
            final Collection<PluginMetaData> values = this.supportedPlugins.values();
            final PluginMetaData[] pluginMetaDatas = values.toArray(new PluginMetaData[values.size()]);
            Arrays.sort(pluginMetaDatas, new ReverseComparator<PluginMetaData>(new PriorityComparator()));
            for (PluginMetaData plugin : pluginMetaDatas) {
                if (plugin.isSupported(s)) {
                    if (!plugin.isEnabled()) {
                        disabledPlugin = plugin;
                    } else
                        return plugin.getId();
                }
            }
        } else {

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
            final Collection<PluginMetaData> values = this.supportedPlugins.values();
            for (PluginMetaData plugin : values) {
                if (plugin.isSupported(s)) {
                    addToCache(plugin);
                    if (!plugin.isEnabled()) {
                        disabledPlugin = plugin;
                    } else
                        return plugin.getId();
                }
            }
        }
        if (disabledPlugin != null)
            throw new PluginIsNotEnabledException(disabledPlugin);

        if (isDirectDownloadEnabled()) {
            return ID_DIRECT;
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
        pluginMetaDataManager.saveToDatabase(new HashSet<PluginMetaData>(supportedPlugins.values()));
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
            } catch (PluginIsNotEnabledException e1) {
                file.setState(DownloadState.DISABLED);
                file.setErrorMessage(Swinger.getResourceMap().getString("PluginIsNotEnabled", file.getPluginID()));
            } catch (NotSupportedDownloadServiceException e1) {//nenasel jsem alternativu
                file.setState(DownloadState.ERROR);
                file.setErrorMessage(Swinger.getMessageFromException(Swinger.getResourceMap(), e));
            }
        }
        return null;
    }

    public boolean hasPlugin(String id) {
        return supportedPlugins.containsKey(id);
    }

    private static URL fileToUrl(final File plugin) throws MalformedURLException {
        return plugin.toURI().toURL();
    }

    private static File urlToFile(final URL plugin) throws MalformedURLException, URISyntaxException {
        final String s = plugin.getFile();
        final int i = s.lastIndexOf("!/");
        if (i != -1) { //smells like a potential bug
            return new File(new URL(s.substring(0, i)).toURI());
        }
        return new File(plugin.toURI());
    }

    private boolean isDirectDownloadEnabled() {
        return AppPrefs.getProperty(UserProp.ENABLE_DIRECT_DOWNLOADS, UserProp.ENABLE_DIRECT_DOWNLOADS_DEFAULT)
                && !isPluginDisabled(ID_DIRECT);
    }

    public static class PriorityComparator implements Comparator<PluginMetaData> {
        @Override
        public int compare(PluginMetaData o1, PluginMetaData o2) {
            return new Integer(o1.getPluginPriority()).compareTo(o2.getPluginPriority());
        }
    }
}
