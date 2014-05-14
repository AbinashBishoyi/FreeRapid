package cz.vity.freerapid.gui.managers.features;

import cz.vity.freerapid.gui.managers.DataManager;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.model.DownloadFile;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * @author Vity
 */
public class NotEnoughSpaceChecker implements PropertyChangeListener {
    private DataManager dataManager;

    public NotEnoughSpaceChecker(ManagerDirector director) {
        dataManager = director.getDataManager();
        dataManager.addPropertyChangeListener("fileSize", this);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        final List<DownloadFile> files = dataManager.getActualDownloadFiles();
        for (DownloadFile file : files) {

            final long fileSize = file.getFileSize();
        }
    }
}
