package cz.vity.freerapid.utilities;

import cz.vity.freerapid.core.tasks.DownloadTask;

import java.io.*;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class FileUtils {
    private final static Logger logger = Logger.getLogger(FileUtils.class.getName());

    private FileUtils() {
    }

    /**
     * Vytvori prazdny soubor o dane velikosti vyplneny nulami.
     * Soubor vytvari zapisem v bufferu o velikosti 1MB.
     *
     * @param f    soubor
     * @param size velikost
     * @param task running task
     * @return soubor pro zapis nahodnych dat - nastaveny na zacatek
     * @throws IOException vyjimka pri IO
     */
    public static OutputStream createEmptyFile(final File f, final long size, DownloadTask task) throws IOException {
        final int megabyte = 1024 * 1024;
        long written = 0;
        RandomAccessFile fos;
        final byte[] bytes = new byte[megabyte];
        fos = null;
        try {
            fos = new RandomAccessFile(f, "rw");
            fos.setLength(size);
            int toWrite = megabyte;
            while (written != size) {
                if (task.isTerminated())
                    return null;
                if (size - written < megabyte) {
                    toWrite = (int) (size - written);
                }
                fos.write(bytes, 0, toWrite);
                written += toWrite;
            }
        } catch (IOException e) {
            if (fos != null) {
                fos.close();
            }
            throw e;
        }

        fos.seek(0);
        return new FileOutputStream(fos.getFD());
    }
//    public static OutputStream createEmptyFile(final File f, final long size, DownloadTask task) throws IOException {
//        final int megabyte = 1024 * 1024;
//        long written = 0;
//        FileOutputStream fos = null;
//        final byte[] bytes = new byte[megabyte];
//        try {
//            fos = new FileOutputStream(f);
//            int toWrite = megabyte;
//            while (written != size) {
//                if (task.isTerminated())
//                    return null;
//                if (size - written < megabyte) {
//                    toWrite = (int) (size - written);
//                }
//                fos.write(bytes, 0, toWrite);
//                written += toWrite;
//            }
//        } finally {
//            if (fos != null)
//                fos.close();
//        }
//
//        return new FileOutputStream(new RandomAccessFile(f, "rw").getFD());
//    }

}
