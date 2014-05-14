package cz.vity.freerapid.plugins.webclient.utils;

import org.apache.commons.codec.binary.Hex;

import java.io.*;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.*;
import java.util.zip.CRC32;

/**
 * Helper class for hash functions.
 * <p>
 * An instance of this class is constructed using the
 * static factory methods. The result can be obtained
 * either as a byte array ({@link Hash#toBytes()})
 * or as a hex string ({@link Hash#toString()}).
 * <p>
 * Hash objects are immutable.
 *
 * @author ntoskrnl
 */
public final class Hash {
    private final static String MD5 = "MD5";
    private final static String SHA1 = "SHA-1";
    private final static String SHA512 = "SHA-512";
    private final static String CRC32 = "CRC32";

    private String algorithm;
    private byte[] bytes;
    private String string;

    /**
     * Private constructor. Use static factory methods instead.
     *
     * @param hashName name of hash function
     * @param bytes result bytes
     */
    private Hash(String hashName, byte[] bytes) {
        this.algorithm = hashName;
        this.bytes = bytes;
    }

    /**
     * @return name of hash function
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * @return result of the hash
     */
    public byte[] toBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    /**
     * Uses lowercase letters. If uppercase is desired,
     * {@link String#toUpperCase()} can be used.
     *
     * @return hex string representation of the result of the hash
     */
    @Override
    public String toString() {
        if (string == null) {
            string = Hex.encodeHexString(bytes);
        }
        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Hash) {
            Hash that = (Hash) o;
            return this.algorithm.equalsIgnoreCase(that.algorithm) && Arrays.equals(this.bytes, that.bytes);
        }
        return false;
    }

    ////////////////////////////
    // Static factory methods //
    ////////////////////////////

    /**
     * Calculates the specified hash of the specified data.
     *
     * @param algorithm name of hash algorithm to use
     * @param data data to hash
     * @return hash result
     * @throws NoSuchAlgorithmException if the specified hash function was not found
     */
    public static Hash calc(String algorithm, byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        return new Hash(digest.getAlgorithm(), digest.digest(data));
    }

    /**
     * Calculates the specified hash of the UTF-8 bytes of the specified string.
     *
     * @param algorithm name of hash algorithm to use
     * @param data string to hash
     * @return hash result
     * @throws NoSuchAlgorithmException if the specified hash function was not found
     */
    public static Hash calc(String algorithm, String data) throws NoSuchAlgorithmException {
        try {
            return calc(algorithm, data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            //should never happen
            return new Hash(algorithm, new byte[0]);
        }
    }

    /**
     * Calculates the specified hash of the contents of the specified file.
     *
     * @param algorithm name of hash algorithm to use
     * @param data file to hash
     * @return hash result
     * @throws NoSuchAlgorithmException if the specified hash function was not found
     * @throws IOException if something goes wrong IO-wise
     */
    public static Hash calc(String algorithm, File data) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        InputStream is = new FileInputStream(data);
        byte[] b = new byte[1024];
        int i;
        while ((i = is.read(b)) > -1) {
            digest.update(b, 0, i);
        }
        return new Hash(digest.getAlgorithm(), digest.digest());
    }

    /**
     * Calculates the MD5 of the specified data.
     *
     * @param data string to hash
     * @return hash result
     */
    public static Hash md5(byte[] data) {
        try {
            return calc(MD5, data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(MD5 + " not supported", e);
        }
    }

    /**
     * Calculates the MD5 of the UTF-8 bytes of the specified string.
     *
     * @param data string to hash
     * @return hash result
     */
    public static Hash md5(String data) {
        try {
            return calc(MD5, data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(MD5 + " not supported", e);
        }
    }

    /**
     * Calculates the MD5 of the contents of the specified file.
     *
     * @param data string to hash
     * @return hash result
     * @throws IOException if something goes wrong IO-wise
     */
    public static Hash md5(File data) throws IOException {
        try {
            return calc(MD5, data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(MD5 + " not supported", e);
        }
    }

    /**
     * Calculates the SHA-1 of the specified data.
     *
     * @param data string to hash
     * @return hash result
     */
    public static Hash sha1(byte[] data) {
        try {
            return calc(SHA1, data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(SHA1 + " not supported", e);
        }
    }

    /**
     * Calculates the SHA-1 of the UTF-8 bytes of the specified string.
     *
     * @param data string to hash
     * @return hash result
     */
    public static Hash sha1(String data) {
        try {
            return calc(SHA1, data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(SHA1 + " not supported", e);
        }
    }

    /**
     * Calculates the SHA-1 of the contents of the specified file.
     *
     * @param data string to hash
     * @return hash result
     * @throws IOException if something goes wrong IO-wise
     */
    public static Hash sha1(File data) throws IOException {
        try {
            return calc(SHA1, data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(SHA1 + " not supported", e);
        }
    }

    /**
     * Calculates the SHA-512 of the specified data.
     *
     * @param data string to hash
     * @return hash result
     */
    public static Hash sha512(byte[] data) {
        try {
            return calc(SHA512, data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(SHA512 + " not supported", e);
        }
    }

    /**
     * Calculates the SHA-512 of the UTF-8 bytes of the specified string.
     *
     * @param data string to hash
     * @return hash result
     */
    public static Hash sha512(String data) {
        try {
            return calc(SHA512, data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(SHA512 + " not supported", e);
        }
    }

    /**
     * Calculates the SHA-512 of the contents of the specified file.
     *
     * @param data string to hash
     * @return hash result
     * @throws IOException if something goes wrong IO-wise
     */
    public static Hash sha512(File data) throws IOException {
        try {
            return calc(SHA512, data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(SHA512 + " not supported", e);
        }
    }

    /**
     * Calculates the CRC32 of the specified data.
     *
     * @param data string to hash
     * @return hash result
     */
    public static Hash crc32(byte[] data) {
        CRC32 crc = new CRC32();
        crc.update(data);
        return new Hash(CRC32, crc32ToByteArray(crc.getValue()));
    }

    /**
     * Calculates the CRC32 of the UTF-8 bytes of the specified string.
     *
     * @param data string to hash
     * @return hash result
     */
    public static Hash crc32(String data) {
        try {
            return crc32(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            //should never happen
            return new Hash(CRC32, new byte[0]);
        }
    }

    /**
     * Calculates the CRC32 of the contents of the specified file.
     *
     * @param data string to hash
     * @return hash result
     * @throws IOException if something goes wrong IO-wise
     */
    public static Hash crc32(File data) throws IOException {
        CRC32 crc = new CRC32();
        InputStream is = new FileInputStream(data);
        byte[] b = new byte[1024];
        int i;
        while ((i = is.read(b)) > -1) {
            crc.update(b, 0, i);
        }
        return new Hash(CRC32, crc32ToByteArray(crc.getValue()));
    }

    private static byte[] crc32ToByteArray(long l) {
        byte[] b = new byte[4];
        ByteBuffer.wrap(b).asIntBuffer().put((int) l);
        return b;
    }

    /**
     * @return all available message digest functions in the VM
     */
    public static Collection<String> getSupportedFunctions() {
        Collection<String> result = new TreeSet<String>();
        for (Provider provider : Security.getProviders()) {
            for (Object o : provider.keySet()) {
                String key = o.toString().split(" ")[0];
                if (key.startsWith("MessageDigest.")) {
                    result.add(key.substring(14));
                } else if (key.startsWith("Alg.Alias.MessageDigest.")) {
                    result.add(key.substring(24));
                }
            }
        }
        return result;
    }

}
