package cz.vity.freerapid.gui.actions;

import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.gui.managers.PluginsManager;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import org.apache.commons.httpclient.URIException;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vity
 */
public abstract class URLTransferHandler extends TransferHandler {
    private final static Logger logger = Logger.getLogger(URLTransferHandler.class.getName());

    private final static Pattern REGEXP_URL = Pattern.compile("((http|https)://)?([a-zA-Z0-9\\.\\-]+(:[a-zA-Z0-9\\.:&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])|([a-zA-Z0-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(:[0-9]+)?(/[^/][\\p{Lu}\\p{Ll}0-9\\[\\]\\.:,\\?'\\\\/\\+&%\\$#!\\|=~_\\-@]*)*", Pattern.MULTILINE);
    private final static String URL_LIST_MIME_TYPE = "application/x-java-url; class=java.net.URL";

    private List<URL> urls;
    private PluginsManager pluginsManager;

    protected abstract void doDropAction(List<URL> urlList);

    public URLTransferHandler(ManagerDirector director) {
        pluginsManager = director.getPluginsManager();
    }

    public static List<URL> textURIListToFileList(String data, final PluginsManager pluginsManager, boolean clipboardMonitoring) {
        final Set<URI> list = new HashSet<URI>();
        final LinkedList<URL> result = new LinkedList<URL>();
        data = data.replaceAll("(\\p{Punct}|[\\t\\n\\x0B\\f\\r])http(s)?(?!%3A%2F%2F)", "  http$2");//2 spaces
        final Matcher match = REGEXP_URL.matcher(data);
        int start = 0;
        final String http = "http://";
        final Pattern dotsEndPattern = Pattern.compile("(.+)\\.{3,}");
        while (match.find(start)) {
            try {
                String spec = match.group();
                if (!spec.startsWith(http) && !spec.startsWith("https://"))
                    spec = http + spec;

                URL url = new URL(updateApostrophs(spec));
                boolean supported = pluginsManager.isSupported(url, clipboardMonitoring);
                if (!supported) {
                    //support for links like http://egydental.com/vb/redirector.php?url=http%3A%2F%2Frapidshare.com%2Ffiles%2F142677856%2FImplant_volum_1.rar
                    int index = spec.indexOf("http%3A%2F%2F");
                    if (index >= 0) {
                        int endIndex = spec.indexOf('&', index);
                        if (endIndex > 0) {
                            spec = spec.substring(index, endIndex);
                        } else
                            spec = spec.substring(index);
                        spec = Utils.urlDecode(spec);
                        url = new URL(updateApostrophs(spec));
                        supported = pluginsManager.isSupported(url, clipboardMonitoring);
                    }
                }
                if (!supported) {
                    //support for links like
                    //  http://www.agaleradodownload.com/download/d/?0PPJOQ2X=d?/moc.daolpuagem.www//:ptth
                    //  http://www.zunay.com/d/?KKLZZ2OL=d?/moc.daolpuagem.www
                    int index = spec.toLowerCase(Locale.ENGLISH).indexOf("//:ptth");
                    if (index >= 0) {
                        int startIndex = spec.indexOf("url=", 0);
                        if (startIndex != -1) {
                            if (startIndex < index) {
                                spec = Utils.reverseString(spec.substring(startIndex + 4, index));
                            } else startIndex = -1;
                        } else {
                            //? has to be prioritized to =
                            startIndex = spec.indexOf('?', 0);
                            if (startIndex != -1) {
                                if (startIndex < index) {
                                    spec = Utils.reverseString(spec.substring(startIndex + 1, index));
                                } else startIndex = -1;
                            } else {
                                startIndex = spec.indexOf('=', 0);
                                if (startIndex != -1) {
                                    if (startIndex < index) {
                                        spec = Utils.reverseString(spec.substring(startIndex + 1, index));
                                    } else startIndex = -1;
                                }
                            }
                        }
                        if (startIndex != -1) {
                            spec = Utils.urlDecode("http://" + spec);
                            url = new URL(updateApostrophs(spec));
                            supported = pluginsManager.isSupported(url, clipboardMonitoring);
                        }
                    }
                }
                if (supported) {
                    final String urlS = url.toExternalForm();
                    final int i = urlS.indexOf("...");
                    Pattern patternMatcher = null;
                    final Matcher dotsMatcher = dotsEndPattern.matcher(urlS);
                    boolean dotsEnd = dotsMatcher.matches();

                    if (i > 0 && !dotsEnd) {
                        String pattern = Pattern.quote(urlS.substring(0, i)) + ".+" + Pattern.quote(urlS.substring(i + 4));
                        patternMatcher = Pattern.compile(pattern);
                    }

                    boolean containable = false;
                    for (URI u : list) {
                        final String previouslyAdded = u.toURL().toExternalForm();
                        if (previouslyAdded.length() > urlS.length()) {
                            if (previouslyAdded.startsWith(urlS) || (patternMatcher != null && patternMatcher.matcher(previouslyAdded).matches() || (dotsEnd && previouslyAdded.startsWith(dotsMatcher.group(1))))) {
                                containable = true;
                                break;
                            }
                        }
                    }
                    if (!containable) {
                        URI uri = Utils.convertToURI(urlS);

                        if (!list.contains(uri)) {
                            list.add(uri);
                            result.add(uri.toURL());
                        }
                    }
                }
            } catch (MalformedURLException e) {
                //ignore
            } catch (URISyntaxException e) {
                //ignore
            } catch (URIException e) {
                //ignore                                
            }
            start = match.end();
        }

        return result;
    }

    private static String updateApostrophs(String spec) {
        if (spec.endsWith("'") && spec.length() > 2) {
            spec = spec.substring(0, spec.length() - 1);
        }
        return spec;
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
                return true;
            }
            if (flavor.isMimeTypeEqual(URL_LIST_MIME_TYPE)) {
                return true;
            }

        }
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

        urls = new LinkedList<URL>();
        try {
            if (urlFlavor != null && transferable.isDataFlavorSupported(urlFlavor)) {
                try {
                    final Object transferData = transferable.getTransferData(urlFlavor);
                    if (transferData instanceof URL) {
                        final URL url = (URL) transferData;
                        if (pluginsManager.isSupported(url, true))
                            urls.add(url);
                        else { //search for our URLs as text
                            try {
                                final String s = url.toExternalForm();
                                urls.addAll(textURIListToFileList(s, pluginsManager, true));
                            } catch (IllegalArgumentException e) {
                                LogUtils.processException(logger, e);
                            }

                        }
                    }
                } catch (UnsupportedFlavorException e) {
                    //ignore
                } catch (IOException e) {
                    //ignore
                } catch (NullPointerException e) { //JDK bug http://bugtracker.wordrider.net/task/953
                    LogUtils.processException(logger, e);
                }

            } else {
                DataFlavor xhtmlFavor = null;
                try {
                    xhtmlFavor = new DataFlavor("application/xhtml+xml;class=java.lang.String");
                } catch (ClassNotFoundException e) {
                    LogUtils.processException(logger, e);
                }
                if (xhtmlFavor != null && transferable.isDataFlavorSupported(xhtmlFavor)) {
                    String data = (String) transferable.getTransferData(xhtmlFavor);
                    urls = textURIListToFileList(data, pluginsManager, true);
                } else {
                    DataFlavor htmlFavor = null;
                    try {
                        htmlFavor = new DataFlavor("text/html;class=java.lang.String");
                    } catch (ClassNotFoundException e) {
                        LogUtils.processException(logger, e);
                    }
                    if (htmlFavor != null && transferable.isDataFlavorSupported(htmlFavor)) {
                        String data = (String) transferable.getTransferData(htmlFavor);
                        if (!Pattern.compile("<a\\s", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE).matcher(data).find()) {
                            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                                data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                                urls = textURIListToFileList(data, pluginsManager, true);
                            } else urls = textURIListToFileList(data, pluginsManager, true);
                        } else urls = textURIListToFileList(data, pluginsManager, true);
                    } else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        String data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                        urls = textURIListToFileList(data, pluginsManager, true);
                    }
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

    public List<URL> getUrls() {
        return urls;
    }

}
