package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.LocalStorage;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class LinksAddedTask extends CoreTask<Void, Void> {
    private final List<DownloadFile> list;
    private final static String ALL_LINKS_FILENAME = "links.txt";
    private final static Logger logger = Logger.getLogger(LinksAddedTask.class.getName());


    public LinksAddedTask(ApplicationContext context, List<DownloadFile> list) {
        super(context.getApplication());
        this.list = list;
        message("");
        this.setUserCanCancel(false);
        this.setDescription("");
        this.setMessage("");
    }


    protected Void doInBackground() throws Exception {
        final String s = getStringRepresentation();
        final byte[] bytes = s.getBytes();
        if (bytes.length <= 0)
            return null;
        OutputStream os = null;

        final LocalStorage ls = getContext().getLocalStorage();

        File dir = ls.getDirectory();
        if (!dir.isDirectory()) {
            if (!dir.mkdirs()) {
                return null;
            }
        }
        File path = new File(dir, ALL_LINKS_FILENAME);
        try {
            os = new BufferedOutputStream(new FileOutputStream(path, true));
            os.write(bytes);
        } catch (IOException e) {
            LogUtils.processException(logger, e);
        } finally {
            if (os != null)
                os.close();
        }
        return null;
    }

    public String getStringRepresentation() {
        final StringBuilder builder = new StringBuilder();
        final String time = new SimpleDateFormat("yyyy-MM-dd HH:mm ").format(Calendar.getInstance().getTime());
        final String lineSeparator = Utils.getSystemLineSeparator();
        for (DownloadFile downloadFile : list) {
            final String desc = downloadFile.getDescription();
            if (desc != null && !downloadFile.getDescription().isEmpty()) {
                builder.append(" [").append(desc).append("]");
            }
            builder.append(time).append(downloadFile.getFileUrl().toExternalForm());
            builder.append(lineSeparator);
        }
        return builder.toString();
    }
}
