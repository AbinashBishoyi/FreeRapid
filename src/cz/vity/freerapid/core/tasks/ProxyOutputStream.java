package cz.vity.freerapid.core.tasks;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Vity
 */
class ProxyOutputStream extends FilterOutputStream {

    /**
     * Constructs a new ProxyOutputStream.
     *
     * @param proxy the OutputStream to delegate to
     */
    public ProxyOutputStream(OutputStream proxy) {
        super(proxy);
        // the proxy is stored in a protected superclass variable named 'out'
    }

    /**
     * @see java.io.OutputStream#write(int)
     */
    public void write(int idx) throws IOException {
        out.write(idx);
    }

    /**
     * @see java.io.OutputStream#write(byte[])
     */
    public void write(byte[] bts) throws IOException {
        out.write(bts);
    }

    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    public void write(byte[] bts, int st, int end) throws IOException {
        out.write(bts, st, end);
    }

    /**
     * @see java.io.OutputStream#flush()
     */
    public void flush() throws IOException {
        out.flush();
    }

    /**
     * @see java.io.OutputStream#close()
     */
    public void close() throws IOException {
        out.close();
    }

}