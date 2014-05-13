package cz.vity.freerapid.sandbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Ladislav Vitasek
 */
public class FileWriting {


    public static FileOutputStream createEmptyFile(final File f, final long size) throws IOException {
        final int megabyte = 1024 * 1024 * 1024;
        long written = 0;
        FileOutputStream fos = null;
        final byte[] bytes = new byte[megabyte];
        try {
            fos = new FileOutputStream(f);
            int toWrite = megabyte;
            while (written != size) {
                if (size - written < megabyte) {
                    toWrite = (int) (size - written);
                }
                fos.write(bytes, 0, toWrite);
                written += toWrite;
            }
        } finally {
            if (fos != null)
                fos.close();
        }

        return new FileOutputStream(new RandomAccessFile(f, "rw").getFD());
    }

    public static void main(String[] args) throws IOException, InterruptedException {


        final RandomAccessFile randomAccessFile = new RandomAccessFile(new File("d:/tempwriter.txt"), "rw");
        final FileOutputStream stream = new FileOutputStream(randomAccessFile.getFD());

        stream.close();
        Thread.sleep(10000);
    }
}
