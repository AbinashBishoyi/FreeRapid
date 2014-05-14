package cz.vity.freerapid.utilities;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.DownloadTask;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
    public static OutputStream createEmptyFile(final File f, final long size, final long startPosition, DownloadTask task) throws IOException {
        final int megabyte = 1024 * 1024;
        long written;
        RandomAccessFile fos;
        final byte[] bytes = new byte[megabyte];
        fos = null;
        long startSeek;
        if (f.exists())
            startSeek = f.length();
        else
            startSeek = 0;
        try {
            fos = new RandomAccessFile(f, "rw");
            fos.setLength(size);
            fos.seek(startSeek);
            written = startSeek;
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

        fos.seek(startPosition);
        return new FileOutputStream(fos.getFD());
    }

    public static void makeBackup(final File srcFile) throws IOException {
        if (!srcFile.exists() || srcFile.length() <= 0)
            return;
        final File backupFile = getBackupFile(srcFile);

        File tmp = new File(srcFile.getParentFile(), "FRD" + (int) (Math.random() * 1000000) + "templist.txt");
        final boolean b = srcFile.renameTo(tmp);
        if (!b) {
            logger.warning("Failed to rename oldSrc file to " + tmp);
        }
        if (backupFile.exists()) {
            final boolean result = backupFile.delete();
            if (!result) {
                logger.warning("Deleting old backup file " + backupFile + " failed.");
            }
        }

        final boolean result = tmp.renameTo(backupFile);
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

    public static File getRelativeDirectory(final File base, final File file) {
        final String relativeFile;
        try {
            relativeFile = getRelativePath(base, file);
            logger.info("Relative path of '" + file + "' to " + base + " is " + relativeFile);
        } catch (IOException e) {
            return file;
        }
        if (relativeFile == null)
            return file;
        else return new File(relativeFile);
    }

    public static void extractZipFileInto(final File zipFile, final File targetDirectory) {
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
            final byte[] buffer = new byte[8192];
            int len;
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                // Directory structure inside archive isn't preserved, but it's not really needed.
                if (!entry.isDirectory()) {
                    final File outputFile = new File(targetDirectory, entry.getName());
                    OutputStream os = null;
                    try {
                        os = new BufferedOutputStream(new FileOutputStream(outputFile));
                        while ((len = zis.read(buffer)) != -1) {
                            os.write(buffer, 0, len);
                        }
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (final Exception e) {
                                LogUtils.processException(logger, e);
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
            logger.log(Level.SEVERE, "Failed to extract archive", e);
        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (final Exception e) {
                    LogUtils.processException(logger, e);
                }
            }
        }
    }


    private static String getRelativePath(final File base, final File file) throws IOException {
        String basePath;
        String filePath = file.getCanonicalPath();
        if (base.isFile()) {
            File baseParent = base.getParentFile();
            if (baseParent == null) {
                return null;
            }
            basePath = baseParent.getCanonicalPath();
        } else {
            basePath = base.getCanonicalPath();
        }
        if (!basePath.endsWith(File.separator)) {
            basePath += File.separator;
        }
        int p = basePath.indexOf(File.separatorChar);
        String prefix = null;
        while (p != -1) {
            String newPrefix = basePath.substring(0, p + 1);
            if (!filePath.startsWith(newPrefix)) {
                break;
            }
            prefix = newPrefix;
            p = basePath.indexOf(File.separatorChar, p + 1);
        }
        if (prefix == null) {
            return null;
        }
        filePath = filePath.substring(prefix.length());
        if (prefix.length() == basePath.length()) {
            return filePath;
        }
        int c = 0;
        p = basePath.indexOf(File.separatorChar, prefix.length());
        while (p != -1) {
            c++;
            p = basePath.indexOf(File.separatorChar, p + 1);
        }
        for (int i = 0; i < c; i++) {
            filePath = ".." + File.separator + filePath; //$NON-NLS-1$
        }
        return filePath;
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

    public static String getAbsolutPath(String path) {
        return getAbsolutPath(new File(path));
    }

    public static String getAbsolutPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            return file.getAbsolutePath();
        }
    }

    public static File getAbsolutFile(File file) {
        try {
            return file.getCanonicalFile();
        } catch (IOException e) {
            return file.getAbsoluteFile();
        }
    }

    public static File getFileDrive(File file) {
        //File.listRoots()
        final FileSystemView fsView = FileSystemView.getFileSystemView();
        while (!fsView.isDrive(file)) {
            file = file.getParentFile();
        }
        return file;
    }

    /**
     * Returns the contents of the file in a byte array.
     */
    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(file);

            // Get the size of the file
            long length = file.length();

            // You cannot create an array using a long type.
            // It needs to be an int type.
            // Before converting to an int type, check
            // to ensure that file is not larger than Integer.MAX_VALUE.
            if (length > Integer.MAX_VALUE) {
                // File is too large
            }

            // Create the byte array to hold the data
            byte[] bytes = new byte[(int) length];

            // Read in the bytes
            int offset = 0;
            int numRead;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            // Ensure all the bytes have been read in
            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
            return bytes;
        } finally {
            if (is != null) {
                is.close();
            }
        }
        // Close the input stream and return bytes
    }

    public static boolean supportsRecycleBin() {
        return com.sun.jna.platform.FileUtils.getInstance().hasTrash();
    }

    public static boolean deleteFileWithRecycleBin(final File... filesToDelete) {
        if (AppPrefs.getProperty(UserProp.USE_RECYCLE_BIN, UserProp.USE_RECYCLE_BIN_DEFAULT) && supportsRecycleBin()) {
            final com.sun.jna.platform.FileUtils fileUtils = com.sun.jna.platform.FileUtils.getInstance();
            boolean failed = false;
            for (final File file : filesToDelete) {
                if (!file.exists()) {
                    continue;
                }
                try {
                    fileUtils.moveToTrash(new File[]{file});
                } catch (final Exception e) {
                    logger.log(Level.WARNING, "Failed to delete file via recycle bin: " + file, e);
                    failed = true;
                }
            }
            return !failed;
        } else {
            return deleteFile(filesToDelete);
        }
    }

    public static boolean deleteFile(final File... filesToDelete) {
        boolean failed = false;
        for (final File file : filesToDelete) {
            if (!file.exists()) {
                continue;
            }
            final boolean delete = file.delete();
            if (!delete) {
                failed = true;
                logger.warning("Failed to delete file " + file);
            }
        }
        return !failed;
    }


    public static void writeFileWithValue(final File file, final String stringValue) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(stringValue.getBytes("UTF-8"));
        } catch (final Exception e) {
            logger.warning("Failed to write plugin version file: " + e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (final Exception e) {
                    LogUtils.processException(logger, e);
                }
            }
        }
    }


}
