package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.exceptions.FileTransferFailedException;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.Application;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class MoveFileTask extends CoreTask<Void, Void> {
    private final static Logger logger = Logger.getLogger(MoveFileTask.class.getName());

    private final File from;
    private File to;
    private final boolean deleteSource;
    private final boolean overWriteExisting;
    private final DownloadFile downloadFile;
    private static final int BSIZE = 4096;

    public MoveFileTask(Application application, File from, File to, final boolean deleteSource, boolean overWriteExisting, DownloadFile downloadFile) {
        super(application);
        this.from = from;
        this.to = to;
        this.deleteSource = deleteSource;
        this.overWriteExisting = overWriteExisting;
        this.downloadFile = downloadFile;
        setTaskToForeground();
    }

    protected Void doInBackground() throws Exception {

//        SwingUtilities.invokeAndWait(new Runnable() {
//            public void run() {
//                Swinger.showInformationDialog("Finished downloading");
//            }
//        });

        if (!from.exists())
            return null;

        if (to.exists()) {
            if (!overWriteExisting) {
                to = getNewUniqueFileName(to);
            } else to.delete();
        }

        message("Moving_file");
        final File parentToDir = to.getParentFile();

        if (!parentToDir.exists()) {
            if (!parentToDir.mkdirs())
                throw new FileTransferFailedException(getResourceMap().getString("creatingOutputFilePathFailed"));
        }

        if (from.renameTo(to)) {
            saveToHistoryList();
            saveDescriptionFiles();
            return null;
        }

        saveDescriptionFiles();

        FileChannel ic = null;
        FileChannel oc = null;

        try {
            try {
                ic = new FileInputStream(from).getChannel();
                oc = new FileOutputStream(to).getChannel();

                final long size = from.length();
                ByteBuffer buffer = ByteBuffer.allocate(BSIZE);
                int read;
                int counter = 0;
                int i = 0;
                while ((read = ic.read(buffer)) != -1) {
                    buffer.flip(); // Prepare for writing
                    oc.write(buffer);
                    buffer.clear(); // Prepare for reading
                    counter += read;
                    if (i++ % 32 == 0) {
                        setProgress(counter, 0, size);
                    }
                }
            } finally {
                try {
                    if (ic != null)
                        ic.close();
                } catch (IOException e) {
                    LogUtils.processException(logger, e);
                }
                try {
                    if (oc != null)
                        oc.close();
                } catch (IOException e) {
                    LogUtils.processException(logger, e);
                }
            }

            saveToHistoryList();
        }
        catch (Exception e) {
            if (to.exists())
                to.delete();
            deleteDescriptionFileOnError();
        }
        finally {
            if (deleteSource && from.exists()) // i v pripade cancelled a failed
                from.delete();
        }
        return null;
    }

    private void deleteDescriptionFileOnError() {
        final String desc = downloadFile.getDescription();
        if (desc == null || desc.isEmpty())
            return;
        final boolean descriptionFile = AppPrefs.getProperty(UserProp.GENERATE_DESCRIPTION_BY_FILENAME, UserProp.GENERATE_DESCRIPTION_BY_FILENAME_DEFAULT);
        if (descriptionFile) {
            final File descTxtFile = new File(to.getParentFile(), getNameForFile(Utils.getPureFilenameWithDots(to) + ".txt"));
            if (descTxtFile.exists())
                descTxtFile.delete();
        }
    }

    private void saveDescriptionFiles() {
        String desc = downloadFile.getDescription();
        if (desc == null || desc.isEmpty())
            return;
        final boolean descIon = AppPrefs.getProperty(UserProp.GENERATE_DESCRIPT_ION_FILE, UserProp.GENERATE_DESCRIPT_ION_FILE_DEFAULT);
        final boolean trim = AppPrefs.getProperty(UserProp.TRIM_DESCRIPTION_FOR_FILES, UserProp.TRIM_DESCRIPTION_FOR_FILES_DEFAULT);
        if (trim)
            desc = desc.trim();

        if (descIon) {
            final File descriptIonFile = new File(to.getParentFile(), getNameForFile("descript.ion"));
            FileWriter writer = null;
            try {
                writer = new FileWriter(descriptIonFile, true);
                if (descriptIonFile.length() > 0)
                    writer.write(Utils.getSystemLineSeparator());
                writer.write(to.getName() + " " + desc.trim());
            } catch (IOException e) {
                LogUtils.processException(logger, e);
            } finally {
                if (writer != null) {
                    if (generateFileAsHidden() && Utils.isWindows())
                        Utils.setFileAsHidden(descriptIonFile);
                    try {
                        writer.close();
                    } catch (IOException e) {
                        LogUtils.processException(logger, e);
                    }
                }
            }
        }

        final boolean descriptionFile = AppPrefs.getProperty(UserProp.GENERATE_DESCRIPTION_BY_FILENAME, UserProp.GENERATE_DESCRIPTION_BY_FILENAME_DEFAULT);
        if (descriptionFile) {
            File descTxtFile = new File(to.getParentFile(), getNameForFile(Utils.getPureFilenameWithDots(to) + ".txt"));
            if (descTxtFile.exists() && descTxtFile.isHidden()) { //na Windows FileWriter haze vyjimku, pokud je hidden
                if (!descTxtFile.delete()) { //txt vztazeny k souboru muzeme klidne smazat, protoze vytvorime novy
                    logger.warning("Deletion of file " + descTxtFile.getAbsolutePath() + " failed");
                }
            }
            if (descTxtFile.exists()) {
                descTxtFile = new File(to.getParentFile(), getNameForFile(Utils.getPureFilenameWithDots(to) + "-description.txt"));
            }
            if (descTxtFile.exists()) {
                descTxtFile = getNewUniqueFileName(descTxtFile);
            }
            FileWriter writer = null;
            try {
                writer = new FileWriter(descTxtFile, false);
                writer.write(desc);
            } catch (IOException e) {
                LogUtils.processException(logger, e);
            } finally {
                if (writer != null) {
                    if (generateFileAsHidden() && Utils.isWindows())
                        Utils.setFileAsHidden(descTxtFile);
                    try {
                        writer.close();
                    } catch (IOException e) {
                        LogUtils.processException(logger, e);
                    }
                }
            }
        }
    }

    private String getNameForFile(String st) {
        if (generateFileAsHidden()) {
            if (!Utils.isWindows() && !st.startsWith(".")) {
                return "." + st;
            }
        }
        return st;
    }

    private boolean generateFileAsHidden() {
        return AppPrefs.getProperty(UserProp.GENERATE_DESCRIPTION_FILES_HIDDEN, UserProp.GENERATE_DESCRIPTION_FILES_HIDDEN_DEFAULT);
    }

    private void saveToHistoryList() {
        downloadFile.setFileName(to.getName());
        ((MainApp) getApplication()).getManagerDirector().getFileHistoryManager().addHistoryItem(downloadFile, to);
    }

    @Override
    protected void failed(Throwable cause) {
        super.failed(cause);
//        Swinger.showErrorMessage(getResourceMap(), "transferFailed", cause.getMessage());
    }

    private File getNewUniqueFileName(final File to) {
        final File dir = to.getParentFile();
        final String pureFileName = Utils.getPureFilenameWithDots(to);
        String ext = Utils.getExtension(to);
        ext = (ext != null) ? ("." + ext) : "";
        File newFile;
        int counter = 2;
        while ((newFile = new File(dir, pureFileName + "-" + String.valueOf(counter) + ext)).exists()) {
            ++counter;
        }
        return newFile;
    }

}
