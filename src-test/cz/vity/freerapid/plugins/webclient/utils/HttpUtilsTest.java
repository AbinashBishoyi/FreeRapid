package cz.vity.freerapid.plugins.webclient.utils;

import cz.vity.freerapid.utilities.Utils;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Junit test for HttpUtils
 *
 * @author Ladislav Vitasek
 */
public class HttpUtilsTest {
    private boolean isWindows;

    @Before
    public void before() {
        isWindows = Utils.isWindows();
    }

    @Test
    public void testGetFileName() {
        final String s = HttpUtils.replaceInvalidCharsForFileSystem("diskFileName:", "_");
        if (isWindows) {
            Assert.assertEquals("Replace invalid chars for file system on Windows", "diskFileName_", s);
        } else {
            Assert.assertEquals("Replace invalid chars for file system on other file system", "diskFileName:", s);
        }
    }

    @Test
    public void testReplaceInvalidCharsForFileSystem() {
        final TestPostMethod postMethod = new TestPostMethod();
        postMethod.responseHeader = new Header("Content-Disposition", "=?UTF-8?attachment;filename=\"Two Peaks Personal Vehicle Manager 2005 3.2.zip\";?=");
        String s = HttpUtils.getFileName(postMethod);
        Assert.assertEquals("Extracting file name #1", "Two Peaks Personal Vehicle Manager 2005 3.2.zip", s);
        postMethod.responseHeader = new Header("Content-Disposition", "attachment;filename=Two Peaks Personal Vehicle Manager 2005 3.2.zip");
        s = HttpUtils.getFileName(postMethod);
        Assert.assertEquals("Extracting file name #2", "Two Peaks Personal Vehicle Manager 2005 3.2.zip", s);

        postMethod.responseHeader = new Header("Content-Disposition", "inline; filename=J1btC65dLS.torrent");
        s = HttpUtils.getFileName(postMethod);
        Assert.assertEquals("Extracting file name #3", "J1btC65dLS.torrent", s);

    }

    private static class TestPostMethod extends PostMethod {
        public Header responseHeader;

        public TestPostMethod() {
            super("http://localhost");
        }

        @Override
        public Header getResponseHeader(String s) {

            if (responseHeader.getName().equalsIgnoreCase(s))
                return responseHeader;
            return super.getResponseHeader(s);
        }
    }
}
