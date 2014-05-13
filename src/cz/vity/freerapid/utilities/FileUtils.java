package cz.vity.freerapid.utilities;

import cz.vity.freerapid.core.tasks.DownloadTask;

import java.io.*;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class FileUtils {
    private final static Logger logger = Logger.getLogger(FileUtils.class.getName());
    public static final String BACKUP_EXTENSION = ".backup";


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


    public static void makeBackup(final File srcFile) throws IOException {
        final File backupFile = getBackupFile(srcFile);

        if (backupFile.exists()) {
            final boolean result = backupFile.delete();
            if (!result) {
                logger.warning("Deleting old backup file " + backupFile + " failed.");
            }
        }
        final boolean result = srcFile.renameTo(backupFile);
        if (!result) {
            logger.warning("Making backup of file " + srcFile + " to backup file " + backupFile + " failed.");
        }
    }

    public static void renewBackup(final File srcFile) throws IOException {
        final File backupFile = getBackupFile(srcFile);
        try {
            if (!backupFile.exists())
                throw new FileNotFoundException("Backup file does not exists");
            copyfile(backupFile, srcFile);
        } catch (IOException e) {
            logger.warning("Renewing file " + srcFile + " from backup file " + backupFile + " failed.");
            throw e;
        }
    }

    public static File getBackupFile(final File srcFile) {
        return new File(srcFile.getParentFile(), srcFile.getName() + BACKUP_EXTENSION);
    }

    public static void copyfile(final File srcFile, final File dstFile) throws IOException {
        if (!srcFile.exists() || !srcFile.isFile())
            return;
        InputStream in = null;
        OutputStream out = null;
        try {
            final File parentFile = dstFile.getParentFile();
            if (!parentFile.exists()) {
                final boolean result = parentFile.mkdirs();
                if (!result)
                    logger.warning("Creating path " + parentFile + " failed");
            }
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(dstFile);

            final byte[] buf = new byte[8 * 1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            LogUtils.processException(logger, e);
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    LogUtils.processException(logger, e1);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    LogUtils.processException(logger, e1);
                }
            }
        }
    }
}
