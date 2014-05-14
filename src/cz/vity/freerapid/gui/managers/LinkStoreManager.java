package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.tasks.LinksAddedTask;
import cz.vity.freerapid.gui.managers.interfaces.UrlListDataListener;
import cz.vity.freerapid.model.DownloadFile;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;

import java.util.List;

/**
 * @author Vity
 */
public class LinkStoreManager implements UrlListDataListener {
    private ManagerDirector director;
    private ApplicationContext context;

    //private final static Logger logger = Logger.getLogger(LinkStoreManager.class.getName());


    public LinkStoreManager(ManagerDirector director, ApplicationContext context) {
        this.director = director;
        this.context = context;
        director.getDataManager().addUrlListDataListener(this);
    }

    public void linksAdded(List<DownloadFile> list) {
        final Task task = new LinksAddedTask(context, list);
        director.getTaskServiceManager().runTask(TaskServiceManager.WORK_WITH_FILE_SERVICE, task);
    }

}
