package cz.vity.freerapid.plugins.webclient.utils;

import cz.vity.freerapid.utilities.crypto.Cipher;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;

/**
 * JUnit test for unlimited cryptography
 *
 * @author ntoskrnl
 */
public class CipherTest {
    private static final String KEY = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
    private static final String IV = "IVtestASDASDASDA";
    private static final String INPUT = "7CA3AC347D6F4C0E88207660241DA945";

    @Test
    public void testCipher() throws Exception {
        final byte[] key = Hex.decodeHex(KEY.toCharArray());
        final byte[] iv = IV.getBytes("UTF-8");
        final byte[] input = Hex.decodeHex(INPUT.toCharArray());
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
        final byte[] decrypted = cipher.doFinal(input);
        Assert.assertEquals("Hello Vity!", new String(decrypted, "UTF-8"));
    }

    @Test
    public void testException() throws Exception {
        final Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        try {
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(new byte[16], "Blowfish"));
        } catch (InvalidKeyException e) {
            return;
        } catch (Exception e) {
            Assert.fail("Wrong exception thrown - " + e);
        }
        Assert.fail("Should have thrown InvalidKeyException");
    }

}
