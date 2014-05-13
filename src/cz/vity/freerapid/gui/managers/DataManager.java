package cz.vity.freerapid.gui.managers;

import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.value.DelayedReadValueModel;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.FWProp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.exceptions.NotSupportedDownloadServiceException;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.*;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class DataManager extends AbstractBean implements PropertyChangeListener, ListDataListener {
    private final static Logger logger = Logger.getLogger(DataManager.class.getName());

    private final ArrayListModel<DownloadFile> downloadFiles = new ArrayListModel<DownloadFile>();

    private ProcessManager processManager;
    private final ManagerDirector director;
    private final ApplicationContext context;
    private static final String FILES_LIST_XML = "filesList.xml";

    private final Object lock = new Object();
    private int completed;
    private PluginsManager pluginsManager;
    private float averageSpeed = 0;
    private int speed = 0;

    private int dataChanged = 0;
    private final Object saveFileLock = new Object();

    public DataManager(ManagerDirector director, ApplicationContext context) {
        this.director = director;
        this.context = context;
        pluginsManager = director.getPluginsManager();
        context.getApplication().addExitListener(new Application.ExitListener() {
            public boolean canExit(EventObject event) {
                if (AppPrefs.getProperty(FWProp.MINIMIZE_ON_CLOSE, FWProp.MINIMIZE_ON_CLOSE_DEFAULT) && event instanceof WindowEvent) {
                    return true;
                }
                if (isDownloading()) {
                    final int result = Swinger.getChoiceOKCancel("downloadInProgress");
                    return (result == Swinger.RESULT_OK);
                }
                return true;
            }

            public void willExit(EventObject event) {
                exitDownloading();
                saveListToBeanImmediately();
            }
        });
        updateCompleted();
        downloadFiles.addListDataListener(this);
    }

    private void exitDownloading() {
        boolean foundRunning = false;
        processManager.interrupt();
        synchronized (lock) {
            for (DownloadFile file : downloadFiles) {
                final DownloadTask task = file.getTask();
                if (task != null && !task.isTerminated()) {
                    task.cancel(true);
                    foundRunning = true;
                    if (AppPrefs.getProperty(UserProp.DOWNLOAD_ON_APPLICATION_START, UserProp.DOWNLOAD_ON_APPLICATION_START_DEFAULT)) {
                        file.setState(DownloadState.QUEUED);
                    } else
                        file.setState(DownloadState.PAUSED);
                }
            }
        }
        //  processManager.queueUpdated();
        if (foundRunning) {
            try {
                Thread.sleep(500);//time to cancel DownloadTask properly
            } catch (InterruptedException e) {
                //ignore
            }
        }
    }

    private void saveListToBeanImmediately() {
        saveToFile(downloadFiles);
    }

    private void saveToFile(ArrayListModel<DownloadFile> downloadFiles) {
        synchronized (saveFileLock) {
            logger.info("=====Saving queue into the XML file=====");
            final LocalStorage localStorage = context.getLocalStorage();
            try {
                localStorage.save(downloadFiles, FILES_LIST_XML);
            } catch (IOException e) {
                LogUtils.processException(logger, e);
            } finally {
                logger.info("=====Finishing saving queue into the XML file=====");
            }

        }
    }

    @SuppressWarnings({"unchecked"})
    private void loadListToBean() {
        final LocalStorage localStorage = context.getLocalStorage();
        final boolean downloadOnStart = AppPrefs.getProperty(UserProp.DOWNLOAD_ON_APPLICATION_START, UserProp.DOWNLOAD_ON_APPLICATION_START_DEFAULT);
        if (new File(localStorage.getDirectory(), FILES_LIST_XML).exists()) {
            final boolean removeCompleted = AppPrefs.getProperty(UserProp.REMOVE_COMPLETED_DOWNLOADS, UserProp.REMOVE_COMPLETED_DOWNLOADS_DEFAULT) == UserProp.REMOVE_COMPLETED_DOWNLOADS_AT_STARTUP;
            try {
                final Object o = localStorage.load(FILES_LIST_XML);
                if (o instanceof ArrayListModel) {
                    for (DownloadFile file : (ArrayListModel<DownloadFile>) o) {
                        final DownloadState state = file.getState();
                        if (state == DownloadState.DELETED)
                            continue;
                        if (state == DownloadState.COMPLETED && removeCompleted) {
                            continue;
                        }
                        if (state != DownloadState.COMPLETED) {
                            file.setDownloaded(0);
                        }
                        if (state == DownloadState.ERROR || state == DownloadState.SLEEPING) {
                            file.setDownloaded(0);
                            if (downloadOnStart && file.getTimeToQueued() > 0) {
                                file.setTimeToQueued(-1);
                                file.setTimeToQueuedMax(-1);
                                file.setState(DownloadState.QUEUED);
                            }
                        }
                        if (DownloadState.isProcessState(state)) {
                            if (downloadOnStart) {
                                file.setState(DownloadState.QUEUED);
                            } else
                                file.setState(DownloadState.PAUSED);
                        }
                        file.resetSpeed();
                        file.setTimeToQueued(-1);
                        file.addPropertyChangeListener(this);
                        this.downloadFiles.add(file);
                    }
                }
            } catch (IOException e) {
                LogUtils.processException(logger, e);
            }
        }
    }

    public void initProcessManager() {
        synchronized (lock) {
            loadListToBean();
        }
        processManager = new ProcessManager(director, context);
        processManager.start();
        if (AppPrefs.getProperty(UserProp.AUTOSAVE_ENABLED, UserProp.AUTOSAVE_ENABLED_DEFAULT)) {

            PropertyAdapter<DataManager> adapter = new PropertyAdapter<DataManager>(this, "dataChanged", true);

            final int time = AppPrefs.getProperty(UserProp.AUTOSAVE_TIME, UserProp.AUTOSAVE_TIME_DEFAULT);

            DelayedReadValueModel delayedReadValueModel = new DelayedReadValueModel(adapter, time * 1000, true);
            delayedReadValueModel.addValueChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    saveListToFileOnBackground();
                }
            });
        }
        this.addPropertyChangeListener("state", new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() == DownloadState.COMPLETED) {
                    if (AppPrefs.getProperty(UserProp.REMOVE_COMPLETED_DOWNLOADS, UserProp.REMOVE_COMPLETED_DOWNLOADS_DEFAULT) == UserProp.REMOVE_COMPLETED_DOWNLOADS_IMMEDIATELY) {
                        removeCompleted();
                    }
                }
            }
        });
    }

    public void addToQueue(List<DownloadFile> files) {
        synchronized (lock) {
            for (DownloadFile f : files) {
                f.setState(DownloadState.QUEUED);
            }
        }
        processManager.queueUpdated();
    }

    public void addToList(List<DownloadFile> files) {
        synchronized (lock) {
            addOnList(files);
        }
    }

    private void addOnList(List<DownloadFile> files) {
        final boolean startFromTop = AppPrefs.getProperty(UserProp.START_FROM_FROM_TOP, UserProp.START_FROM_FROM_TOP_DEFAULT);
        for (DownloadFile file : files) {
            try {
                file.setShareDownloadServiceID(pluginsManager.getServiceIDForURL(file.getFileUrl()));
            } catch (NotSupportedDownloadServiceException e) {
                file.setState(DownloadState.ERROR);
                file.setErrorMessage("Not Supported Download Service Exception");
            }
            file.addPropertyChangeListener(this);
            if (startFromTop)
                this.downloadFiles.add(file);
            else
                this.downloadFiles.add(0, file);
        }
    }

    public ArrayListModel<DownloadFile> getDownloadFiles() {
        return downloadFiles;
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
                if ("state".equals(s)) {
                    firePropertyChange(s, evt.getOldValue(), evt.getNewValue());
                    fireDataChanged();
                } else if ("averageSpeed".equals(s)) {
                    final float oldValue = averageSpeed;
                    averageSpeed -= (Float) evt.getOldValue();
                    averageSpeed += (Float) evt.getNewValue();
                    firePropertyChange(s, oldValue, averageSpeed);
                } else if ("speed".equals(s)) {
                    final int oldValue = speed;
                    speed -= (Long) evt.getOldValue();
                    speed += (Long) evt.getNewValue();
                    firePropertyChange(s, oldValue, speed);
                }
//                    } else if ("speed".equals(s)) {
//                        firePropertyChange(s, -1, getCurrentAllSpeed());
//                        firePropertyChange("averageSpeed", -1, getAverageSpeed());
//                    }
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

    public void removeSelected(final int[] indexes) {
        synchronized (this.lock) {
            final List<DownloadFile> toRemoveList = new ArrayList<DownloadFile>();
            for (int index : indexes) {
                final DownloadFile downloadFile = downloadFiles.get(index);
                downloadFile.setState(DownloadState.DELETED);
                toRemoveList.add(downloadFile);
            }
            for (DownloadFile file : toRemoveList) {
                downloadFiles.remove(file);
            }
            //downloadFiles.removeAll(toRemoveList);
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

    private void saveListToFileOnBackground() {
        logger.info("--------saveListToBeansOnBackground------");
        final TaskService service = director.getTaskServiceManager().getTaskService(TaskServiceManager.WORK_WITH_FILE_SERVICE);
        service.execute(new Task(context.getApplication()) {
            protected Object doInBackground() throws Exception {
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                final ArrayListModel<DownloadFile> files;
                synchronized (lock) {
                    files = new ArrayListModel<DownloadFile>(downloadFiles);
                }

                saveToFile(files);

                return null;
            }

            @Override
            protected void failed(Throwable cause) {
                LogUtils.processException(logger, cause);
            }
        });

    }

    public void resumeSelected(final int[] indexes) {
        //predpoklada se, ze alespon jeden soubor splni vnitrni podminku
        synchronized (this.lock) {
            final List<DownloadFile> files = selectionToList(indexes);
            final List<DownloadFile> resumingFiles = new LinkedList<DownloadFile>();
            for (DownloadFile file : files) {
                if (DownloadState.resumeEnabledStates.contains(file.getState())) {
                    file.resetErrorAttempts();
                    resumingFiles.add(file);
                }
            }
            addToQueue(resumingFiles);
        }

    }

    public void cancelSelected(final int[] indexes) {
        synchronized (this.lock) {
            List<DownloadFile> toRemoveList = selectionToList(indexes);
            for (DownloadFile file : toRemoveList) {
                if (DownloadState.cancelEnabledStates.contains(file.getState())) {
                    final DownloadTask task = file.getTask();
                    if (task != null && !task.isTerminated()) {
                        task.cancel(true);
                        //file.setState(DownloadState.CANCELLED);
                    }
                    file.setState(DownloadState.CANCELLED);
                    file.setDownloaded(0);
                    final File outputFile = file.getOutputFile();
                    if (outputFile.exists()) {
                        outputFile.delete();
                    }
                }
            }
        }
        processManager.queueUpdated();
    }

    public List<DownloadFile> getSelectionToList(int[] indexes) {
        synchronized (lock) {
            return selectionToList(indexes);
        }
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
                if (DownloadState.pauseEnabledStates.contains(file.getState())) {
                    final DownloadTask task = file.getTask();
                    if (task != null) {
                        task.cancel(true);
                    }
                    file.setState(DownloadState.PAUSED);
                    final File outputFile = file.getOutputFile();
                    if (outputFile.exists()) {
                        outputFile.delete();
                        file.setDownloaded(0);
                    }
                }
            }
        }
        processManager.queueUpdated();
    }

    public void intervalAdded(ListDataEvent e) {
        contentsChanged(e);
        fireDataChanged();
    }

    public void intervalRemoved(ListDataEvent e) {
        contentsChanged(e);
        fireDataChanged();
    }

    public void contentsChanged(ListDataEvent e) {
        updateCompleted();
    }

    private void updateCompleted() {
//        logger.info("updateCompleted");
        synchronized (lock) {
            //          logger.info("updateCompleted2");
            int counter = 0;
            for (DownloadFile file : downloadFiles) {
                if (file.getState() == DownloadState.COMPLETED) {
                    counter++;
                }
            }
            setCompleted(counter);
        }
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
                if (file.getState() == DownloadState.COMPLETED)
                    toRemoveList.add(file);
            }
            downloadFiles.removeAll(toRemoveList);
            for (DownloadFile file : toRemoveList) {
                file.setState(DownloadState.DELETED);
            }
        }
    }

    public Object getLock() {
        return lock;
    }

    public boolean checkComplete() {
        synchronized (lock) {
            for (DownloadFile file : downloadFiles) {
                final DownloadState state = file.getState();
                if (DownloadState.isProcessState(state) || state == DownloadState.QUEUED) {
                    return false;
                }
            }
            return true;
        }
    }

//    public int getCurrentAllSpeed() {
//
//        int speed = 0;
//        averageSpeed = 0;
//        for (DownloadFile file : downloadFiles) {
//            if (file.getState() == DownloadState.DOWNLOADING) {
//                speed += file.getSpeed();
//                averageSpeed += file.getAverageSpeed();
//            }
//        }
//        return speed;
//    }


    public float getAverageSpeed() {
        return averageSpeed;
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
                    downloadFiles.add(newIndex, downloadFiles.remove(index));
                }
            }
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
                    downloadFiles.add(newIndex, downloadFiles.remove(index));
                }
                indexes[i] = newIndex;
            }
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
                    downloadFiles.add(newIndex, downloadFiles.remove(index));
                }
            }
        }
    }

    public boolean isDownloading() {
        synchronized (lock) {
            for (DownloadFile file : downloadFiles) {
                if (DownloadState.isProcessState(file.getState()))
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
        firePropertyChange("dataChanged", this.dataChanged, ++this.dataChanged);
    }

    public ProcessManager getProcessManager() {
        return processManager;
    }

    public int getCurrentSpeed() {
        return speed;
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
                downloadFiles.remove(indexes[i]);
            }
            downloadFiles.addAll(placeIndex, Arrays.asList(sorted));
            return placeIndex;
        }
    }
}
