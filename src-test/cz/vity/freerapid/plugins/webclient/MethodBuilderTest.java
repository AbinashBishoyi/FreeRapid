package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.plugins.exceptions.BuildMethodException;
import cz.vity.freerapid.utilities.Utils;
import org.apache.commons.httpclient.URIException;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * @author Vity
 */
public class MethodBuilderTest {
    private String content;
    private DownloadClient client;

    @Before
    public void before() throws URISyntaxException {
        final URI uri = MethodBuilderTest.class.getResource("resources/MethodBuilderTest.html").toURI();
        content = Utils.loadFile(new File(uri));
        client = new DownloadClient();
        client.initClient(new ConnectionSettings());
    }

    @Test
    public void testSetActionFromAHrefWhereATagContains() throws BuildMethodException {
        final MethodBuilder methodBuilder = new MethodBuilder(content, client);
        String action = methodBuilder.setActionFromAHrefWhereATagContains("Download file").getAction();
        Assert.assertEquals("Parsing A tag", "http://dl1u.savefile.com/aca4c18942687b57f454185120bb2f45/plugins.zip", action);
        action = methodBuilder.setActionFromAHrefWhereATagContains("Your Ad").getAction();
        String resultLink = "http://www.adbrite.com/mb/commerce/purchase_form.php?opid=895133&afsid=1";
        Assert.assertEquals("Parsing A tag - replaced &amp; for &", resultLink, action);
        Assert.assertEquals("Get Method", MethodBuilder.HttpMethodEnum.GET, methodBuilder.getMethodAction());

        action = methodBuilder.setActionFromAHrefWhereATagContains("CLICK HERE to download for free with Filef").getAction();
        resultLink = "http://dl009.filefactory.com/cache/dl/f/af39314//b/3/h/a1fbf022aeecaf20c76806c5/n/0bafc387y3yafwjw06fv.jpg";
        Assert.assertEquals("Parsing A tag ", resultLink, action);
        Assert.assertEquals("Get Method", MethodBuilder.HttpMethodEnum.GET, methodBuilder.getMethodAction());

        try {
            methodBuilder.setActionFromAHrefWhereATagContains("Your AdX").getAction();
            fail("Should throw exception - not such A tag");
        } catch (BuildMethodException e) {

        }

    }

    @Test
    public void testSetActionFromFormWhereTagContains() throws BuildMethodException, URIException {
        MethodBuilder methodBuilder = new MethodBuilder(content, client);
        String action = methodBuilder.setActionFromFormWhereTagContains("Zaregistruj se", false).getAction();
        Assert.assertEquals("Parsing Form tag", "/login/", action);

        try {
            methodBuilder.toGetMethod();
            fail("Should throw exception - no URL base");
        } catch (BuildMethodException e) {

        }

        String s = methodBuilder.setBaseURL("http://testbase.com/").toGetMethod().getURI().toString();
        Assert.assertEquals("Parsing Form tag", "http://testbase.com/login/", s);

        methodBuilder = new MethodBuilder(content, client);
        methodBuilder.setActionFromFormWhereTagContains("Zaregistruj se", true);
        s = methodBuilder.setBaseURL("http://testbase.com/").toGetMethod().getURI().toString();
        Assert.assertEquals("Parsing Form tag", "http://testbase.com/login/?prihlasit=P%C5%99ihl%C3%A1sit&pamatovat=1", s);


    }

    @Test
    public void testSetActionFromFormWhereActionContains() throws BuildMethodException {
        final MethodBuilder methodBuilder = new MethodBuilder(content, client);
        String action = methodBuilder.setActionFromFormWhereActionContains("login", true).getAction();
        Assert.assertEquals("Parsing Form tag", "/login/", action);
    }

    @Test
    public void testSetActionFromFormByIndex() throws BuildMethodException {
        final MethodBuilder methodBuilder = new MethodBuilder(content, client);
        String action = methodBuilder.setActionFromFormByIndex(3, true).getAction();
        Assert.assertEquals("Parsing Form tag", "/login/", action);
    }

    @Test
    public void testSetActionFromFormByName() throws BuildMethodException {
        final MethodBuilder methodBuilder = new MethodBuilder(content, client);
        String action = methodBuilder.setActionFromFormByName("test", true).getAction();
        Assert.assertEquals("Parsing Form tag", "/login/", action);

        action = methodBuilder.setActionFromFormByName("F1", true).getAction();
        Assert.assertEquals("Parsing Form tag", "./testAction/", action);

    }

    @Test
    public void testSetActionFromTextBetween() throws BuildMethodException {
        final MethodBuilder methodBuilder = new MethodBuilder(content, client);
        String action = methodBuilder.setActionFromTextBetween("$ld('", "', '").getAction();
        String resultLink = "/process.php?process=get_link&id=300e73bd1792&tm=1236436294&th=a555de272268aa132a9d57e793e09fe9&ur=0";
        Assert.assertEquals("Searched text between text", resultLink, action);
        Assert.assertEquals("Get Method", MethodBuilder.HttpMethodEnum.GET, methodBuilder.getMethodAction());
    }

    @Test
    public void testSetActionFromAHrefWhereImgTagContains() throws BuildMethodException {
        final MethodBuilder methodBuilder = new MethodBuilder(content, client);
        String action = methodBuilder.setActionFromImgSrcWhereTagContains("/captchas/").getAction();
        String resultLink = "http://bagruj.cz/captchas/wp2fjkdzvlndpf6j1ev9.jpg";
        Assert.assertEquals("Searched IMG tag text in the tag", resultLink, action);
        Assert.assertEquals("Get Method", MethodBuilder.HttpMethodEnum.GET, methodBuilder.getMethodAction());
    }
}
