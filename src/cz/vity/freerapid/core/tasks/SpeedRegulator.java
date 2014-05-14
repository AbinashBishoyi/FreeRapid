package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.DownloadState;

import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * @author Vity
 */
public final class SpeedRegulator implements PropertyChangeListener {
    private final static Logger logger = Logger.getLogger(SpeedRegulator.class.getName());

    private Map<DownloadFile, DownloadFileInfo> downloading = new Hashtable<DownloadFile, DownloadFileInfo>(10);
    private final float SPEED_BACKUP = 1.3F;
    private static int globalSpeed;
    private final Object lock = new Object();
    private Timer timer;
    private static final int SPEED_MINIMUM_HOLDED = 10;
    private SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this, true);
    private volatile long speed;
    private volatile float averageSpeed;


    public SpeedRegulator() {
        timer = null;
        speed = 0;
        averageSpeed = 0;
        initProperties();
        initSpeeds();
    }

    private void initProperties() {
        AppPrefs.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {
            public void preferenceChange(PreferenceChangeEvent evt) {
                if (UserProp.SPEED_LIMIT_ENABLED.equals(evt.getKey()) || UserProp.SPEED_LIMIT.equals(evt.getKey()))
                    initSpeeds();
            }
        });
    }

    private void initSpeeds() {
        synchronized (lock) {
            if (AppPrefs.getProperty(UserProp.SPEED_LIMIT_ENABLED, UserProp.SPEED_LIMIT_ENABLED_DEFAULT))
                globalSpeed = AppPrefs.getProperty(UserProp.SPEED_LIMIT, UserProp.SPEED_LIMIT_DEFAULT);
            else globalSpeed = -1;
        }
    }

    //metoda volana kazdou sekundu
    private void assignTokensToFiles() {
        final int downloadingCount = downloading.size();//pocet souboru, ktere provadi stahovani
        final Set<DownloadFile> files = downloading.keySet();
        if (globalSpeed > 0 && downloadingCount > 0) {
            //first iteration
            long available = globalSpeed;
            Set<DownloadFile> notSatisfied = new HashSet<DownloadFile>(files); //neuspokojeni

            boolean firstIteration = true;
            do { //je co a komu docpavat
                int speedPerFile = Math.max((int) (available / (float) notSatisfied.size()), 1);
                for (DownloadFile file : files) {
                    if (!notSatisfied.contains(file)) //pokud neni v neuspojenych, jdeme na dalsiho
                        continue;
                    if (firstIteration)
                        file.setTokensLimit(0); //reset limitu na token - prvni iterace (jeste jsme nic nerozdali), nutne
                    int set;
                    final int lastTaken = file.getTakenTokens();
                    if (file.hasSpeedLimit()) { //pokud ma svuj lokalni limit
                        final int min = Math.min(file.getSpeedLimit() - file.getTokensLimit(), speedPerFile);
                        if (lastTaken < 0 || downloadingCount == 1) {
                            set = min;
                        } else if (lastTaken > 0) {
                            set = Math.min(min, (int) (lastTaken * SPEED_BACKUP));
                        } else {
                            set = Math.min(min, SPEED_MINIMUM_HOLDED);
                        }
                        assert set >= 0;
                        if (file.getTokensLimit() + set == file.getSpeedLimit())
                            notSatisfied.remove(file);
                    } else {
                        if (lastTaken < 0 || downloadingCount == 1) { //pokud se jeste nezaclo stahovat nebo pokud stahuje pouze 1 soubor
                            set = speedPerFile;
                        } else if (lastTaken > 0) {//pokud se minule neco stahlo
                            if (firstIteration) //pokud je to prvni iterace while cyklu
                                set = Math.min(speedPerFile, (int) (lastTaken * SPEED_BACKUP));
                            else { //pokud je to druha a dalsi iterace pridelovani v cyklu
                                final int last = (int) (lastTaken * SPEED_BACKUP); //nesmime dovolit pridat vic nez je 1.3 * lastTaken - prakticky se to musi chovat jako kdyby byl nastaveny speedLimit na soubor, i kdyz na nej speedlimit neni
                                set = Math.min(speedPerFile, Math.min(last, Math.abs(last - file.getTokensLimit())));
                                if (set == 0)
                                    set = 1;
                            }
                        } else {
                            set = Math.min(speedPerFile, SPEED_MINIMUM_HOLDED);
                        }
                        assert set >= 0;
                    }
                    available -= set;
                    file.setTokensLimit(file.getTokensLimit() + set);
                }
                firstIteration = false;
            } while (available > 0 && notSatisfied.size() > 0);
            //docpu zbytky - do uspokojeni a dokud je co davat
            for (DownloadFile file : files) {
//                final String s = file.getPluginID();
//                System.out.println("PluginID = " + s);
//                System.out.println("Taken tokens:" + file.getTakenTokens());
//                System.out.println("Tokens Limit:" + file.getTokensLimit());
//                System.out.println("-------------------------------");
                file.setTakenTokens(0);
            }
        } else {
            //zadne globalni omezeni neni, jen jejich lokalni
            for (DownloadFile file : files) {
                file.setTokensLimit(file.hasSpeedLimit() ? file.getSpeedLimit() : Integer.MAX_VALUE);
//                final String s = file.getPluginID();
//                System.out.println("PluginID = " + s);
//                System.out.println("Taken tokens:" + file.getTakenTokens());
//                System.out.println("Tokens Limit:" + file.getTokensLimit());
//                System.out.println("-------------------------------");
                file.setTakenTokens(0);
            }
        }
    }

    private void tick() {
        synchronized (lock) {
            assignTokensToFiles();

            long speed = 0;
            float avgSpeed = 0;
            final int size = downloading.size();
            for (DownloadFileInfo info : downloading.values()) {
                if (info.file.getState() != DownloadState.DOWNLOADING)
                    continue;
                info.tick();
                speed += info.speed;
                avgSpeed += info.averageSpeed;
            }
            fireSpeed(speed);
            fireAvgSpeed(avgSpeed / (float) size);
        }
    }


    /**
     * Vraci true, pokud ma dotycny soubor dostatek tokenu (muze stahovat)
     *
     * @param file soubor, ktery stahuje
     * @param o    kolik token soubor pri stahovani zada
     * @return true ma dostatek tokenu, jinak false
     */
    public final boolean takeTokens(DownloadFile file, final int bytes) {
        int kilobytes = (int) Math.round(bytes / 1024.0);
        synchronized (lock) {
            final DownloadFileInfo info = downloading.get(file);
            if (info == null)
                return true;
            info.counter += bytes;
            final int taken = file.getTakenTokens();
//            System.out.println("kilobytes = " + kilobytes + " (" + info.task.getBuffer().length + ")");
            file.setTakenTokens((taken == -1) ? kilobytes : taken + kilobytes);
            return file.getTokensLimit() > file.getTakenTokens();
        }
    }


    public final void addDownloading(final DownloadFile file, final DownloadTask task) {
        synchronized (lock) {
            file.setTakenTokens(-1);
            file.addPropertyChangeListener("state", this);
            file.setTokensLimit(Integer.MAX_VALUE);
            file.setSpeed(0);
            file.setAverageSpeed(0);
            downloading.put(file, new DownloadFileInfo(task));
            if (timer == null) {
                timer = new Timer("SpeedRegulatorTimer");
                timer.schedule(new TimerTask() {
                    public void run() {
                        tick();
                    }
                }, 0, 1000);
            }
        }
    }

    public final void removeDownloading(DownloadFile file) {
        synchronized (lock) {
            downloading.remove(file);
            file.removePropertyChangeListener("state", this);
            file.setSpeed(0);
//            file.setAverageSpeed(0);
            if (timer != null && downloading.isEmpty()) {
//                fireAvgSpeed(0);
                fireSpeed(0);
                timer.cancel();
                timer = null;
            }
        }
    }

    private void fireSpeed(final long newSpeed) {
        final long oldValue = this.speed;
        this.speed = newSpeed;
        pcs.firePropertyChange("speed", oldValue, this.speed);
    }

    private void fireAvgSpeed(final float newSpeed) {
        final float oldValue = this.averageSpeed;
        this.averageSpeed = newSpeed;
        pcs.firePropertyChange("averageSpeed", oldValue, this.averageSpeed);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        final DownloadFile downloadFile = (DownloadFile) evt.getSource();
        if (downloadFile.getState() != DownloadState.DOWNLOADING)
            removeDownloading(downloadFile);
    }

    private static class DownloadFileInfo {
        private volatile long counter = 0;
        private long lastSize = 0;
        private int noDataTimeOut = 0; //XXX seconds to timeout
        private short indexer = 0;
        private long[] avgSpeedArray;
        private final DownloadTask task;
        private final long startTime;
        private int avgSpeedMeasuredSeconds;
        private static final int NO_DATA_TIMEOUT_LIMIT = 120;
        private float avgSpeed;
        private final static int[] bufferSizes = {1024, 2 * 1024, 5 * 1024, 10 * 1024, 25 * 1024, 50 * 1024};
        private byte buffers[][] = new byte[bufferSizes.length][];
        private DownloadFile file;
        private long speed;
        private float averageSpeed;
        private long downloadedStart;

        DownloadFileInfo(DownloadTask task) {
            this.task = task;
            this.file = task.getDownloadFile();
            downloadedStart = this.file.getDownloaded();
            avgSpeedMeasuredSeconds = AppPrefs.getProperty(UserProp.AVG_SPEED_MEASURED_SECONDS, UserProp.AVG_SPEED_MEASURED_SECONDS_DEFAULT);
            avgSpeedArray = new long[avgSpeedMeasuredSeconds];
            Arrays.fill(avgSpeedArray, -1);
            startTime = System.currentTimeMillis();
            avgSpeed = 0;
            speed = 0;
            averageSpeed = 0;
        }

        private void updateShortAvgSpeed() {
            int i = 0;
            long sum = 0;
            for (long l : avgSpeedArray) {
                if (l != -1) {
                    sum += l;
                    ++i;
                }
            }
            avgSpeed = (i == 0) ? 0 : (float) sum / (float) i;
            file.setShortTimeAvgSpeed(avgSpeed);
        }

        void tick() {
            final long localCounter = counter;
            speed = localCounter - lastSize;

            // task.setSpeed(speed);
            file.setSpeed(speed);
            if (speed == 0) {
                if (++noDataTimeOut >= NO_DATA_TIMEOUT_LIMIT) { //X seconds with no data
                    logger.info("Cancelling download - no downloaded data during " + NO_DATA_TIMEOUT_LIMIT + " seconds");
                    averageSpeed = 0;
                    task.setConnectionTimeOut(true);
                    task.cancel(true);
                    return;
                }
            } else {
                noDataTimeOut = 0;
                lastSize = localCounter;
                file.setDownloaded(downloadedStart + localCounter);
            }

            final long time = System.currentTimeMillis() - startTime;
            //System.out.println("time = " + time);
            final float l = time / 1000.0F;

            averageSpeed = (Float.compare(l, 0) == 0) ? 0.0F : (float) localCounter / l;
            file.setAverageSpeed(averageSpeed);
            if (indexer == avgSpeedMeasuredSeconds)
                indexer = 0;
            avgSpeedArray[indexer++] = speed;
            updateShortAvgSpeed();
            updateBufferSize(speed);
        }

        private void updateBufferSize(long speed) {
            int real = Math.min((int) (speed / 1024.0F), file.getTokensLimit() - Math.max(file.getTakenTokens(), 0));
            if (real < 0)
                real = 10;
            if (file.hasSpeedLimit() || globalSpeed > 0)
                real /= 4;
            final int result;
            if (real > 50) {
                if (real >= 300) {
                    result = 5;
                } else result = 4;
            } else {
                if (real > 15) {
                    if (real >= 31)
                        result = 3;
                    else
                        result = 2;
                } else {
                    if (real >= 6)
                        result = 1;
                    else
                        result = 0;
                }
            }
            byte[] buffer = buffers[result];
            if (buffer != null) {
                task.setBuffer(buffer);
            } else {
                buffer = new byte[bufferSizes[result]];
                task.setBuffer(buffer);
                buffers[result] = buffer;
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public float getAverageSpeed() {
        return averageSpeed;
    }

    public long getSpeed() {
        return speed;
    }
}
