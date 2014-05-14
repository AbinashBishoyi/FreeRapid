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
 * Class that compounds access to GOCR extern application - for CAPTCHA recognition
 *
 * @author Ladislav Vitasek
 */
class GOCR {
    private final static Logger logger = Logger.getLogger(GOCR.class.getName());

    private final BufferedImage image;
    private final String commandLineOptions;
    private final static String PATH_WINDOWS = "tools\\gocr\\gocr.exe";
    private final static String PATH_LINUX = "gocr";

    /**
     * Constructor
     *
     * @param image              image for OCR recognition
     * @param commandLineOptions additional command line options for GOCR application
     */
    public GOCR(BufferedImage image, String commandLineOptions) {

        this.image = image;
        this.commandLineOptions = commandLineOptions;
    }

    /**
     * Makes OCR recognition with GOCR application
     * Calls system application GOCR
     *
     * @return
     * @throws IOException error calling GOCR application or IO working with streams
     */
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
            final PNMEncodeParam param = new PNMEncodeParam();
            param.setRaw(false);
            final ImageEncoder encoder = ImageCodec.createImageEncoder("PNM", out, param);
            assert encoder != null;
            encoder.encode(image);


            final Process process = Runtime.getRuntime().exec(command + " " + commandLineOptions + " -f ASCII -");
            OutputStream processOut = process.getOutputStream();
            processOut.write(out.toByteArray());
//            processOut.flush();
            processOut.close();
            scanner = new Scanner(process.getInputStream());
            StringBuilder builder = new StringBuilder();
            final String s;
            while (scanner.hasNext()) {
                builder.append(scanner.next());
            }
            s = builder.toString();
            if (s == null || s.isEmpty())
                throw new IllegalStateException("No output");
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
