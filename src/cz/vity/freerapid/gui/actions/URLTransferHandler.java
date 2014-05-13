package cz.vity.freerapid.gui.actions;

import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.gui.managers.PluginsManager;
import cz.vity.freerapid.utilities.LogUtils;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vity
 */
public abstract class URLTransferHandler extends TransferHandler {

    private final static Pattern REGEXP_URL = Pattern.compile("((http|https)://)?([a-zA-Z0-9\\.\\-]+(:[a-zA-Z0-9\\.:&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])|([a-zA-Z0-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(:[0-9]+)?(/[^/][a-zA-Z0-9\\.:,\\?'\\\\/\\+&%\\$#=~_\\-@]*)*", Pattern.MULTILINE);

    //private final static String URI_LIST_MIME_TYPE = "text/uri-list;class=java.lang.String";
    private final static String URL_LIST_MIME_TYPE = "application/x-java-url; class=java.net.URL";
    private final static Logger logger = Logger.getLogger(URLTransferHandler.class.getName());

    private List<URL> urls;
    private PluginsManager pluginsManager;


    protected abstract void doDropAction(List<URL> urlList);

    public URLTransferHandler(ManagerDirector director) {
        pluginsManager = director.getPluginsManager();
    }

    private List<URL> textURIListToFileList(String data) {
        final Set<URL> list = new LinkedHashSet<URL>();
//        final String[] strings = data.split("\\p{Space}");
//        logger.info("Dragged string data " + data);
//        for (String s : strings) {
//            s = s.trim();
//            if (!s.isEmpty() && !s.startsWith("#")) {
//                logger.info("Testing for url " + s);
//                try {
//                    list.add(new URI(s).toURL());
//                } catch (URISyntaxException e) {
//                    logger.warning("Invalid URI " + e.getMessage());
//                } catch (IllegalArgumentException e) {
//                    logger.warning("Invalid argument " + e.getMessage());
//                } catch (MalformedURLException e) {
//                    logger.warning("Invalid argument " + e.getMessage());
//                }
//            }
//
//        }
        data = data.replaceAll("\\p{Punct}http", "  http");//2 spaces
        final Matcher match = REGEXP_URL.matcher(data);
        int start = 0;
        final String http = "http://";
        while (match.find(start)) {
            try {
                String spec = match.group();
                if (!spec.startsWith(http))
                    spec = http + spec;
                final URL url = new URL(spec);
                if (pluginsManager.isSupported(url))
                    list.add(url);
            } catch (MalformedURLException e) {
                //ignore
            }
            start = match.end();
        }

//        for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens();) {
//            String s = st.nextToken().trim();
//            // the line is a comment (as per the RFC 2483)
//        }
        return new LinkedList<URL>(list);
    }

    @Override
    public boolean importData(JComponent comp, Transferable t) {
        return super.importData(comp, t);
    }


    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        return super.canImport(comp, transferFlavors);
    }

    @Override
    public boolean canImport(TransferSupport support) {
        if (!support.isDrop()) {
            return super.canImport(support);
        }
        for (DataFlavor flavor : support.getDataFlavors()) {
            if (flavor.isFlavorTextType()) {
                //logger.info("canImport: JavaFileList FLAVOR: " + flavor);
                return true;
            }
            //System.out.println("flavor.getMimeType() = " + flavor.getMimeType());
            if (flavor.isMimeTypeEqual(URL_LIST_MIME_TYPE)) {
                //logger.info("canImport: URI_LIST_MIME_TYPE FLAVOR: " + flavor);
                return true;
            }

        }
        //logger.info("canImport: Rejected Flavors: " + Arrays.toString(dataFlavors));
        return false;
    }


    @Override
    public boolean importData(TransferSupport support) {
        final Transferable transferable = support.getTransferable();
        logger.info("Trying to import:" + transferable);
        DataFlavor urlFlavor = null;
        try {
            urlFlavor = new DataFlavor(URL_LIST_MIME_TYPE);
        } catch (ClassNotFoundException e) {
            LogUtils.processException(logger, e);
        }

        try {
            urls = new LinkedList<URL>();
//            final DataFlavor[] flavors = transferable.getTransferDataFlavors();
//            for (DataFlavor flavor : flavors) {
//                System.out.println("flavor = " + flavor);
//                if (flavor.equals(urlFlavor)) {
//                    System.out.println("rovnaji se");
//                }
//            }
            if (urlFlavor != null && transferable.isDataFlavorSupported(urlFlavor)) {
                try {
                    final Object transferData = transferable.getTransferData(urlFlavor);
                    if (transferData instanceof URL) {
                        final URL url = (URL) transferData;
                        if (pluginsManager.isSupported(url))
                            urls.add(url);
                        else { //search for our URLs as text
                            try {
                                final String s = URLDecoder.decode(url.toExternalForm(), "UTF-8");
                                urls.addAll(textURIListToFileList(s));
                            } catch (UnsupportedEncodingException e) {
                                //ignore
                            }
                        }
                    }
                } catch (UnsupportedFlavorException e) {
                    //ignore
                } catch (IOException e) {
                    //ignore
                }
            } else {
                DataFlavor htmlFavor = null;
                try {
                    htmlFavor = new DataFlavor("text/html;class=java.lang.String");
                } catch (ClassNotFoundException e) {
                    LogUtils.processException(logger, e);
                }
                if (htmlFavor != null && transferable.isDataFlavorSupported(htmlFavor)) {
                    String data = (String) transferable.getTransferData(htmlFavor);
                    urls = textURIListToFileList(data);
                } else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    String data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                    urls = textURIListToFileList(data);
                }
            }

            if (urls.isEmpty()) {
                logger.info("Importing drag and drop failed or unsupported.");
                doDropAction(new LinkedList<URL>());
                return false;
            }
            logger.info("Imported files " + Arrays.toString(urls.toArray()));
            doDropAction(urls);
            return true;
        } catch (UnsupportedFlavorException e) {
            LogUtils.processException(logger, e);
        } catch (IOException e) {
            LogUtils.processException(logger, e);
        }
        return false;
    }

//    @Override
//    public boolean importData(JComponent comp, Transferable transferable) {
//    }

    public List<URL> getUrls() {
        return urls;
    }
}
