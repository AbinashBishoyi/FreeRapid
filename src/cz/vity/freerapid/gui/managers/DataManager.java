package cz.vity.freerapid.gui.managers;

import com.jgoodies.binding.list.ArrayListModel;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.ConnectionSettings;
import cz.vity.freerapid.core.tasks.DownloadClient;
import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.model.DownloadState;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Sound;
import org.jdesktop.application.AbstractBean;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.LocalStorage;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
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

    private final List<ConnectionSettings> availableConnections = new ArrayList<ConnectionSettings>(2);
    private final ArrayListModel<DownloadFile> downloadFiles = new ArrayListModel<DownloadFile>();

    private ProcessManager processManager;
    private final ManagerDirector director;
    private final ApplicationContext context;
    private static final String FILES_LIST_XML = "filesList.xml";

    private final Object lock = new Object();
    private int completed;

    public DataManager(ManagerDirector director, ApplicationContext context) {
        this.director = director;
        this.context = context;
        availableConnections.add(new ConnectionSettings());
        final ConnectionSettings connectionSettings = new ConnectionSettings();
        connectionSettings.setProxy("exfort.org", 8118);
        availableConnections.add(connectionSettings);
        context.getApplication().addExitListener(new Application.ExitListener() {
            public boolean canExit(EventObject event) {
                return true;
            }

            public void willExit(EventObject event) {
                cancelDownloading();
                saveListToBeans();
            }
        });
        updateCompleted();
        downloadFiles.addListDataListener(this);
    }

    private void cancelDownloading() {
        synchronized (lock) {
            for (DownloadFile file : downloadFiles) {
                final DownloadTask task = file.getTask();
                if (task != null && !task.isCancelled()) {
                    task.cancel(true);
                    if (AppPrefs.getProperty(UserProp.DOWNLOAD_ON_START, false)) {
                        file.setState(DownloadState.PAUSED);
                    } else
                        file.setState(DownloadState.QUEUED);
                }
            }
            processManager.clearQueue();
        }
    }

    private void saveListToBeans() {
        final LocalStorage localStorage = context.getLocalStorage();
        try {
            localStorage.save(downloadFiles, FILES_LIST_XML);
        } catch (IOException e) {
            LogUtils.processException(logger, e);
        }
    }

    @SuppressWarnings({"unchecked"})
    private void loadListToBean() {
        final LocalStorage localStorage = context.getLocalStorage();
        if (new File(localStorage.getDirectory(), FILES_LIST_XML).exists()) {
            try {
                final Object o = localStorage.load(FILES_LIST_XML);
                if (o instanceof ArrayListModel) {
                    for (DownloadFile file : (ArrayListModel<DownloadFile>) o) {
                        if (file.getState() != DownloadState.COMPLETED) {
                            file.setDownloaded(0);
                        }
                        file.addPropertyChangeListener(this);
                        this.downloadFiles.add(file);
                    }
                    queueQueued();
                }
            } catch (IOException e) {
                LogUtils.processException(logger, e);
            }
        }
    }

    private void queueQueued() {
//        Stack<DownloadFile> stack = new Stack<DownloadFile>();
//        for (DownloadFile file : downloadFiles) {
//            if (file.getState() == DownloadState.QUEUED) {
//                stack.push(file);
//            }
//        }
        processManager.updateNext();
    }

    public void initProcessManager() {
        synchronized (lock) {
            processManager = new ProcessManager(director, context);
            processManager.start();
            loadListToBean();
        }
    }

    public List<ConnectionSettings> getAvailableConnections() {
        return Collections.unmodifiableList(availableConnections);
    }

    public Collection<DownloadClient> getClients() {
        Collection<DownloadClient> result = new LinkedList<DownloadClient>();
        //TODO ((.*?):(.*?)@)?(.*?):(.*)

        for (ConnectionSettings availableConnection : availableConnections) {
            result.add(new DownloadClient(availableConnection));
        }
        return result;
    }

    public void addToQueue(DownloadFile file) {
        synchronized (lock) {
            file.setState(DownloadState.QUEUED);
            processManager.updateNext();
        }

    }

    public void addToQueue(List<DownloadFile> files) {
        synchronized (lock) {
            for (DownloadFile f : files) {
                f.setState(DownloadState.QUEUED);
            }
            processManager.updateNext();
        }
    }

    public void addToList(List<DownloadFile> files) {
        synchronized (lock) {
            addOnList(files);
        }
    }

    private void addOnList(List<DownloadFile> files) {
        for (DownloadFile file : files) {
            file.addPropertyChangeListener(this);
            this.downloadFiles.add(0, file);
        }
    }

    public ArrayListModel<DownloadFile> getDownloadFiles() {
        return downloadFiles;
    }


    @SuppressWarnings({"SuspiciousMethodCalls"})
    private int getIndex(Object file) {
        return downloadFiles.indexOf(file);
    }

    public void propertyChange(final PropertyChangeEvent evt) {
        final String s = evt.getPropertyName();
        synchronized (this.lock) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    logger.info("Firing contents changed");
                    downloadFiles.fireContentsChanged(getIndex(evt.getSource()));
                    if ("state".equals(s)) {
                        firePropertyChange(s, evt.getOldValue(), evt.getNewValue());
                    } else if ("speed".equals(s)) {
                        firePropertyChange(s, -1, getCurrentAllSpeed());
                    }
                }
            });
        }
    }

    public boolean hasDownloadFilesStates(int[] indexes, DownloadState... states) {
        synchronized (this.lock) {
            if (indexes.length == 0)
                return false;
            for (int index : indexes) {
                final DownloadFile file = downloadFiles.get(index);
                final DownloadState s = file.getState();
                boolean found = false;
                for (DownloadState state : states) {
                    if (s == state) {
                        found = true;
                        break;
                    }
                }
                if (!found)
                    return false;
            }
            return true;
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
                if (task != null && !task.isCancelled()) {
                    task.cancel(true);
                }
                file.removePropertyChangeListener(this);
            }
            processManager.removeFromQueue(toRemoveList);
        }

    }

    public void resumeSelected(int[] indexes) {
        synchronized (this.lock) {
            addToQueue(selectionToList(indexes));
        }

    }

    public void cancelSelected(final int[] indexes) {
        synchronized (this.lock) {
            List<DownloadFile> toRemoveList = selectionToList(indexes);
            processManager.removeFromQueue(toRemoveList);
            for (DownloadFile file : toRemoveList) {
                final DownloadTask task = file.getTask();
                if (task != null && !task.isCancelled()) {
                    task.cancel(true);
                    //file.setState(DownloadState.CANCELLED);
                }
                file.setState(DownloadState.CANCELLED);
                final File outputFile = file.getOutputFile();
                if (outputFile.exists()) {
                    outputFile.delete();
                    file.setDownloaded(0);
                }
            }
        }
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
        synchronized (this.lock) {
            List<DownloadFile> toRemoveList = selectionToList(indexes);
            processManager.removeFromQueue(toRemoveList);
            for (DownloadFile file : toRemoveList) {
                final DownloadTask task = file.getTask();
                if (task != null && !task.isCancelled()) {
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

    public void intervalAdded(ListDataEvent e) {
        contentsChanged(e);

    }

    public void intervalRemoved(ListDataEvent e) {
        contentsChanged(e);
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
            if (getCompleted() <= 0)
                return;
            List<DownloadFile> toRemoveList = new LinkedList<DownloadFile>();
            for (DownloadFile file : downloadFiles) {
                if (file.getState() == DownloadState.COMPLETED)
                    toRemoveList.add(file);
            }
            downloadFiles.removeAll(toRemoveList);
        }
    }

    public Object getLock() {
        return lock;
    }

    public void checkComplete() {
        final boolean sound = AppPrefs.getProperty(UserProp.PLAY_SOUNDS, true);
        if (sound) {
            synchronized (lock) {
                boolean completed = true;
                for (DownloadFile file : downloadFiles) {
                    if (DownloadState.isProcessState(file.getState()) || file.getState() == DownloadState.QUEUED) {
                        completed = false;
                        break;
                    }
                }
                if (completed)
                    Sound.playSound("done.wav");
            }
        }
    }

    public int getCurrentAllSpeed() {
        synchronized (lock) {
            int speed = 0;
            for (DownloadFile file : downloadFiles) {
                if (file.getState() == DownloadState.DOWNLOADING) {
                    speed += file.getSpeed();
                }
            }
            return speed;
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
}
