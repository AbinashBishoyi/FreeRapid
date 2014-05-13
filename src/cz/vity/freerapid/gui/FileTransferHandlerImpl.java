package cz.vity.freerapid.gui;

import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * Drag and drop ze systemu - soubor
 *
 * @author Vity
 */
public class FileTransferHandlerImpl extends FileTransferHandler {
    private final static Logger logger = Logger.getLogger(FileTransferHandlerImpl.class.getName());


    public FileTransferHandlerImpl(final boolean isLeft) {
        super();
    }

    @SuppressWarnings({"unchecked"})
    protected void doDropAction(List files) {
        if (!EventQueue.isDispatchThread())
            logger.warning("This is not on EDT");
        logger.info("Doing drop action");
    }
}
