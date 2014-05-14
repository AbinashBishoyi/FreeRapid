package cz.vity.freerapid.gui.managers;

import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.value.DelayedReadValueModel;
import com.jgoodies.common.collect.ArrayListModel;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.FWProp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.gui.actions.DownloadsActions;
import cz.vity.freerapid.gui.actions.URLTransferHandler;
import cz.vity.freerapid.gui.managers.exceptions.NotSupportedDownloadServiceException;
import cz.vity.freerapid.gui.managers.interfaces.FileStateChangeListener;
import cz.vity.freerapid.gui.managers.interfaces.UrlListDataListener;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.container.FileInfo;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.interfaces.MaintainQueueSupport;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.FileUtils;
import cz.vity.freerapid.utilities.Utils;
import org.apache.commons.httpclient.URIException;
import org.jdesktop.application.AbstractBean;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

import static cz.vity.freerapid.plugins.webclient.DownloadState.*;
import static cz.vity.freerapid.plugins.webclient.FileState.NOT_CHECKED;

/**
 * @author Vity
 */
public class DataManager extends AbstractBean implements PropertyChangeListener, ListDataListener, MaintainQueueSupport {
    private final static Logger logger = Logger.getLogger(DataManager.class.getName());
    private static final String DATA_CHANGED_PROPERTY = "dataChanged";

    private final ArrayListModel<DownloadFile> downloadFiles = new ArrayListModel<DownloadFile>();
    private final Set<DownloadFile> changedFiles = Collections.synchronizedSet(new LinkedHashSet<DownloadFile>());

    private ProcessManager processManager;
    private final ManagerDirector director;
    private final ApplicationContext context;

    private final Object lock = new Object();
    private int completed;
    private int notFound;

    private PluginsManager pluginsManager;

    private int dataChanged = 0;
    private FileListMaintainer fileListMaintainer;
    private boolean optimizeSavingList;
    private EventListenerList listenerList = new EventListenerList();

    public DataManager(ManagerDirector director, ApplicationContext context) {
        this.director = director;
        this.context = context;
        fileListMaintainer = new FileListMaintainer(context, director, this);
        pluginsManager = director.getPluginsManager();
        optimizeSavingList = AppPrefs.getProperty(UserProp.OPTIMIZE_SAVING_LIST, UserProp.OPTIMIZE_SAVING_LIST_DEFAULT);
        context.getApplication().addExitListener(new Application.ExitListener() {
            public boolean canExit(EventObject event) {
                if (AppPrefs.getProperty(FWProp.MINIMIZE_ON_CLOSE, FWProp.MINIMIZE_ON_CLOSE_DEFAULT) && event instanceof WindowEvent) {
                    return true;
                }
                final boolean confirmExiting = AppPrefs.getProperty(UserProp.CONFIRM_EXITING, UserProp.CONFIRM_EXITING_DEFAULT);
                if (confirmExiting && isDownloading()) {
                    final int result = Swinger.getChoiceOKCancel("downloadInProgress");
                    return (result == Swinger.RESULT_OK);
                }
                return true;
            }

            public void willExit(EventObject event) {
                exitDownloading();
                updateChangedFilesInDatabase();//commit changed files
                fileListMaintainer.doShutDown();//run all tasks, stop accepting new ones
            }
        });
    }

    private void exitDownloading() {
        boolean foundRunning = false;
        processManager.interrupt();
        synchronized (lock) {
            for (DownloadFile file : downloadFiles) {
                final DownloadTask task = file.getTask();
                if (task != null && !task.isTerminated()) {
                    file.setState(PAUSED);
                    task.cancel(true);
                    foundRunning = true;
                    if (AppPrefs.getProperty(UserProp.DOWNLOAD_ON_APPLICATION_START, UserProp.DOWNLOAD_ON_APPLICATION_START_DEFAULT)) {
                        file.setState(QUEUED);
                    } else
                        file.setState(PAUSED);
                }
            }
        }
        //  processManager.queueUpdated();
        if (foundRunning) {
            try {
                Thread.sleep(700);//time to cancel DownloadTask properly
            } catch (InterruptedException e) {
                //ignore
            }
        }
    }

    public void addFileStateChangedListener(FileStateChangeListener listener) {
        listenerList.add(FileStateChangeListener.class, listener);
    }

    public void removeFileStateChangedListener(FileStateChangeListener listener) {
        listenerList.remove(FileStateChangeListener.class, listener);
    }

    private void saveListToBeanImmediately() {
        fileListMaintainer.saveToDatabase(downloadFiles);
    }

    public void initProcessManagerInstance() {
        processManager = new ProcessManager(director, context);
    }

    public void initProcessManagerQueue() {
        synchronized (lock) {
            fileListMaintainer.loadListToBean(downloadFiles);
        }
        updateCompleted();
        downloadFiles.addListDataListener(this);
        processManager.start();
        if (AppPrefs.getProperty(UserProp.AUTOSAVE_ENABLED, UserProp.AUTOSAVE_ENABLED_DEFAULT)) {

            PropertyAdapter<DataManager> adapter = new PropertyAdapter<DataManager>(this, DATA_CHANGED_PROPERTY, true);

            final int time = AppPrefs.getProperty(UserProp.AUTOSAVE_TIME, UserProp.AUTOSAVE_TIME_DEFAULT);

            DelayedReadValueModel delayedReadValueModel = new DelayedReadValueModel(adapter, time * 1000, false);
            delayedReadValueModel.addValueChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    updateChangedFilesInDatabase();

                }
            });
        }
        this.addPropertyChangeListener("state", new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() == COMPLETED) {
                    if (AppPrefs.getProperty(UserProp.REMOVE_COMPLETED_DOWNLOADS, UserProp.REMOVE_COMPLETED_DOWNLOADS_DEFAULT) == UserProp.REMOVE_COMPLETED_DOWNLOADS_IMMEDIATELY) {
                        removeCompleted();
                    }
                }
            }
        });
    }

    private void updateChangedFilesInDatabase() {
        //we are on EDT thread, we don't need to block changedFiles
        if (!changedFiles.isEmpty()) {
            final List<DownloadFile> updateFiles = new ArrayList<DownloadFile>(changedFiles);
            changedFiles.clear();
            fileListMaintainer.saveToDatabaseOnBackground(updateFiles);
        }
    }


    public void addToQueue(List<DownloadFile> files) {
        synchronized (lock) {
            for (DownloadFile f : files) {
                f.setState(QUEUED);
            }
        }
        processManager.queueUpdated();
    }

    public void addToList(List<DownloadFile> files) {
        synchronized (lock) {
            addOnList(files);
        }
        fireUrlsAdded(files);
    }

    private void addOnList(List<DownloadFile> files) {
        final boolean startFromTop = AppPrefs.getProperty(UserProp.START_FROM_TOP, UserProp.START_FROM_TOP_DEFAULT);
        final Date insertDate = new Date();
        int counter = this.downloadFiles.size();
        for (DownloadFile file : files) {
            file.setListOrder(counter++); //optimization , we don't need to reOrder and resave the whole list
            file.setDateInserted(insertDate);
            try {
                file.setPluginID(pluginsManager.getServiceIDForURL(file.getFileUrl()));
            } catch (NotSupportedDownloadServiceException e) {
                file.setState(ERROR);
                file.setErrorMessage(Swinger.getMessageFromException(Swinger.getResourceMap(), e));
            }
            file.addPropertyChangeListener(this);
        }
        if (startFromTop) {
            this.downloadFiles.addAll(files);
        } else {
            this.downloadFiles.addAll(0, files); //reverse collection first?
            reOrderListProperty();
        }
    }


    public void addUrlListDataListener(UrlListDataListener l) {
        listenerList.add(UrlListDataListener.class, l);
    }


    private void fireUrlsAdded(List<DownloadFile> list) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == UrlListDataListener.class) {
                // Lazily create the event:
                ((UrlListDataListener) listeners[i + 1]).linksAdded(list);
            }
        }

    }

    private void fireFileStateChanged(DownloadFile file, DownloadState oldState, DownloadState newState) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        final StateChangeEvent event = new StateChangeEvent(file, oldState, newState);
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == FileStateChangeListener.class) {
                // Lazily create the event:
                ((FileStateChangeListener) listeners[i + 1]).stateChanged(event);
            }
        }
    }

    public ArrayListModel<DownloadFile> getDownloadFiles() {
        return downloadFiles;
    }

    public List<DownloadFile> getActualDownloadFiles() {
        synchronized (lock) {
            return Collections.unmodifiableList(new ArrayList<DownloadFile>(downloadFiles));
        }
    }

    public List<DownloadFile> getDownloadFilesInStates(EnumSet<DownloadState> states) {
        synchronized (lock) {
            final List<DownloadFile> list = new ArrayList<DownloadFile>();
            for (DownloadFile file : downloadFiles) {
                if (states.contains(file.getState())) {
                    list.add(file);
                }
            }
            return Collections.unmodifiableList(list);
        }
    }

    public List<DownloadFile> setDownloadFilesState(DownloadState oldState, DownloadState newState) {
        synchronized (lock) {
            final List<DownloadFile> list = new ArrayList<DownloadFile>();
            for (DownloadFile file : downloadFiles) {
                if (file.getState() == oldState) {
                    list.add(file);
                }
            }
            for (DownloadFile downloadFile : list) {
                downloadFile.setState(newState);
            }
            return Collections.unmodifiableList(list);
        }
    }


    @SuppressWarnings({"SuspiciousMethodCalls"})
    private int getIndex(DownloadFile file) {
        return downloadFiles.indexOf(file);
    }

    public void propertyChange(final PropertyChangeEvent evt) {
        final String s = evt.getPropertyName();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // logger.info("Firing contents changed");
                final DownloadFile downloadFile = (DownloadFile) evt.getSource();
                downloadFiles.fireContentsChanged(getIndex(downloadFile));
                if (downloadFile.getState() != DELETED) {
                    //deleted file cannot be re-added
                    changedFiles.add(downloadFile);//mark as dirty - on EDT thread
                }
                if ("state".equals(s) || (!optimizeSavingList && "downloaded".equals(s))) {
                    firePropertyChange(s, evt.getOldValue(), evt.getNewValue());
                    fireDataChanged();
                }
                if ("orderList".equals(s)) {
                    fireDataChanged();
                } else if ("state".equals(s)) {
                    fireFileStateChanged(downloadFile, (DownloadState) evt.getOldValue(), (DownloadState) evt.getNewValue());
                }
            }
        });

    }

    public boolean hasDownloadFilesStates(int[] indexes, final EnumSet<DownloadState> states) {
        synchronized (this.lock) {
            if (indexes.length == 0)
                return false;
            for (int index : indexes) {
                final DownloadFile file = downloadFiles.get(index);
                final DownloadState s = file.getState();
                if (!states.contains(s))
                    return false;
            }
            return true;
        }
    }

    public boolean hasAnyDownloadFilesStates(int[] indexes, final EnumSet<DownloadState> states) {
        synchronized (this.lock) {
            if (indexes.length == 0)
                return false;
            for (int index : indexes) {
                final DownloadFile file = downloadFiles.get(index);
                final DownloadState s = file.getState();
                if (states.contains(s))
                    return true;
            }
            return false;
        }
    }

    public void removeSelected(final List<DownloadFile> fileList) {
        synchronized (this.lock) {
            final List<DownloadFile> toRemoveList = new ArrayList<DownloadFile>();
            for (DownloadFile downloadFile : fileList) {
                downloadFile.setState(DELETED);
                toRemoveList.add(downloadFile);
            }
            removeFromList(toRemoveList);
            for (DownloadFile file : toRemoveList) {
                final DownloadTask task = file.getTask();
                if (task != null && !task.isTerminated()) {
                    task.cancel(true);
                }
                file.removePropertyChangeListener(this);
            }
        }
        processManager.queueUpdated();

    }


    public void resumeSelected(final int[] indexes) {
        //predpoklada se, ze alespon jeden soubor splni vnitrni podminku
        synchronized (this.lock) {
            final List<DownloadFile> files = selectionToList(indexes);
            final List<DownloadFile> resumingFiles = new LinkedList<DownloadFile>();
            for (DownloadFile file : files) {
                if (DownloadsActions.resumeEnabledStates.contains(file.getState())) {
                    file.resetErrorAttempts();
                    resumingFiles.add(file);
                }
            }
            addToQueue(resumingFiles);
        }

    }

    public void cancelSelected(final int[] indexes, final boolean delete) {
        synchronized (this.lock) {
            List<DownloadFile> toRemoveList = selectionToList(indexes);
            for (DownloadFile file : toRemoveList) {
                if (DownloadsActions.cancelEnabledStates.contains(file.getState())) {
                    final DownloadTask task = file.getTask();
                    if (task != null && !task.isTerminated()) {
                        task.cancel(true);
                        //file.setState(DownloadState.CANCELLED);
                    }
                    file.setState(CANCELLED);
                    if (delete && file.getDownloaded() > 0) {
                        File outputFile = file.getStoreFile();
                        if (outputFile != null && outputFile.exists()) {

                            FileUtils.deleteFileWithRecycleBin(outputFile);
                        }
                        outputFile = file.getOutputFile();
                        if (outputFile != null && outputFile.exists()) {
                            FileUtils.deleteFileWithRecycleBin(outputFile);
                        }
                    }
                    file.setDownloaded(0);
                }
            }
        }
        processManager.queueUpdated();
    }

    public void retryAllError() {
        synchronized (this.lock) {
            final List<DownloadFile> resumingFiles = new LinkedList<DownloadFile>();
            for (DownloadFile file : downloadFiles) {
                if (file.getState() == DownloadState.ERROR) {
                    file.resetErrorAttempts();
                    resumingFiles.add(file);
                }
            }
            addToQueue(resumingFiles);
        }
    }

    public List<DownloadFile> getSelectionToList(int[] indexes) {
        synchronized (lock) {
            return selectionToList(indexes);
        }
    }


    @Override
    public boolean addLinksToQueue(final HttpFile parentFile, final String data) {
        final List<URL> urlList = URLTransferHandler.textURIListToFileList(data, pluginsManager, false);
        final List<URI> uriList = new LinkedList<URI>();
        for (URL url : urlList) {
            try {
                uriList.add(Utils.convertToURI(url.toExternalForm()));
            } catch (URISyntaxException e) {
                //ignore
            } catch (URIException e) {
                //ignore
            }
        }
        return !uriList.isEmpty() && addLinksToQueue(parentFile, uriList);
    }

    private List<DownloadFile> selectionToList(int[] indexes) {
        List<DownloadFile> toRemoveList = new ArrayList<DownloadFile>();
        for (int index : indexes) {
            toRemoveList.add(downloadFiles.get(index));
        }
        return toRemoveList;
    }

    public void pauseSelected(final int[] indexes) {
        //processManager.queueUpdated();
        synchronized (this.lock) {
            List<DownloadFile> toRemoveList = selectionToList(indexes);
            for (DownloadFile file : toRemoveList) {
                final DownloadState state = file.getState();
                if (DownloadsActions.pauseEnabledStates.contains(state) || (state == DOWNLOADING && file.isResumeSupported())) {
                    //boolean isProcessState = DownloadsActions.isProcessState(state);
                    final DownloadTask task = file.getTask();
                    file.setState(PAUSED);
                    if (task != null) {
                        task.cancel(true);
                    }
//                    final File outputFile = file.getOutputFile();
//                    if (isProcessState && outputFile.exists()) {
//                        outputFile.delete();
//                        file.setDownloaded(0);
//                    }
                }
            }
        }
        processManager.queueUpdated();
    }

    public void intervalAdded(ListDataEvent e) {
        //to do make on the thread
        this.fileListMaintainer.saveToDatabaseOnBackground(getItems(e));
        contentsChanged(e);
        fireDataChanged();
    }

    public void intervalRemoved(ListDataEvent e) {
        //to do make on the thread
        contentsChanged(e);
        fireDataChanged();
    }

    public void contentsChanged(ListDataEvent e) {
        updateCompleted();
    }

    private Collection<DownloadFile> getItems(ListDataEvent e) {
        final int start = e.getIndex0();
        final int end = e.getIndex1();
        Collection<DownloadFile> result = new ArrayList<DownloadFile>(end - start + 1);
        for (int i = start; i <= end; ++i) {
            result.add(downloadFiles.get(i));
        }
        return result;
    }


    private void updateCompleted() {
//        logger.info("updateCompleted");
        synchronized (lock) {
            //          logger.info("updateCompleted2");
            int counter = 0;
            int notFound = 0;
            for (DownloadFile file : downloadFiles) {
                if (file.getState() == COMPLETED) {
                    counter++;
                }
                if (file.getFileState() == FileState.FILE_NOT_FOUND) {
                    notFound++;
                }
            }
            setCompleted(counter);
            setNotFound(notFound);
        }
    }

    private void setNotFound(int notFound) {
        int oldValue = this.notFound;
        this.notFound = notFound;
        firePropertyChange("notFound", oldValue, this.notFound);
    }


    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        int oldValue = this.completed;
        this.completed = completed;
        firePropertyChange("completed", oldValue, this.completed);
    }

    public void removeCompleted() {
        synchronized (lock) {
//            if (getCompleted() <= 0)
//                return;
            List<DownloadFile> toRemoveList = new LinkedList<DownloadFile>();
            for (DownloadFile file : downloadFiles) {
                if (file.getState() == COMPLETED)
                    toRemoveList.add(file);
            }
            removeFromList(toRemoveList);
            for (DownloadFile file : toRemoveList) {
                file.setState(DELETED);
            }
        }
    }

    private void removeFromList(List<DownloadFile> toRemoveList) {
        downloadFiles.removeAll(toRemoveList);
        //we have to remove existing changed, otherwise it would be added in the future again
        changedFiles.removeAll(toRemoveList);
        reOrderListProperty();
        this.fileListMaintainer.removeFromDatabaseOnBackground(toRemoveList);
    }

    public void removeCompletedAndDeleted() {
        synchronized (lock) {
            List<DownloadFile> toRemoveList = new LinkedList<DownloadFile>();
            for (DownloadFile file : downloadFiles) {
                if (file.getState() == COMPLETED && (file.getOutputFile() == null || !file.getOutputFile().exists())) {
                    toRemoveList.add(file);
                }
            }
            removeFromList(toRemoveList);
            for (DownloadFile file : toRemoveList) {
                file.setState(DELETED);
            }
        }
    }

    public Object getLock() {
        return lock;
    }

    public boolean checkAllComplete() {
        synchronized (lock) {
            final boolean nonFatalErrorIsOK = AppPrefs.getProperty(UserProp.AUTOSHUTDOWN_WITH_ERRORS, UserProp.AUTOSHUTDOWN_WITH_ERRORS_DEFAULT);
            for (DownloadFile file : downloadFiles) {
                final DownloadState state = file.getState();
                if (DownloadsActions.isProcessState(state) || state == QUEUED || state == SLEEPING) {
                    return false;
                }
                if (state == ERROR) {
                    final boolean isFatal = file.getErrorAttemptsCount() == 0;
                    if (isFatal)
                        continue;
                    if (!nonFatalErrorIsOK) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public void moveTop(int[] indexes) {
        synchronized (lock) {
            if (indexes.length > 1)
                Arrays.sort(indexes);
            for (int i = 0; i < indexes.length; i++) {
                int index = indexes[i];
                if (index != i) {
                    final DownloadFile f = downloadFiles.remove(index);
                    downloadFiles.add(i, f);
                }
            }
            reOrderListProperty();
        }
    }

    public void moveBottom(int[] indexes) {
        synchronized (lock) {
            final int length = indexes.length;
            if (length > 1)
                Arrays.sort(indexes);
            final int top = downloadFiles.size() - 1;
            for (int i = length - 1; i >= 0; --i) {
                int index = indexes[i];
                int newIndex = top - (length - 1 - i);
                if (index != newIndex) {
                    final DownloadFile downloadFile = downloadFiles.remove(index);
                    downloadFiles.add(newIndex, downloadFile);
                }
            }
            reOrderListProperty();
        }
    }

    public void moveUp(int[] indexes) {
        synchronized (lock) {
            if (indexes.length > 1)
                Arrays.sort(indexes);
            for (int i = 0; i < indexes.length; i++) {
                int index = indexes[i];
                int newIndex = Math.max(Math.max(0, index - 1), i);
                if (index != newIndex) {
                    //cannot use Collections.swap
                    final DownloadFile downloadFile = downloadFiles.remove(index);
                    downloadFiles.add(newIndex, downloadFile);
                }
                indexes[i] = newIndex;
            }
            reOrderListProperty();
        }
    }

    private void reOrderListProperty() {
        //should be called from synchronized block
        int counter = 0;
        for (DownloadFile file : downloadFiles) {
            file.setListOrder(counter++);
        }
    }

    public void moveDown(int[] indexes) {
        synchronized (lock) {
            final int length = indexes.length;
            if (length > 1)
                Arrays.sort(indexes);
            final int top = downloadFiles.size() - 1;
            for (int i = length - 1; i >= 0; --i) {
                int index = indexes[i];
                int newIndex = Math.min(Math.min(top, index + 1), top - (length - 1 - i));
                indexes[i] = newIndex;
                if (index != newIndex) {
                    final DownloadFile downloadFile = downloadFiles.remove(index);
                    downloadFiles.add(newIndex, downloadFile);
                }
            }
            reOrderListProperty();
        }
    }

    public boolean isDownloading() {
        synchronized (lock) {
            for (DownloadFile file : downloadFiles) {
                if (DownloadsActions.isProcessState(file.getState()))
                    return true;
            }
        }
        return false;
    }

    public int getDownloading() {
        return processManager.getDownloading();
    }

    public void forceDownload(final ConnectionSettings settings, final int[] indexes) {
        List<DownloadFile> forceDownloadList;
        synchronized (this.lock) {
            forceDownloadList = selectionToList(indexes);
        }
        processManager.forceDownload(settings, forceDownloadList);
    }

    public void checkQueue() {
        processManager.queueUpdated();
    }

    public int getDataChanged() {
        return dataChanged;
    }

    private void fireDataChanged() {
        firePropertyChange(DATA_CHANGED_PROPERTY, this.dataChanged, ++this.dataChanged);
    }

    public ProcessManager getProcessManager() {
        return processManager;
    }

    public void validateLinks(int[] indexes) {
        if (indexes.length == 0)
            return;
        final List<DownloadFile> files = new LinkedList<DownloadFile>();
        synchronized (lock) {
            for (DownloadFile file : selectionToList(indexes)) {
                if (DownloadsActions.recheckExistingStates.contains(file.getState())) {
                    file.getProperties().put("previousState", file.getState());
                    file.setFileState(NOT_CHECKED);
                    file.setState(QUEUED);
                    files.add(file);
                }
            }
        }
        if (!files.isEmpty())
            processManager.forceValidateCheck(files);
    }

    public void removeInvalidLinks() {
        synchronized (lock) {
//            if (getCompleted() <= 0)
//                return;
            List<DownloadFile> toRemoveList = new LinkedList<DownloadFile>();
            for (DownloadFile file : downloadFiles) {
                if (file.getFileState() == FileState.FILE_NOT_FOUND)
                    toRemoveList.add(file);
            }
            removeFromList(toRemoveList);
            for (DownloadFile file : toRemoveList) {
                file.setState(DELETED);
            }
        }
    }

    @Override
    public boolean addLinksToQueue(HttpFile parentFile, List<URI> uriList) {
        final List<DownloadFile> files = new LinkedList<DownloadFile>();
        final boolean dontAddNotSupported = AppPrefs.getProperty(UserProp.DONT_ADD_NOTSUPPORTED_FROMCRYPTER, UserProp.DONT_ADD_NOTSUPPORTED_FROMCRYPTER_DEFAULT);
        final boolean startDownload = AppPrefs.getProperty(UserProp.AUTO_START_DOWNLOADS_FROM_DECRYPTER, UserProp.AUTO_START_DOWNLOADS_FROM_DECRYPTER_DEFAULT);
        for (URI uri : uriList) {
            try {
                final URL url = uri.toURL();
                if (dontAddNotSupported && !pluginsManager.isSupported(url))
                    continue;
                final DownloadFile downloadFile = new DownloadFile(url, parentFile.getSaveToDirectory(), parentFile.getDescription());
                downloadFile.setPluginID("");
                files.add(downloadFile);
            } catch (MalformedURLException e) {
                logger.warning("File with URI " + uri.toString() + " cannot be added to queue");
            }
        }
        final boolean[] result = new boolean[1];
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    addToList(files);
                    if (startDownload) {
                        addToQueue(files);
                    }
                    result[0] = true;
                } catch (Exception e) {
                    result[0] = false;
                }
            }
        };
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (InterruptedException e) {
                return false;
            } catch (InvocationTargetException e) {
                return false;
            }
        } else {
            runnable.run();
        }
        return result[0];
    }

    @Override
    public boolean addLinkToQueueUsingPriority(HttpFile parentFile, String data) throws Exception {
        final List<URL> urlList = URLTransferHandler.textURIListToFileList(data, pluginsManager, false);
        return !urlList.isEmpty() && addLinkToQueueUsingPriority(parentFile, urlList);
    }

    @Override
    public boolean addLinkToQueueUsingPriority(HttpFile parentFile, List<URL> urlList) throws Exception {
        List<URLByPriority> list = new ArrayList<URLByPriority>(urlList.size());
        for (URL url : urlList) {
            final String id;
            try {
                id = pluginsManager.getServiceIDForURL(url);
            } catch (NotSupportedDownloadServiceException e) {
                continue;
            }
            list.add(new URLByPriority(url, pluginsManager.getPluginMetadata(id).getPluginPriority()));
        }
        return !list.isEmpty() && addLinksToQueue(parentFile, Arrays.asList(Utils.convertToURI(Collections.min(list).getUrl().toExternalForm())));
    }

    @Override
    public boolean addLinksToQueueFromContainer(final HttpFile parentFile, final List<FileInfo> infoList) {
        return addLinksToQueueFromContainer(infoList, parentFile.getSaveToDirectory(), parentFile.getDescription(), false);
    }

    @Override
    public boolean addLinkToQueueFromContainerUsingPriority(final HttpFile parentFile, final List<FileInfo> infoList) throws Exception {
        return addLinkToQueueFromContainerUsingPriority(infoList, parentFile.getSaveToDirectory(), parentFile.getDescription());
    }

    public boolean addLinksToQueueFromContainer(final List<FileInfo> infoList, final File saveToDirectory, final String description, final boolean selectAdded) {
        final List<DownloadFile> files = new LinkedList<DownloadFile>();
        final boolean dontAddNotSupported = AppPrefs.getProperty(UserProp.DONT_ADD_NOTSUPPORTED_FROMCRYPTER, UserProp.DONT_ADD_NOTSUPPORTED_FROMCRYPTER_DEFAULT);
        final boolean startDownload = AppPrefs.getProperty(UserProp.AUTO_START_DOWNLOADS_FROM_DECRYPTER, UserProp.AUTO_START_DOWNLOADS_FROM_DECRYPTER_DEFAULT);
        for (final FileInfo info : infoList) {
            if (dontAddNotSupported && !pluginsManager.isSupported(info.getFileUrl())) {
                continue;
            }
            final DownloadFile downloadFile = new DownloadFile(info, saveToDirectory);
            if (description != null) {
                downloadFile.setDescription(description);
            }
            files.add(downloadFile);
        }
        final boolean[] result = new boolean[1];
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    addToList(files);
                    if (startDownload) {
                        addToQueue(files);
                    }
                    if (selectAdded) {
                        director.getContentManager().getContentPanel().selectAdded(files);
                    }
                    result[0] = true;
                } catch (Exception e) {
                    result[0] = false;
                }
            }
        };
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (InterruptedException e) {
                return false;
            } catch (InvocationTargetException e) {
                return false;
            }
        } else {
            runnable.run();
        }
        return result[0];
    }

    public boolean addLinkToQueueFromContainerUsingPriority(final List<FileInfo> infoList, final File saveToDirectory, final String description) throws Exception {
        final TreeMap<URLByPriority, FileInfo> map = new TreeMap<URLByPriority, FileInfo>();
        for (final FileInfo info : infoList) {
            final String id;
            try {
                id = pluginsManager.getServiceIDForURL(info.getFileUrl());
            } catch (NotSupportedDownloadServiceException e) {
                continue;
            }
            map.put(new URLByPriority(info.getFileUrl(), pluginsManager.getPluginMetadata(id).getPluginPriority()), info);
        }
        return !map.isEmpty() && addLinksToQueueFromContainer(Arrays.asList(map.firstEntry().getValue()), saveToDirectory, description, false);
    }

    public void setSpeedLimit(int[] indexes, int speed) {
        if (indexes.length == 0)
            return;

        synchronized (lock) {
            for (DownloadFile file : selectionToList(indexes)) {
                file.setSpeedLimit(speed);
            }
        }
    }

    public boolean isPausable(int[] indexes) {
        synchronized (this.lock) {
            if (indexes.length == 0)
                return false;
            for (int index : indexes) {
                final DownloadFile file = downloadFiles.get(index);
                final DownloadState s = file.getState();
                if (DownloadsActions.pauseEnabledStates.contains(s))
                    return true;
                if (s == DownloadState.DOWNLOADING && file.isResumeSupported()) {
                    return true;
                }
            }
            return false;
        }

    }


    public boolean isSameDownloading(DownloadFile file) {
        synchronized (lock) {
            final List<DownloadFile> list = getDownloadFilesInStates(DownloadsActions.processStates);
            for (DownloadFile downloadFile : list) {
                if (!downloadFile.equals(file) && downloadFile.getFileName().equals(file.getFileName()) && downloadFile.getSaveToDirectory().equals(file.getSaveToDirectory()) && downloadFile.getFileSize() >= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Bere v uvahu i radky vybrane na preskacku.
     *
     * @param indexes list of indexes
     * @return Vraci -1 pokud nedoslo ke zmene.
     */
    public int sortByName(int[] indexes) {
        synchronized (lock) {
            if (indexes.length == 1)
                return -1;
            final List<DownloadFile> files = selectionToList(indexes);
            //        final DownloadFile[] beforeSorting = files.toArray(new DownloadFile[files.size()]);
            final DownloadFile[] sorted = files.toArray(new DownloadFile[files.size()]);
            Arrays.sort(sorted, new Comparator<DownloadFile>() {
                public int compare(DownloadFile o1, DownloadFile o2) {
                    return o1.getFileName().compareToIgnoreCase(o2.getFileName());
                }
            });
//            boolean sameIndexes = true;
//            for (int i = 0; i < sorted.length; i++) {
//                if (getIndex(sorted[i]) != getIndex(beforeSorting[i])) {
//                    sameIndexes = false;
//                    break;
//                }
//            }
//
//            if (sameIndexes) {
//                return -1;
//            }

            if (indexes.length > 1)
                Arrays.sort(indexes);
            //final int placeIndex = getIndex(sorted[0]);
            final int placeIndex = indexes[0];
            final int length = indexes.length;
            for (int i = length - 1; i >= 0; --i) {
                downloadFiles.remove(indexes[i]);//it does not generate event to database
            }
            downloadFiles.addAll(placeIndex, Arrays.asList(sorted));
            return placeIndex;
        }
    }

    private final class URLByPriority implements Comparable<URLByPriority> {
        private int priority;
        private URL url;

        public URLByPriority(URL url, int priority) {
            this.url = url;
            this.priority = priority;
        }

        @Override
        public int compareTo(URLByPriority that) {
            return new Integer(that.priority).compareTo(this.priority);
        }

        public URL getUrl() {
            return url;
        }
    }

}
