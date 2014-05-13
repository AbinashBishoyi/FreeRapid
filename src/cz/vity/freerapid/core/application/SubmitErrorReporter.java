package cz.vity.freerapid.core.application;

import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.gui.dialogs.SubmitErrorDialog;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorReporter;

/**
 * Odesle pomoci vestaveneho klienta vyjimku
 *
 * @author Vity
 */
public class SubmitErrorReporter implements ErrorReporter {

    public void reportError(ErrorInfo info) throws NullPointerException {
        final MainApp app = MainApp.getInstance(MainApp.class);
        final SubmitErrorDialog dialog = new SubmitErrorDialog(app.getMainFrame(), new SubmitErrorInfo(info));
        app.prepareDialog(dialog, true);
    }

}