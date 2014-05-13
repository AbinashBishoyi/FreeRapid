package cz.vity.freerapid.utilities.os;

/**
 * @author Ladislav Vitasek
 */
public interface SystemCommander {

    boolean createShortCut(OSCommand shortCutCommand);

    boolean shutDown(OSCommand shutDownCommand, boolean force);

    boolean isSupported(OSCommand command);
}
