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
public final class DataURITest {

    @Test
    public void test() {
        String basicResult = DataURI.parse("data:,Hello%2C%20World!").toString();
        String base64Result = DataURI.parse("data:text/plain;base64,SGVsbG8sIFdvcmxkIQ%3D%3D").toString();
        Assert.assertEquals("Test 1, basic", "Hello, World!", basicResult);
///        Assert.assertEquals("Test 1, base64", "Hello, World!", base64Result);

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
        DataURI base64Ico = DataURI.parse("data:image/x-icon;base64,AAABAAEAEBAAAAEAIABoBAAAFgAAACgAAAAQAAAAIAAAAAEAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA7PT7%2F3zF6%2F9Ptu%2F%2FRbHx%2F0227%2F%2BTzvb%2F9vv5%2F97h0f9JeBz%2FNHoA%2Fz98Av9AfAD%2FPHsA%2F0F6AP8AAAAA%2Fvz7%2F1%2B33%2F8Mp%2Bz%2FFrHw%2FxWy8f8bs%2FT%2FHqrx%2F3zE7v%2F%2F%2F%2F7%2Ft8qp%2FzF2A%2F87gwH%2FP4ID%2Fz59AP8%2BegD%2FQ3kA%2F97s8v8botj%2FELn3%2Fwy58f8PtfL%2FD7Lw%2Fxuz9P8vq%2Bf%2F8%2Fn%2F%2F%2F779v9KhR3%2FOYYA%2F0GFAv88hgD%2FQIAC%2Fz17AP%2F0%2B%2Fj%2FN6bM%2FwC07%2F8Cxf7%2FCsP7%2Fwm%2B9v8Aqur%2FSrDb%2F%2F7%2B%2Fv%2F%2F%2FP7%2FVZEl%2FzSJAP87jQD%2FPYYA%2F0OBBf8%2BfQH%2F%2F%2F3%2F%2F9Dp8%2F84sM7%2FCrDf%2FwC14%2F8CruL%2FKqnW%2F9ns8f%2F8%2Fv%2F%2F4OjX%2Fz%2BGDf85kAD%2FPIwD%2Fz2JAv8%2BhQD%2FPoEA%2F9C7pv%2F97uv%2F%2F%2F%2F%2B%2F9Xw%2Bv%2Bw3ej%2Fls%2Fe%2F%2Brz9%2F%2F%2F%2F%2F%2F%2F%2B%2Fz6%2F22mSf8qjQH%2FOJMA%2FzuQAP85iwL%2FPIgA%2FzyFAP%2BOSSL%2FnV44%2F7J%2BVv%2FAkG7%2F7trP%2F%2F7%2F%2Ff%2F9%2F%2F7%2F6%2FLr%2F2uoRv8tjQH%2FPJYA%2FzuTAP87kwD%2FPY8A%2Fz2KAP89hAD%2Folkn%2F6RVHP%2BeSgj%2FmEgR%2F%2FHo3%2F%2F%2B%2Fv7%2F5Ozh%2F1GaJv8tlAD%2FOZcC%2FzuXAv84lAD%2FO5IC%2Fz2PAf89iwL%2FOIkA%2F6hWFf%2BcTxD%2Fpm9C%2F76ihP%2F8%2Fv%2F%2F%2B%2F%2F%2F%2F8nav%2F8fdwL%2FNZsA%2FzeZAP83mgD%2FPJQB%2FzyUAf84jwD%2FPYsB%2Fz6HAf%2BfXif%2F1r6s%2F%2F79%2F%2F%2F58u%2F%2F3r%2Bg%2F%2B3i2v%2F%2B%2F%2F3%2FmbiF%2FyyCAP87mgP%2FOpgD%2FzeWAP85lgD%2FOpEB%2Fz%2BTAP9ChwH%2F7eHb%2F%2F%2F%2F%2Fv%2F28ej%2FtWwo%2F7tUAP%2B5XQ7%2F5M%2B5%2F%2F%2F%2F%2Fv%2BbsZn%2FIHAd%2FzeVAP89lgP%2FO5MA%2FzaJCf8tZTr%2FDyuK%2F%2F3%2F%2F%2F%2F9%2F%2F%2F%2F0qmC%2F7lTAP%2FKZAT%2FvVgC%2F8iQWf%2F%2B%2F%2F3%2F%2F%2Fj%2F%2Fygpx%2F8GGcL%2FESax%2FxEgtv8FEMz%2FAALh%2FwAB1f%2F%2F%2Ff7%2F%2F%2Fz%2F%2F758O%2F%2FGXQL%2FyGYC%2F8RaAv%2FOjlf%2F%2B%2F%2F%2F%2F%2F%2F%2F%2F%2F9QU93%2FBAD0%2FwAB%2F%2F8DAP3%2FAAHz%2FwAA5f8DAtr%2F%2F%2F%2F%2F%2F%2Fv7%2B%2F%2B2bCT%2FyGMA%2F89mAP%2FBWQD%2F0q%2BD%2F%2F%2F%2B%2F%2F%2F%2F%2FP7%2FRkbg%2FwEA%2Bf8AA%2Fz%2FAQH5%2FwMA8P8AAev%2FAADf%2F%2F%2F7%2FP%2F%2F%2F%2F7%2FuINQ%2F7lXAP%2FMYwL%2FvGIO%2F%2FLm3P%2F8%2Fv%2F%2F1dT2%2FwoM5%2F8AAP3%2FAwH%2B%2FwAB%2Ff8AAfb%2FBADs%2FwAC4P8AAAAA%2F%2Fz7%2F%2BLbzP%2BmXyD%2FoUwE%2F9Gshv%2F8%2F%2F3%2F7%2FH5%2Fzo%2Fw%2F8AAdX%2FAgL6%2FwAA%2Ff8CAP3%2FAAH2%2FwAA7v8AAAAAgAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAEAAA%3D%3D");
        Assert.assertNotNull("Test 2, base64", base64Ico);
        i = base64NotNull.toImage();
        Assert.assertNotNull("Test 2, base64 image", i);
    }

}
