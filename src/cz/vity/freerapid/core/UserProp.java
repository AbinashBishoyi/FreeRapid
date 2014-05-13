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
}
