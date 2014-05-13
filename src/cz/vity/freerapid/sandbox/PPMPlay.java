package cz.vity.freerapid.sandbox;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.PNMEncodeParam;
import cz.vity.freerapid.utilities.LogUtils;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class PPMPlay {
    private final static Logger logger = Logger.getLogger(PPMPlay.class.getName());
    private final static String PATH = "tools/gocr/gocr046.exe";


    public static void main(String[] args) throws IOException {
        //OutputStream out = new FileOutputStream(new File("c:/test.ppm"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PNMEncodeParam param = new PNMEncodeParam();
        //param.setRaw(true);

        //new PPMEnco
        ImageEncoder encoder = ImageCodec.createImageEncoder("PNM", out, param);

        final File input = new File("d:\\captcha.gif");
        final RenderedImage image = ImageIO.read(input);
        encoder.encode(image);

        try {
            //final String command = Utils.addFileSeparator(Utils.getAppPath()) + PATH;
            final Process process = Runtime.getRuntime().exec("d:\\Downloads\\gocr046.exe -f ASCII -");
            final OutputStream processOut = process.getOutputStream();
            processOut.write(out.toByteArray());
            processOut.flush();
            processOut.close();
            final Scanner scanner = new Scanner(process.getInputStream());
            if (scanner.hasNext())
                System.out.println("has next");
            final String s = scanner.next();
            //scanner.close();
            System.out.println("s = " + s);
            process.waitFor();
            final int i = process.exitValue();
            System.out.println("i = " + i);
            //return process.exitValue() == 0;
        } catch (IOException e) {
            LogUtils.processException(logger, e);
            //return false;
        } catch (InterruptedException e) {
            LogUtils.processException(logger, e);
            //return false;
        }

    }

}
