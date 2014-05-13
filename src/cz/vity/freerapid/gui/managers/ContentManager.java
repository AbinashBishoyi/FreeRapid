package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.gui.content.ContentPanel;
import org.jdesktop.application.ApplicationContext;

import java.awt.*;

/**
 * @author Vity
 */
public class ContentManager {
    private final ApplicationContext context;
    private final ManagerDirector managerDirector;
    private ContentPanel contentPanel;

    public ContentManager(ApplicationContext context, ManagerDirector managerDirector) {
        this.context = context;
        this.managerDirector = managerDirector;
    }

    public ContentPanel getContentPanel() {
        if (this.contentPanel == null)
            return this.contentPanel = new ContentPanel(context, managerDirector);
        else return this.contentPanel;
    }

    public Component getComponent() {
        return getContentPanel();
    }
}
