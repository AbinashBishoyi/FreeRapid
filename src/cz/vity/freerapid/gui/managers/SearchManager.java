package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.gui.managers.search.OpenSearchDescription;
import cz.vity.freerapid.gui.managers.search.OpenSearchDescriptionBinding;
import cz.vity.freerapid.gui.managers.search.SearchItem;
import cz.vity.freerapid.utilities.Browser;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import jlibs.xml.sax.binding.BindingHandler;
import jlibs.xml.sax.binding.BindingListener;
import jlibs.xml.sax.binding.SAXContext;
import org.jdesktop.application.ApplicationContext;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class SearchManager {
    private final ApplicationContext context;
    private final ManagerDirector managerDirector;
    private final static String SEARCH_DIR = "search";
    private final static Logger logger = Logger.getLogger(SearchManager.class.getName());
    private List<SearchItem> searchItems;

    public SearchManager(ApplicationContext context, ManagerDirector managerDirector) {
        this.context = context;
        this.managerDirector = managerDirector;
    }

    public void loadSearchData() {
        final File dir = new File(Utils.getAppPath(), SEARCH_DIR);
        if (!(dir.exists() && dir.isDirectory())) {
            searchItems = new LinkedList<SearchItem>();
            return;
        }
        final File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase(Locale.ENGLISH).endsWith(".xml");
            }
        });
        searchItems = new ArrayList<SearchItem>(files.length);

        Arrays.sort(files);
        final BindingHandler handler = new BindingHandler(OpenSearchDescriptionBinding.class);
        handler.setPopulateNamespaces(true);
//        Namespaces.getSuggested().put("http://a9.com/-/spec/opensearch/1.1/", "");
        handler.setBindingListener(new BindingListener() {
            public void unresolvedElement(SAXContext<?> context) throws SAXException {
                System.out.println("context = " + context);
            }
        });
        for (File f : files) {

            logger.info("Parsing search.xml file " + f.getAbsolutePath());
            try {
                final OpenSearchDescription searchDescription = (OpenSearchDescription) handler.parse(new InputSource(new FileInputStream(f)));
                if (searchDescription == null) {
                    logger.warning("Ignoring file - this is not OpenSearchDescription file! " + f.getAbsolutePath());
                    continue;
                }
                if (searchDescription.getUrlTemplate() == null || searchDescription.getShortName() == null) {
                    logger.warning("Invalid search description " + searchDescription);
                    continue;
                }
                searchItems.add(new SearchItem(f.getName(), searchDescription));
            } catch (Exception e) {
                LogUtils.processException(logger, e);
            }

        }
    }

    public void openBrowser(SearchItem item, String searchText) {
        final OpenSearchDescription sd = item.getSearchDescription();
        final String enc = sd.getInputEncoding();
        String uri = sd.getUrlTemplate();
        uri = replaceSearchedText(searchText, enc, uri);
        final Map<String, String> parameters = sd.getUrlParams();
        if (!parameters.isEmpty()) {
            final StringBuilder builder = new StringBuilder(uri);
            if (!uri.contains("?"))
                builder.append('?');
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                final char lastChar = builder.charAt(builder.length() - 1);
                if (lastChar != '&' && lastChar != '?' && lastChar != '/')
                    builder.append('&');
                builder.append(entry.getKey()).append('=');
//                if (!encodeParameters)
//                    builder.append(entry.getValue());
//                else
                if (entry.getValue().contains("{searchTerms}")) {
                    builder.append(encode(replaceSearchedText(searchText, enc, entry.getValue()), enc));
                } else
                    builder.append(entry.getValue());
            }
            uri = builder.toString();
        }

        //String s;
//        try {
//            s = (encodePathAndQuery) ? URIUtil.encodePathQuery(uri, enc) : uri;
//        } catch (URIException e) {
//            LogUtils.processException(logger, e);
//            //throw new BuildMethodException("Cannot create URI");
//        }
        //      uri = checkURI(uri, enc);

        System.out.println("uri = " + uri);
        try {
            final URL url = new URI(uri).toURL();
            logger.info("Opening URL " + url.toExternalForm());
            Browser.openBrowser(url);
        } catch (MalformedURLException e) {
            logger.severe("Error generating URL:" + uri + " " + e.getMessage());
        } catch (URISyntaxException e) {
            logger.severe("Error generating URL:" + uri + " " + e.getMessage());
        }
    }

    private String replaceSearchedText(String searchText, String enc, String value) {

        value = value.replace("{searchTerms}", searchText);

        return value;
    }


    private String encode(String value, String encoding) {
        String encoded;
        try {
            encoded = URLEncoder.encode(value, encoding);
        } catch (UnsupportedEncodingException e) {
            return value;
        }
        return encoded;
    }

//    private String checkURI(String url, String encoding) {
//        try {
//            return new org.apache.commons.httpclient.URI(url, true, encoding).toString();
//        } catch (URIException e) {
//            logger.warning(String.format("Invalid URL - '%s' does not match URI specification with URI charset %s", url, encoding));
//            try {
//                return new org.apache.commons.httpclient.URI(URIUtil.encodePathQuery(url, encoding), true, encoding).toString();
//            } catch (URIException e1) {
//                return url;
//                //throw new BuildMethodException("Invalid URL - does not match URI specification: " + url);
//            }
//        }
//    }
//

    public List<SearchItem> getSearchItems() {
        return searchItems;
    }
}
