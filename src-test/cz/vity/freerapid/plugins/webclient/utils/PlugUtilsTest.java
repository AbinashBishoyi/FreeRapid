package cz.vity.freerapid.plugins.webclient.utils;

import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Junit test for PlugUtils
 *
 * @author Ladislav Vitasek
 */
public class PlugUtilsTest {
    @Before
    public void setUp() {
        // Add your code here
    }

    @After
    public void tearDown() {
        // Add your code here
    }

    @Test
    public void testGetFileSizeFromString() {
        assertEquals("File size from MB", PlugUtils.getFileSizeFromString("\t  2500 MB"), 2500 * 1024 * 1024L);
        assertEquals("File size from GB", PlugUtils.getFileSizeFromString("  2 \t500 GB"), 2500 * 1024L * 1024 * 1024L);
        assertEquals("File size from bytes", PlugUtils.getFileSizeFromString(" 2500 byTes"), 2500);
        assertEquals("File size with dot in a number", PlugUtils.getFileSizeFromString("25.00 kb"), new BigDecimal("25.00").multiply(BigDecimal.valueOf(1024L)).setScale(0, RoundingMode.UP).longValue());
        assertEquals("File size remove spaces", PlugUtils.getFileSizeFromString("   25     kB   "), 25 * 1024);
        assertEquals("File size with dot in a number #2", PlugUtils.getFileSizeFromString("25,75 MB"), new BigDecimal("25.75").multiply(BigDecimal.valueOf(1024 * 1024L)).setScale(0, RoundingMode.UP).longValue());

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
    public void testGetParameter() throws PluginImplementationException {
        try {
            assertEquals(PlugUtils.getParameter("par", "\"input type=\"hidden\" name=\"par\" value=val"), "val");
            fail("Should throw exception - not ending tag");
        } catch (PluginImplementationException e) {

        }
        assertEquals(PlugUtils.getParameter("par", "\"input type=\"hidden\" name=\"par\" value=\"val\">"), "val");
        assertEquals(PlugUtils.getParameter("par", "\"input type=\"hidden\" name='par' value='val'>"), "val");
        assertEquals(PlugUtils.getParameter("par", "\"input type=\"hidden\" name='par'value='val'>"), "val");
        assertEquals(PlugUtils.getParameter("par", "\"input type=\"hidden\" name=par value=val>"), "val");
        assertEquals(PlugUtils.getParameter("par", "\"input type=\"hidden\" name=par value='val'"), "val");
        assertEquals(PlugUtils.getParameter("par", "\"input type=\"hidden\" name=par value=val>"), "val");
        assertEquals(PlugUtils.getParameter("par", "\"input type=\"hidden\" name=par value=val  >"), "val");
        assertEquals(PlugUtils.getParameter("par", "\"input type=\"hidden\" name=\"PAR\" value=val  >"), "val");
    }

    @Test
    public void testAddParameters() throws PluginImplementationException {
        PostMethod postMethod = new PostMethod("http://localhost");
        try {
            PlugUtils.addParameters(postMethod, "\"input type=\"hidden\" name=\"PARY\" value=\"val", new String[]{"par"});
            fail("Should throw exception - invalid parameter name");
        } catch (PluginImplementationException e) {

        }
        postMethod.removeParameter("par");
        PlugUtils.addParameters(postMethod, "\"input type=\"hidden\" name=\"par\" value=\"val\">", new String[]{"par"});
        assertEquals(postMethod.getParameter("par").getValue(), "val");

        postMethod.removeParameter("par");
        PlugUtils.addParameters(postMethod, "\"input type=\"hidden\" name=par value=val>", new String[]{"par"});
        assertEquals(postMethod.getParameter("par").getValue(), "val");

        postMethod.removeParameter("par");
        PlugUtils.addParameters(postMethod, "\"input type=\"hidden\" name='par'value='val'>", new String[]{"par"});
        assertEquals(postMethod.getParameter("par").getValue(), "val");

        postMethod.removeParameter("par");
        PlugUtils.addParameters(postMethod, "\"input type=\"hidden\" name=\"par\" value='val'>", new String[]{"par"});
        assertEquals(postMethod.getParameter("par").getValue(), "val");

        postMethod.removeParameter("par");
        PlugUtils.addParameters(postMethod, "\"input type=\"hidden\" name=\"par\" value=val   >", new String[]{"par"});
        assertEquals(postMethod.getParameter("par").getValue(), "val");
    }

    @Test
    public void testReplaceEntities() {
        assertEquals(PlugUtils.replaceEntities("asdasd &amp; fdsdfsdf"), "asdasd & fdsdfsdf");
    }

    @Test
    public void testRecognize() {
        //
    }
}
