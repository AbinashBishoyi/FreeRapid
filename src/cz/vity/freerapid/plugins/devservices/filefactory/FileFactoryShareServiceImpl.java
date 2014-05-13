package cz.vity.freerapid.plugins.devservices.filefactory;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.HttpFileDownloader;

import java.util.regex.Pattern;

/**
 * @author Vity
 */
public class FileFactoryShareServiceImpl extends AbstractFileShareService {
    private static final String SERVICE_NAME = "filefactory.com";
    private final static Pattern pattern = Pattern.compile("http://(www\\.)?filefactory\\.com/.*", Pattern.CASE_INSENSITIVE);

    public String getName() {
        return SERVICE_NAME;
    }

    public int getMaxDownloadsFromOneIP() {
        return 1;
    }

    public boolean supportsURL(String url) {
        return pattern.matcher(url).matches();
    }

    public void run(HttpFileDownloader downloader) throws Exception {
        super.run(downloader);
        new FileFactoryRunner().run(downloader);
    }

}
