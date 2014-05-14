package cz.vity.freerapid.gui.managers.interfaces;

import org.jdesktop.application.ApplicationContext;

/**
 * @author Vity
 */
public interface EventAction {
    boolean run(ApplicationContext context) throws Exception;
}
