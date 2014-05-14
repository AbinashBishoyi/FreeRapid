package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.plugins.exceptions.BuildMethodException;
import cz.vity.freerapid.utilities.Utils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;


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
        Locale.setDefault(new Locale("CS", "CZ"));
    }

    @Test
    public void testSetActionFromAHrefWhereATagContains() throws BuildMethodException {
        final MethodBuilder methodBuilder = getMethodBuilder();
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
        MethodBuilder methodBuilder = getMethodBuilder();
        String action = methodBuilder.setActionFromFormWhereTagContains("Zaregistruj se", false).getAction();
        Assert.assertEquals("Parsing Form tag", "/login/", action);

        try {
            methodBuilder.toGetMethod();
            fail("Should throw exception - no URL base");
        } catch (BuildMethodException e) {

        }

        String s = methodBuilder.setBaseURL("http://testbase.com/").toGetMethod().getURI().toString();
        Assert.assertEquals("Parsing Form tag", "http://testbase.com/login/", s);

        methodBuilder = getMethodBuilder();
        methodBuilder.setActionFromFormWhereTagContains("Zaregistruj se", true);
        s = methodBuilder.setBaseURL("http://testbase.com/").setEncodePathAndQuery(true).toGetMethod().getURI().toString();
        Assert.assertEquals("Parsing Form tag", "http://testbase.com/login/?login=&pass=&pamatovat=1&prihlasit=P%C5%99ihl%C3%A1sit", s);


    }

    @Test
    public void testSetActionFromFormWhereActionContains() throws BuildMethodException {
        final MethodBuilder methodBuilder = getMethodBuilder();
        String action = methodBuilder.setActionFromFormWhereActionContains("login", true).getAction();
        Assert.assertEquals("Parsing Form tag", "/login/", action);
    }

    @Test
    public void testSetActionFromFormByIndex() throws BuildMethodException {
        final MethodBuilder methodBuilder = getMethodBuilder();
        String action = methodBuilder.setActionFromFormByIndex(3, true).getAction();
        Assert.assertEquals("Parsing Form tag", "/login/", action);


    }

    @Test
    public void testSetActionFromFormByName() throws BuildMethodException {
        MethodBuilder methodBuilder = getMethodBuilder();
        String action = methodBuilder.setActionFromFormByName("test", true).getAction();
        Assert.assertEquals("Parsing Form tag", "/login/", action);

        action = methodBuilder.setActionFromFormByName("F1", true).getAction();
        Assert.assertEquals("Parsing Form tag", "./testAction/", action);

        methodBuilder = getMethodBuilder();
        action = methodBuilder.setActionFromFormByName("downForm", true).getAction();
        Assert.assertEquals("Parsing Form tag", "http://www.badongo.com/cfile/8203387", action);

        final Map<String, String> params = methodBuilder.getParameters();
        //takovy parametr se tam nesmi vyskytovat
        Assert.assertEquals("Correct parameter parsing", Boolean.FALSE, params.containsKey("pamatovat"));

        Assert.assertEquals("Correct parameter value", "269200932", params.get("cap_id"));
        Assert.assertEquals("Correct parameter value", "281f3fe24cc4894a5bb61e4a6b12a3b7", params.get("cap_secret"));
        Assert.assertEquals("Correct parameter value", "", params.get("user_code")); //parametr nema hodnotu

        methodBuilder = getMethodBuilder();
        action = methodBuilder.setActionFromFormByName("f", true).getAction();
        Assert.assertEquals("Simple Form name", "/dl/12662/9832a7e/P_hpaf.rar.html", action);

        methodBuilder = getMethodBuilder();
        action = methodBuilder.setActionFromFormByName("formular", true).getAction();
        Assert.assertEquals("Simple Form name with ' = '", "/getfile.php?152686", action);
        Assert.assertEquals("Parameters size", methodBuilder.getParameters().size(), 4);

        methodBuilder = getMethodBuilder();
        methodBuilder.setActionFromFormByName("noActionForm", true);
    }

    private MethodBuilder getMethodBuilder() {
        return new MethodBuilder(content, client);
    }

    @Test
    public void testSetActionFromTextBetween() throws BuildMethodException {
        final MethodBuilder methodBuilder = getMethodBuilder();
        String action = methodBuilder.setActionFromTextBetween("$ld('", "', '").getAction();
        String resultLink = "/process.php?process=get_link&id=300e73bd1792&tm=1236436294&th=a555de272268aa132a9d57e793e09fe9&ur=0";
        Assert.assertEquals("Searched text between text", resultLink, action);
        Assert.assertEquals("Get Method", MethodBuilder.HttpMethodEnum.GET, methodBuilder.getMethodAction());
    }

    @Test
    public void testSetActionFromAHrefWhereImgTagContains() throws BuildMethodException {
        final MethodBuilder methodBuilder = getMethodBuilder();
        String action = methodBuilder.setActionFromImgSrcWhereTagContains("/captchas/").getAction();
        String resultLink = "http://bagruj.cz/captchas/wp2fjkdzvlndpf6j1ev9.jpg";
        Assert.assertEquals("Searched IMG tag text in the tag", resultLink, action);
        Assert.assertEquals("Get Method", MethodBuilder.HttpMethodEnum.GET, methodBuilder.getMethodAction());
    }

    @Test
    public void testEncodePathAndQuery() throws BuildMethodException, URIException {
        MethodBuilder methodBuilder = getMethodBuilder();
        HttpMethod httpMethod = methodBuilder.setActionFromFormByName("downForm2", true).setEncodePathAndQuery(true).toHttpMethod();
        String resultLink = "http://www.badongo.com/cfile/%C4%8Cesk%C3%A9%20z%C3%A1zem%C3%AD/";
        Assert.assertEquals("Encoded part of action 1", resultLink, httpMethod.getURI().toString());

        methodBuilder = getMethodBuilder();
        methodBuilder.setAction("http://dla.uloz.to/Ps;Hs;fid=1563039;cid=296926994;rid=686135084;up=0;uid=0;uip=85.71.214.165;tm=1238798202;ut=f;aff=uloz.to;He;ch=3a48fe4b04126e5175db9d506ea5531f;cpnb=;cput=;cptm=;Pe/1563039/Lost S05E11_by_insomniac.rar?bD&u=0&c=296926994&De");
        httpMethod = methodBuilder.setEncodePathAndQuery(true).toHttpMethod();

        resultLink = "http://dla.uloz.to/Ps;Hs;fid=1563039;cid=296926994;rid=686135084;up=0;uid=0;uip=85.71.214.165;tm=1238798202;ut=f;aff=uloz.to;He;ch=3a48fe4b04126e5175db9d506ea5531f;cpnb=;cput=;cptm=;Pe/1563039/Lost%20S05E11_by_insomniac.rar?bD&u=0&c=296926994&De";
        Assert.assertEquals("Encoded part of action 2", resultLink, httpMethod.getURI().toString());

        methodBuilder = getMethodBuilder();
        httpMethod = methodBuilder.setActionFromFormByName("downForm2", true).setEncodePathAndQuery(true).toGetMethod();
        resultLink = "http://www.badongo.com/cfile/%C4%8Cesk%C3%A9%20z%C3%A1zem%C3%AD/";
        Assert.assertEquals("Encoded part of action 3", resultLink, httpMethod.getURI().toString());

        methodBuilder = getMethodBuilder();
        httpMethod = methodBuilder.setAction("http://www.iskladka.cz/download.php?file=1238795053_Èeský sen.zip").setParameter("test", "È").setEncodePathAndQuery(true).toGetMethod();
        resultLink = "http://www.iskladka.cz/download.php?file=1238795053_%C4%8Cesk%C3%BD%20sen.zip&test=%C4%8C";
        Assert.assertEquals("Encoded part of action 3", resultLink, httpMethod.getURI().toString());
    }


}
