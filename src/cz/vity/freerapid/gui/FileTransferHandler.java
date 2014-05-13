package cz.vity.freerapid.gui;

import cz.vity.freerapid.utilities.LogUtils;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * @author Vity
 */
abstract class FileTransferHandler extends TransferHandler {
    private final static String URI_LIST_MIME_TYPE = "text/uri-list;class=java.lang.String";
    private final static Logger logger = Logger.getLogger(FileTransferHandler.class.getName());

    protected abstract void doDropAction(List files);

    private static List<File> textURIListToFileList(String data) {
        final List<File> list = new LinkedList<File>();
        for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens();) {
            String s = st.nextToken();
            // the line is a comment (as per the RFC 2483)
            if (!s.startsWith("#")) {
                try {
                    list.add(new File(new URI(s)));
                } catch (URISyntaxException e) {
                    logger.warning("Invalid URI " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    logger.warning("Invalid argument " + e.getMessage());
                }
            }
        }
        return list;
    }

    @Override
    public boolean canImport(JComponent com, DataFlavor[] dataFlavors) {
        for (DataFlavor flavor : dataFlavors) {
            if (flavor.isFlavorJavaFileListType()) {
                //logger.info("canImport: JavaFileList FLAVOR: " + flavor);
                return true;
            }
            if (flavor.isMimeTypeEqual(URI_LIST_MIME_TYPE)) {
                //logger.info("canImport: String FLAVOR: " + flavor);
                return true;
            }

        }
        //logger.info("canImport: Rejected Flavors: " + Arrays.toString(dataFlavors));
        return false;
    }


    @Override
    public boolean importData(JComponent comp, Transferable transferable) {
        logger.info("Trying to import:" + transferable);
        DataFlavor uriListFlavor = null;
        try {
            uriListFlavor = new DataFlavor(URI_LIST_MIME_TYPE);
        } catch (ClassNotFoundException e) {
            logger.warning("Unsupported URI List MIME Type");
        }

        try {
            List files = new LinkedList();
            if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                final Object transferData = transferable.getTransferData(DataFlavor.javaFileListFlavor);
                if (transferData instanceof List) {
                    files = (List) transferData;
                }
            } else if (transferable.isDataFlavorSupported(uriListFlavor)) {
                String data = (String) transferable.getTransferData(uriListFlavor);
                files = textURIListToFileList(data);
            }

            if (files.isEmpty()) {
                logger.info("Importing drag and drop failed or unsupported.");
                doDropAction(new LinkedList<File>());
                return false;
            }
            logger.info("Imported files " + Arrays.toString(files.toArray()));
            doDropAction(files);
            return true;
        } catch (UnsupportedFlavorException e) {
            LogUtils.processException(logger, e);
        } catch (IOException e) {
            LogUtils.processException(logger, e);
        }
        return false;
    }
}
