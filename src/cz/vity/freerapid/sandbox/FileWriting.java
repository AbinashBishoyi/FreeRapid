package cz.vity.freerapid.sandbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Ladislav Vitasek
 */
public class FileWriting {

    public static void main(String[] args) throws IOException, InterruptedException {
        final RandomAccessFile randomAccessFile = new RandomAccessFile(new File("d:/tempwriter.txt"), "rw");
        final FileOutputStream stream = new FileOutputStream(randomAccessFile.getFD());

        stream.close();
        Thread.sleep(10000);
    }
}
