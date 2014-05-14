package cz.vity.freerapid.utilities.os;

import java.io.IOException;
import java.util.List;

/**
 * @author Ladislav Vitasek
 */
public interface SystemCommander {

    boolean createShortCut(OSCommand shortCutCommand);

    List<String> getTopLevelWindowsList() throws IOException;

    boolean findTopLevelWindow(String stringToFind, boolean caseSensitive) throws IOException;

    boolean shutDown(OSCommand shutDownCommand, boolean force);

    boolean isSupported(OSCommand command);

    void preventSystemStandby(boolean prevent);

}
