package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.model.DownloadFile;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * @author Vity
 */
class SpeedRegulator {

    private Set<DownloadFile> downloading = new HashSet<DownloadFile>(5);
    private final float SPEED_BACKUP = 1.3F;
    private int globalSpeed;
    private final Object lock = new Object();
    private Timer timer;
    private static final int SPEED_MINIMUM_HOLDED = 10;


    public SpeedRegulator() {
        timer = null;
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
        if (downloadingCount > 0 && globalSpeed > 0) {
            //first iteration
            long available = globalSpeed;
            Set<DownloadFile> notSatisfied = new HashSet<DownloadFile>(downloading); //neuspokojeni
            for (DownloadFile file : downloading) {
                file.setTokensLimit(0); //reset limitu na token, nutne
            }
            while (available > 0 && notSatisfied.size() > 0) { //je co a komu docpavat
                int speedPerFile = Math.max((int) (available / (float) notSatisfied.size()), 1);
                for (DownloadFile file : downloading) {
                    if (!notSatisfied.contains(file)) //pokud neni v neuspojenych, jdeme na dalsiho
                        continue;
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
                        } else if (lastTaken > 0) {
                            if (file.getTokensLimit() <= 0) //pokud je to prvni iterace while cyklu
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
                    System.out.println("available = " + available);
                    System.out.println("notSatisfied.size() = " + notSatisfied.size());
                }
            }
            //docpu zbytky - do uspokojeni a dokud je co davat
        } else {
            //zadne globalni omezeni neni, jen jejich lokalni
            for (DownloadFile file : downloading) {
                file.setTokensLimit(file.hasSpeedLimit() ? file.getSpeedLimit() : Integer.MAX_VALUE);
            }
        }
        for (DownloadFile file : downloading) {
            final int tokensLimit = file.getTokensLimit();
            System.out.println("tokensLimit = " + tokensLimit);
            final int tokens = file.getTakenTokens();
            System.out.println("taken tokens = " + tokens);
        }
        for (DownloadFile file : downloading) {
            file.setTakenTokens(0);
        }
    }

    /**
     * Vraci true, pokud ma dotycny soubor dostatek tokenu (muze stahovat)
     *
     * @param file soubor, ktery stahuje
     * @param o    kolik token soubor pri stahovani zada
     * @return true ma dostatek tokenu, jinak false
     */
    public boolean takeTokens(DownloadFile file, int kilobytes) {
        synchronized (lock) {
            final int limit = file.getTokensLimit();
            file.setTokensLimit(limit - kilobytes);
            final int taken = file.getTakenTokens();
            file.setTakenTokens((taken == -1) ? kilobytes : taken + kilobytes);
            return limit > kilobytes;
        }
    }


    public void addDownloading(DownloadFile file) {
        synchronized (lock) {
            file.setTakenTokens(-1);
            downloading.add(file);
            if (timer == null) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                        synchronized (lock) {
                            assignTokensToFiles();
                        }
                    }
                }, 0, 1000);
            }

        }
    }

    public void removeDownloading(DownloadFile file) {
        synchronized (lock) {
            downloading.remove(file);
            if (timer != null && downloading.isEmpty()) {
                timer.cancel();
                timer = null;
            }
        }
    }


}
