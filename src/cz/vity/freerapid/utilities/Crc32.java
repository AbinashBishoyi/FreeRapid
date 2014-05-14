package cz.vity.freerapid.utilities;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

/**
 * @author ntoskrnl
 * @see org.apache.commons.codec.digest.DigestUtils
 * @since 0.85
 */
public final class Crc32 {

    /**
     * Do not instantiate
     */
    private Crc32() {
    }

    /**
     * Returns the CRC32 for the bytes in an array
     *
     * @param data Data to hash
     * @return CRC32 result
     */
    public static byte[] crc32(byte[] data) {
        CRC32 crc = new CRC32();
        crc.update(data);
        return crc32ToByteArray(crc.getValue());
    }

    /**
     * Returns the CRC32 for the UTF-8 bytes of a String
     *
     * @param data Data to hash
     * @return CRC32 result
     */
    public static byte[] crc32(String data) {
        return crc32(StringUtils.getBytesUtf8(data));
    }

    /**
     * Reads through an InputStream and returns the CRC32 for the data
     *
     * @param data Data to hash
     * @return CRC32 result
     * @throws IOException if something goes wrong IO-wise
     */
    public static byte[] crc32(InputStream data) throws IOException {
        CRC32 crc = new CRC32();
        byte[] b = new byte[1024];
        int i;
        while ((i = data.read(b)) > -1) {
            crc.update(b, 0, i);
        }
        return crc32ToByteArray(crc.getValue());
    }

    private static byte[] crc32ToByteArray(long l) {
        byte[] b = new byte[4];
        ByteBuffer.wrap(b).asIntBuffer().put((int) l);
        return b;
    }

    /**
     * Returns the CRC32 for the bytes in an array
     *
     * @param data Data to hash
     * @return CRC32 result
     */
    public static String crc32Hex(byte[] data) {
        return Hex.encodeHexString(crc32(data));
    }

    /**
     * Returns the CRC32 for the UTF-8 bytes of a String
     *
     * @param data Data to hash
     * @return CRC32 result
     */
    public static String crc32Hex(String data) {
        return Hex.encodeHexString(crc32(data));
    }

    /**
     * Reads through an InputStream and returns the CRC32 for the data
     *
     * @param data Data to hash
     * @return CRC32 result
     * @throws IOException if something goes wrong IO-wise
     */
    public static String crc32Hex(InputStream data) throws IOException {
        return Hex.encodeHexString(crc32(data));
    }

}
