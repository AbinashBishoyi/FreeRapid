package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.plugins.exceptions.BuildMethodException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Vity
 */
public class DefaultFileStreamRecognizerTest {
    @Before
    public void before() throws URISyntaxException {
    }

    @Test
    public void testStreamResult() throws BuildMethodException, URIException {
        final DefaultFileStreamRecognizer rec1 = new DefaultFileStreamRecognizer(new String[]{"blabla/bleble"}, new String[]{"application/xml"}, false);

        assertTrue("Stream has to be identified", rec1.isStream(new ContentTypeMethod("blabla/bleble"), false));
        assertTrue("Stream has to be identified", rec1.isStream(new ContentTypeMethod("AUDIO/wav"), false));//test case in-sensitivity
        assertTrue("Stream has to be identified", rec1.isStream(new ContentTypeMethod("image/jpeg"), false));
        assertTrue("Stream has to be identified", rec1.isStream(new ContentTypeMethod("thisIsNotContentType/"), false));
        assertFalse("Stream has to be identified as false", rec1.isStream(new ContentTypeMethod("application/xml"), false));
        assertFalse("Stream has to be identified as false", rec1.isStream(new ContentTypeMethod("application/xml+rss"), false));
        assertFalse("Stream has to be identified as false", rec1.isStream(new ContentTypeMethod("text/plain"), false));
    }


    private class ContentTypeMethod extends PostMethod {
        private ContentTypeMethod(String contentType) {
            getResponseHeaderGroup().addHeader(new Header("Content-Type", contentType));
        }
    }
}
