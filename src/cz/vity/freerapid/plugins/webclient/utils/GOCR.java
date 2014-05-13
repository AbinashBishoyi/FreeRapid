package cz.vity.freerapid.plugins.webclient.utils;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.PNMEncodeParam;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class GOCR {
    private final static Logger logger = Logger.getLogger(GOCR.class.getName());

    private final BufferedImage image;
    private final String commandLineOptions;
    private final static String PATH_WINDOWS = "tools/gocr/gocr046.exe";
    private final static String PATH_LINUX = "gocr";

    public GOCR(BufferedImage image, String commandLineOptions) {

        this.image = image;
        this.commandLineOptions = commandLineOptions;
    }


    public String recognize() throws IOException {

        final String command;
        if (Utils.isWindows()) {
            command = Utils.addFileSeparator(Utils.getAppPath()) + PATH_WINDOWS;
        } else {
            command = PATH_LINUX;
        }


        Scanner scanner = null;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            final ImageEncoder encoder = ImageCodec.createImageEncoder("PNM", out, new PNMEncodeParam());
            assert encoder != null;
            encoder.encode(image);


            final Process process = Runtime.getRuntime().exec(command + " " + commandLineOptions + " -f ASCII -");
            OutputStream processOut = process.getOutputStream();
            processOut.write(out.toByteArray());
            processOut.flush();
            processOut.close();
            scanner = new Scanner(process.getInputStream());
            final String s;
            if (scanner.hasNext()) {
                s = scanner.next();
            } else throw new IllegalStateException("No output");
            process.waitFor();
            if (process.exitValue() != 0)
                throw new IOException("Process exited abnormally");
            return s;
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            throw new IOException(e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                LogUtils.processException(logger, e);
            }

            if (scanner != null)
                try {
                    scanner.close();
                } catch (Exception e) {
                    LogUtils.processException(logger, e);
                }
        }
    }
}
