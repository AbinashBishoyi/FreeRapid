package cz.vity.freerapid.plugins.webclient.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Junit test for ScriptUtils
 *
 * @author ntoskrnl
 */
public class ScriptUtilsTest {

    @Test
    public void testScript() throws Exception {
        final Number result = ScriptUtils.evaluateJavaScriptToNumber("eval(eval(eval(((((262896824+23150049))+eval((eval(1907)*71270+(((0+8))*23+3))))))));");
        Assert.assertEquals("Script evaluation", 421958950L, result.longValue());
    }

}
