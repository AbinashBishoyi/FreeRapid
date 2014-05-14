package cz.vity.freerapid.plugins.webclient.utils;

import cz.vity.freerapid.utilities.DataURI;
import org.junit.Assert;
import org.junit.Test;

import java.awt.image.BufferedImage;

/**
 * JUnit test for DataURI
 *
 * @author ntoskrnl
 */
public class DataURITest {

    @Test
    public void test() {
        String basicResult = DataURI.parse("data:,Hello%2C%20World!").toString();
        String base64Result = DataURI.parse("data:text/plain;base64,SGVsbG8sIFdvcmxkIQ%3D%3D").toString();
        Assert.assertEquals("Test 1, basic", "Hello, World!", basicResult);
        Assert.assertEquals("Test 1, base64", "Hello, World!", base64Result);

        DataURI basicNotNull = DataURI.parse("data:text/html;charset=utf-8,%3C%21DOCTYPE%20" +
                "html%3E%0D%0A%3Chtml%20lang%3D%22en%22%3E%0D%0A%3Chead%" +
                "3E%3Ctitle%3EEmbedded%20Window%3C%2Ftitle%3E%3C%2Fhead%" +
                "3E%0D%0A%3Cbody%3E%3Ch1%3E42%3C%2Fh1%3E%3C%2Fbody%3E%0A" +
                "%3C%2Fhtml%3E%0A%0D%0A");
        Assert.assertNotNull("Test 2, basic", basicNotNull);
        DataURI base64NotNull = DataURI.parse("data:image/png;base64,\n" +
                "iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAABGdBTUEAALGP\n" +
                "C/xhBQAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9YGARc5KB0XV+IA\n" +
                "AAAddEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRoIFRoZSBHSU1Q72QlbgAAAF1J\n" +
                "REFUGNO9zL0NglAAxPEfdLTs4BZM4DIO4C7OwQg2JoQ9LE1exdlYvBBeZ7jq\n" +
                "ch9//q1uH4TLzw4d6+ErXMMcXuHWxId3KOETnnXXV6MJpcq2MLaI97CER3N0\n" +
                "vr4MkhoXe0rZigAAAABJRU5ErkJggg==");
        Assert.assertNotNull("Test 2, base64", base64NotNull);
        BufferedImage i = base64NotNull.toImage();
        Assert.assertNotNull("Test 2, base64 image", i);
    }

}
