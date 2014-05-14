package cz.vity.freerapid.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * @author Vity
 */
public class FileEncWriter extends OutputStreamWriter {
    public FileEncWriter(File file, boolean append, Charset charset) throws IOException {
        super(new FileOutputStream(file, append), charset);
    }

}
