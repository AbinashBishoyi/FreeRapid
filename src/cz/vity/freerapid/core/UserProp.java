package cz.vity.freerapid.core;

/**
 * @author Vity
 */
public class UserProp {
    public static final String LAST_IMPORT_FILTER = "lastUsedImportFilter";
    public static final String IMPORT_LAST_USED_FOLDER = "importLastUsedFolder";

    public static final String LAST_EXPORT_FILENAME = "lastExportFilename";
    public static final String LAST_EXPORT_FILTER = "lastExportFilter";
    public static final String LAST_USED_FOLDER_EXPORT = "lastUsedFolderExport";

    public static final String USE_TEMPORARY_FILES = "useTemporaryFiles";

    public static final String DOWNLOAD_ON_START = "downloadOnStart";
    public static final String FILE_ALREADY_EXISTS = "whatToDoIfFileAlreadyExists";

    public static final int RENAME = 0;
    public static final int OVERWRITE = 1;
    public static final int SKIP = 2;
    public static final int ASK = 3;


    public static final String PLAY_SOUNDS = "playSound";
    public static final String LAST_USED_SAVED_PATH = "lastUsedSavedPaths";
    public static final String MAX_DOWNLOADS_AT_A_TIME = "maxDownloadsAtATime";

    public static final String AUTO_RECONNECT_TIME = "autoReconnectTime";
    public static final int AUTO_RECONNECT_TIME_DEFAULT = 120;

    public static final String ERROR_ATTEMPTS_COUNT = "errorAttemptsCount";
    public static final int ERROR_ATTEMPTS_COUNT_DEFAULT = 5;
    public static int MAX_DOWNLOADS_AT_A_TIME_DEFAULT = 5;
    public static final String PROXY_LIST_FILE = "proxyListFile";
}
