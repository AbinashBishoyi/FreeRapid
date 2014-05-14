package cz.vity.freerapid.plugins.webclient.utils;

import cz.vity.freerapid.plugins.webclient.MethodBuilderTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

/**
 * JUnit test for Hash
 *
 * @author ntoskrnl
 */
public class HashTest {

    private final static String string = "freerapid";
    private File file;

    @Before
    public void before() throws URISyntaxException {
        URL url = MethodBuilderTest.class.getResource("resources/frd.gif");
        Assert.assertFalse("Load test file resource", url == null);
        file = new File(url.toURI());
    }

    @Test
    public void testGetSupportedFunctions() {
        System.out.println("Supported hash functions:");
        for (String s : Hash.getSupportedFunctions()) {
            System.out.println(s);
        }
    }

    @Test
    public void testMD5() throws IOException {
        String stringResult = Hash.md5(string).toString();
        Assert.assertEquals("MD5 test string", "c81b9d4a3aae99cef73fc426f26abbce", stringResult);

        String fileResult = Hash.md5(file).toString();
        Assert.assertEquals("MD5 test file", "aee50f4be6bbe4f2e37c3389bd3f7776", fileResult);
    }

    @Test
    public void testSHA1() throws IOException {
        String stringResult = Hash.sha1(string).toString();
        Assert.assertEquals("SHA-1 test string", "23fa5f07657b532c955b321121c82edd4a141068", stringResult);

        String fileResult = Hash.sha1(file).toString();
        Assert.assertEquals("SHA-1 test file", "53593f3d25a163d72e710cd73026573bd8e818f4", fileResult);
    }

    @Test
    public void testSHA512() throws IOException {
        String stringResult = Hash.sha512(string).toString();
        Assert.assertEquals("SHA-512 test string", "376d3223b6b7b757198b4e9b7eab15303121a9585abd4dd92f138fd4d10ae0a1097b3e15ed2bc83ee516d8ca374209516b0b496e0d83f342c702b5bcdc301e00", stringResult);

        String fileResult = Hash.sha512(file).toString();
        Assert.assertEquals("SHA-512 test file", "431abaeabb29925683fb988462d9a86fe154588c727efbbfaf7e46c7673c83e85e4960277d6ae55a2b5779a77396f9dd180fbd33c10b574b8541e0223111d3f6", fileResult);
    }

    @Test
    public void testCRC32() throws IOException {
        String stringResult = Hash.crc32(string).toString();
        Assert.assertEquals("CRC32 test string", "0c2bb9f0", stringResult);

        String fileResult = Hash.crc32(file).toString();
        Assert.assertEquals("CRC32 test file", "da5d6d89", fileResult);
    }

    @Test
    public void testMD2() throws NoSuchAlgorithmException, IOException {
        String stringResult = Hash.calc("MD2", string).toString();
        Assert.assertEquals("MD2 test string", "d36babcd0842708580fccca89ea94228", stringResult);

        String fileResult = Hash.calc("MD2", file).toString();
        Assert.assertEquals("MD2 test file", "791caa88874742b491cbc450f1627963", fileResult);
    }

}
