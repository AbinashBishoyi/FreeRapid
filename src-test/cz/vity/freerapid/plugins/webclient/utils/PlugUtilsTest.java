package cz.vity.freerapid.plugins.webclient.utils;

import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.webclient.MethodBuilderTest;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.utilities.Utils;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Junit test for PlugUtils
 *
 * @author Ladislav Vitasek
 * @author ntoskrnl
 */
public class PlugUtilsTest {

    private String content;

    @Before
    public void before() throws URISyntaxException {
        final URI uri = MethodBuilderTest.class.getResource("resources/MethodBuilderTest.html").toURI();
        content = Utils.loadFile(new File(uri));
    }

    @Test
    public void testGetFileSizeFromString() throws PluginImplementationException {
        assertEquals("File size from B", PlugUtils.getFileSizeFromString("280B"), 280L);
        assertEquals("File size from BB", PlugUtils.getFileSizeFromString("280BB"), 280L);
        assertEquals("File size from bytes", PlugUtils.getFileSizeFromString("280 bytes"), 280L);
        assertEquals("File size from MB", PlugUtils.getFileSizeFromString("\t  2500 MB"), 2500 * 1024 * 1024L);
        assertEquals("File size from GB", PlugUtils.getFileSizeFromString("  2 \t500 GB"), 2500 * 1024L * 1024 * 1024L);
        assertEquals("File size from TB", PlugUtils.getFileSizeFromString("2TB"), 2 * 1024L * 1024 * 1024 * 1024);
        assertEquals("File size from bytes", PlugUtils.getFileSizeFromString(" 2500 byTes"), 2500);
        assertEquals("File size from kB", PlugUtils.getFileSizeFromString(" 2500 KbyTes"), 2500 * 1024);
        assertEquals("File size from bytes", PlugUtils.getFileSizeFromString("321 B"), 321);
        assertEquals("File size with point in value", PlugUtils.getFileSizeFromString("25.00 kb"), new BigDecimal("25.00").multiply(BigDecimal.valueOf(1024L)).setScale(0, RoundingMode.UP).longValue());
        assertEquals("File size with comma in value", PlugUtils.getFileSizeFromString("25,75 MB"), new BigDecimal("25.75").multiply(BigDecimal.valueOf(1024 * 1024L)).setScale(0, RoundingMode.UP).longValue());
        assertEquals("File size remove whitespace", PlugUtils.getFileSizeFromString("   25 \t \r\n    kB   "), 25 * 1024);
        assertEquals("File size remove \"&nbsp;\"", PlugUtils.getFileSizeFromString("25&nbsp;kB"), 25 * 1024);
        testFileSizeFromStringFailure("File size, illegal", "25A00 MB");

        final long expected = 1000 * 1024 * 1024;
        assertEquals("File size with point/comma #1", expected, PlugUtils.getFileSizeFromString("1,000.00MB"));
        assertEquals("File size with point/comma #2", expected, PlugUtils.getFileSizeFromString("1.000,00MB"));
        assertEquals("File size with point/comma #3", expected, PlugUtils.getFileSizeFromString("1.000,MB"));
        assertEquals("File size with point/comma #4", expected, PlugUtils.getFileSizeFromString("1.0.0.0,0MB"));
        assertEquals("File size with point/comma #5", expected * 1000, PlugUtils.getFileSizeFromString("1,000,000MB"));
        assertEquals("File size with point/comma #6", expected * 1000, PlugUtils.getFileSizeFromString("1,000,000.0MB"));
        testFileSizeFromStringFailure("File size with point/comma, illegal #1", "1.0,00.0MB");
        testFileSizeFromStringFailure("File size with point/comma, illegal #2", "1,0.00.0MB");
        testFileSizeFromStringFailure("File size with point/comma, illegal #3", "1.000,000,0MB");
    }

    private void testFileSizeFromStringFailure(final String message, final String string) {
        try {
            PlugUtils.getFileSizeFromString(string);
            fail(message);
        } catch (final PluginImplementationException e) {
            //test succeeded
        }
    }

    @Test
    public void testMatcher() {
        assertEquals(PlugUtils.matcher("test", "test").matches(), true);
    }

    @Test
    public void testFind() {
        assertEquals(PlugUtils.find(".*?test.*?", "xxxtestyyy"), true);
    }

    @Test
    public void testUnescapeHtml() {
        assertEquals(PlugUtils.unescapeHtml("&nbsp;&nbsp;&gt;&lt;"), "  ><");
    }

    @Test
    public void testUnescapeUnicode() throws PluginImplementationException {
        assertEquals("aý\u1234\n", PlugUtils.unescapeUnicode("a\\u00fd\\u1234\\n"));
    }

    @Test
    public void testGetParameter() throws PluginImplementationException {
        try {
            assertEquals(PlugUtils.getParameter("par", "<input type=\"hidden\" name=\"par\" value=val"), "val");
            fail("Should throw exception - not ending tag");
        } catch (PluginImplementationException e) {

        }
        assertEquals(PlugUtils.getParameter("par", "<input type=\"hidden\" name=\"par\" value=\"val\">"), "val");
        assertEquals(PlugUtils.getParameter("par", "<input type=\"hidden\" name='par' value='val'>"), "val");
        assertEquals(PlugUtils.getParameter("par", "<input type=\"hidden\" name='par'value='val'>"), "val");
        assertEquals(PlugUtils.getParameter("par", "<input type=\"hidden\" name=par value=val>"), "val");
        assertEquals(PlugUtils.getParameter("par", "<input type=\"hidden\" name=par value='val'>"), "val");
        assertEquals(PlugUtils.getParameter("par", "<input type=\"hidden\" name=par value=val>"), "val");
        assertEquals(PlugUtils.getParameter("par", "<input type=\"hidden\" name=par value=val  >"), "val");
        assertEquals(PlugUtils.getParameter("fname", "<input type=\"hidden\" name=\"fname\" value=\"\" />"), "");
        assertEquals(PlugUtils.getParameter("submitted", "<input name='submitted' type=\"hidden\" value=1>"), "1");
        assertEquals(PlugUtils.getParameter("submit_btn", "<input type=\"submit\" name=\"submit_btn\" DISABLED value=\"val\" />"), "val");
        assertEquals(PlugUtils.getParameter("par", "<input type=\"hidden\" name=\"PAR\" value=val  >"), "val");
        assertEquals(PlugUtils.getParameter("PAR", "<input type=\"hidden\" value=\"val\" name=PAR  >"), "val");
    }

    @Test
    public void testAddParameters() throws PluginImplementationException {
        PostMethod postMethod = new PostMethod("http://localhost");
        try {
            PlugUtils.addParameters(postMethod, "<input type=\"hidden\" name=\"PARY\" value=\"val>", new String[]{"par"});
            fail("Should throw exception - invalid parameter name");
        } catch (PluginImplementationException e) {

        }
        postMethod.removeParameter("par");
        PlugUtils.addParameters(postMethod, "<input type=\"hidden\" name=\"par\" value=\"val\">", new String[]{"par"});
        assertEquals(postMethod.getParameter("par").getValue(), "val");

        postMethod.removeParameter("par");
        PlugUtils.addParameters(postMethod, "<input type=\"hidden\" name=par value=val>", new String[]{"par"});
        assertEquals(postMethod.getParameter("par").getValue(), "val");

        postMethod.removeParameter("par");
        PlugUtils.addParameters(postMethod, "<input type=\"hidden\" name='par'value='val'>", new String[]{"par"});
        assertEquals(postMethod.getParameter("par").getValue(), "val");

        postMethod.removeParameter("par");
        PlugUtils.addParameters(postMethod, "<input type=\"hidden\" name=\"par\" value='val'>", new String[]{"par"});
        assertEquals(postMethod.getParameter("par").getValue(), "val");

        postMethod.removeParameter("par");
        PlugUtils.addParameters(postMethod, "<input type=\"hidden\" name=\"par\" value=val   >", new String[]{"par"});
        assertEquals(postMethod.getParameter("par").getValue(), "val");

        postMethod.removeParameter("par");
        PlugUtils.addParameters(postMethod, "<input type=\"hidden\" value=\"val\" name=par   >", new String[]{"par"});
        assertEquals(postMethod.getParameter("par").getValue(), "val");

        postMethod.removeParameter("par");
        PlugUtils.addParameters(postMethod, "<input type=\"hidden\" value = \"val\" name = par   >", new String[]{"par"});
        assertEquals(postMethod.getParameter("par").getValue(), "val");
    }

    @Test
    public void testReplaceEntities() {
        assertEquals(PlugUtils.replaceEntities("asdasd &amp; fdsdfsdf"), "asdasd & fdsdfsdf");
    }

    @Test
    public void testGetWaitTimeBetween() throws PluginImplementationException {
        assertEquals(PlugUtils.getWaitTimeBetween("waitTime = 2;", "waitTime = ", ";", TimeUnit.MINUTES), 120);
        assertEquals(PlugUtils.getWaitTimeBetween("waitTime = 2000\n\n;", "waitTime =", ";", TimeUnit.MILLISECONDS), 2);
        assertEquals(PlugUtils.getWaitTimeBetween("waitTime = 2000\n\n;", "waitTime =   ", "   ;", TimeUnit.MILLISECONDS), 2);
    }

    @Test
    public void testRecognize() {
        //
    }

    @Test
    public void testCheckName() throws PluginImplementationException {
        final String content = "strong>File name:   filename <";
        final HttpFile httpFile = new DownloadFile() {
            String fn;

            @Override
            public void setFileName(String fileName) {
                fn = fileName;
            }

            @Override
            public String getFileName() {
                return fn;
            }
        };
        PlugUtils.checkName(httpFile, content, "strong>File name:", "<");
        assertEquals(httpFile.getFileName(), "filename");
    }


    @Test
    public void testCheckSize() throws PluginImplementationException {
        final String content = "strong>File size:   5900 KB <";
        final HttpFile httpFile = new DownloadFile();
        PlugUtils.checkFileSize(httpFile, content, "strong>File size:", "<");
        assertEquals(httpFile.getFileSize(), 5900 * 1024);

        PlugUtils.checkFileSize(httpFile, this.content, "File size:</b></td>\n<td align=left>", "</td>");
        assertEquals(httpFile.getFileSize(), 18 * 1024 * 1024);
    }

    @Test
    public void testStringBetween() throws PluginImplementationException {
        final String between = PlugUtils.getStringBetween(content, "token = unescape(", ");", 2);
        assertEquals("Result between strigns", between, "pong.replace(/zzz/, \"www\").replace(/unf/g, \"d\")");
    }

    @Test
    public void testGetNumberBetween() throws PluginImplementationException {
        final int between = PlugUtils.getNumberBetween("http://uloz.to/   1386350    /quickshare.frp", "uloz.to/  ", "/quickshare.frp");
        assertEquals("Result number", between, 1386350);
    }

}
