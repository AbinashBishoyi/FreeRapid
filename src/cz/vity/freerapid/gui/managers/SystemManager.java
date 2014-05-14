package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.utilities.os.SystemCommander;
import cz.vity.freerapid.utilities.os.SystemCommanderFactory;
import org.jdesktop.application.ApplicationContext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * @author Vity
 */
public class SystemManager {
    private ManagerDirector director;
    private ApplicationContext context;
    private final static Logger logger = Logger.getLogger(SystemManager.class.getName());


    public SystemManager(ManagerDirector director, ApplicationContext context) {
        this.director = director;
        this.context = context;

    }

    void initManager() {
        final PropertyChangeListener pcl = new PropertyChangeListener() {
            private boolean lastState = false;

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                final boolean newState =
                        AppPrefs.getProperty(UserProp.PREVENT_STANDBY_WHILE_DOWNLOADING, UserProp.PREVENT_STANDBY_WHILE_DOWNLOADING_DEFAULT) && isDownloading();
                if (newState != lastState) {
                    lastState = newState;
                    if (logger.isLoggable(Level.INFO)) {
                        logger.info("preventSystemStandby(" + newState + ")");
                    }
                    final SystemCommander commander = SystemCommanderFactory.getInstance().getSystemCommanderInstance(context);
                    commander.preventSystemStandby(newState);
                }
            }
        };
        AppPrefs.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {
            @Override
            public void preferenceChange(PreferenceChangeEvent evt) {
                if (UserProp.PREVENT_STANDBY_WHILE_DOWNLOADING.equals(evt.getKey())) {
                    pcl.propertyChange(null);
                }
            }
        });
        director.getDataManager().getProcessManager().addPropertyChangeListener("downloading", pcl); //for all on download thread
    }

    private boolean isDownloading() {
        return director.getDataManager().getDownloading() > 0;
    }
}
