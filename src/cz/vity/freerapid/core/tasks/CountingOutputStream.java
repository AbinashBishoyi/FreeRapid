package cz.vity.freerapid.core.tasks;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Vity
 */
final class CountingOutputStream extends ProxyOutputStream {

    volatile transient long count;

    /**
     * Writes to the given stream, counting the bytes.
     *
     * @param out
     */
    public CountingOutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public void write(int b) throws IOException {
        super.write(b);
        count++;
    }

    @Override
    public void write(byte[] b) throws IOException {
        super.write(b);
        count += b.length;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
        count += len;
    }

    public long getCount() {
        return count;
    }

    public void setCount(final long count) {
        this.count = count;
    }

}
