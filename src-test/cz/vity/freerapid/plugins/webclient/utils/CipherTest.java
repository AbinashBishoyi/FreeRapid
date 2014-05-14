package cz.vity.freerapid.plugins.webclient.utils;

import cz.vity.freerapid.utilities.Utils;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * JUnit test for unlimited cryptography
 *
 * @author ntoskrnl
 */
public class CipherTest {

    private static final String KEY = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
    private static final String IV = "IVtestASDASDASDA";
    private static final String INPUT = "7CA3AC347D6F4C0E88207660241DA945";

    static {
        // Initialize this class as it removes the cryptography restrictions
        Utils.getAppPath();
    }

    @Test
    public void testUnlimitedKeySize() throws Exception {
        final byte[] key = Hex.decodeHex(KEY.toCharArray());
        final byte[] iv = IV.getBytes("UTF-8");
        final byte[] input = Hex.decodeHex(INPUT.toCharArray());
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
        final byte[] decrypted = cipher.doFinal(input);
        Assert.assertEquals("Hello Vity!", new String(decrypted, "UTF-8"));
    }

}
