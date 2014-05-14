package cz.vity.freerapid.gui.managers;

/**
 * @author Vity
 */
public class ActionManager {
    private final ManagerDirector director;


    public ActionManager(ManagerDirector director) {
        this.director = director;
    }

    public void triggerActions(EventActionType actionType) {
        switch (actionType) {
            case WHEN_ALL_DOWNLOADS_FINISH:
                if (director.getDataManager().checkAllComplete()) {

                }
                break;
            case WHEN_DOWNLOAD_FINISHES_AND_STOP_OTHERS:
                if (director.getDataManager().checkAllComplete()) {

                }
                break;
            default:
                assert false;
        }
    }
}
