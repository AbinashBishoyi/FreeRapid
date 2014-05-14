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
    public void testReplaceInvalidCharsForFileSystem() {
        final String s1 = HttpUtils.replaceInvalidCharsForFileSystem("diskFileName:", "_");
        final String s2 = HttpUtils.replaceInvalidCharsForFileSystem("test\u0000\u0002.txt", "_");
        final String s3 = HttpUtils.replaceInvalidCharsForFileSystem("test.", "_");
        final String s4 = HttpUtils.replaceInvalidCharsForFileSystem("com1.txt", "_");
        final String s5 = HttpUtils.replaceInvalidCharsForFileSystem("test\uFFFD.txt", "_");
        final String s6 = HttpUtils.replaceInvalidCharsForFileSystem("..", "_");
        if (isWindows) {
            Assert.assertEquals("Replace invalid chars for file system on Windows #1", "diskFileName_", s1);
            Assert.assertEquals("Replace invalid chars for file system on Windows #2", "test__.txt", s2);
            Assert.assertEquals("Replace invalid chars for file system on Windows #3", "test", s3);
            Assert.assertEquals("Replace invalid chars for file system on Windows #4", "com1_.txt", s4);
            Assert.assertEquals("Replace invalid chars for file system on Windows #5", "test_.txt", s5);
            Assert.assertEquals("Replace invalid chars for file system on Windows #6", "_", s6);
        } else {
            Assert.assertEquals("Replace invalid chars for file system on other file system #1", "diskFileName:", s1);
            Assert.assertEquals("Replace invalid chars for file system on other file system #2", "test\u0000\u0002.txt", s2);
            Assert.assertEquals("Replace invalid chars for file system on other file system #3", "test.", s3);
            Assert.assertEquals("Replace invalid chars for file system on other file system #4", "com1.txt", s4);
            Assert.assertEquals("Replace invalid chars for file system on other file system #5", "test_.txt", s5);
            Assert.assertEquals("Replace invalid chars for file system on other file system #6", "_", s6);
        }
    }

    @Test
    public void testGetFileName() {
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

        postMethod.responseHeader = new Header("Content-Disposition", "attachment; filename==?UTF-8?B?Sm9yZGkgVmlsbGFsdGEgLSBBbW5lc2lhIFRlcnJhY2UgKE9yaWdpbmFsIG1peCkubXAz?=");
        s = HttpUtils.getFileName(postMethod);
        Assert.assertEquals("Extracting file name #4", "Jordi Villalta - Amnesia Terrace (Original mix).mp3", s);

        postMethod.responseHeader = new Header("Content-Disposition", "attachment; filename*=UTF-8''Archives+andy.rar");
        s = HttpUtils.getFileName(postMethod);
        Assert.assertEquals("Extracting file name #5", "Archives andy.rar", s);
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
