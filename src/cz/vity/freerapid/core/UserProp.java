package cz.vity.freerapid.core;

/**
 * @author Vity
 */
final public class UserProp {

    private UserProp() {
    }

    public static final String LAST_IMPORT_FILTER = "lastUsedImportFilter";
    public static final String IMPORT_LAST_USED_FOLDER = "importLastUsedFolder";

    public static final String LAST_EXPORT_FILENAME = "lastExportFilename";
    public static final String LAST_EXPORT_FILTER = "lastExportFilter";
    public static final String LAST_USED_FOLDER_EXPORT = "lastUsedFolderExport";

    public static final String USE_TEMPORARY_FILES = "useTemporaryFiles";

    public static final String DOWNLOAD_ON_APPLICATION_START = "downloadOnStart";
    public static final boolean DOWNLOAD_ON_APPLICATION_START_DEFAULT = true;

    public static final String FILE_ALREADY_EXISTS = "whatToDoIfFileAlreadyExists";

    public static final int RENAME = 0;
    public static final int OVERWRITE = 1;
    public static final int SKIP = 2;
    public static final int ASK = 3;


    public static final String PLAY_SOUNDS_OK = "playSoundOK";//OK
    public static final String LAST_USED_SAVED_PATH = "lastUsedSavedPaths";

    public static final String LAST_COMBO_PATH = "lastComboPath";


    public static final String MAX_DOWNLOADS_AT_A_TIME = "maxDownloadsAtATime";

    public static final String AUTO_RECONNECT_TIME = "autoReconnectTime";
    public static final int AUTO_RECONNECT_TIME_DEFAULT = 120;

    public static final String ERROR_ATTEMPTS_COUNT = "errorAttemptsCount";
    public static final int ERROR_ATTEMPTS_COUNT_DEFAULT = 5;

    public static int MAX_DOWNLOADS_AT_A_TIME_DEFAULT = 5;

    public static final String DOWNLOADED_HISTORY_FILE_NAME = "downloads.txt";

    public static final String CONTAIN_DOWNLOADS_FILTER = "containDownloadsFilter";

    public static final String SELECTED_DOWNLOADS_FILTER = "selectedDownloadsFilter";

    public static final String SHOW_COMPLETED = "removeCompleted";

    public static final String USE_DEFAULT_CONNECTION = "useDefaultConnection";
    public static final boolean USE_DEFAULT_CONNECTION_DEFAULT = true;

    public static final String USE_SYSTEM_ICONS = "useSystemIcons";

    public static final String CLOSE_APPLICATION_CONFIRM_WAITTIME = "closeApplicationConfirmTime";

    public static final String CLOSE_WHEN_COMPLETED = "closeWhenCompleted";

    public static final String USE_PROXY_LIST = "useProxyList";

    public static final String PROXY_LIST_PATH = "proxyListPath";
    public static final String PLAY_SOUNDS_FAILED = "playSoundsFailed";
    public static final int FILE_ALREADY_EXISTS_DEFAULT = UserProp.ASK;

    public static final String ERROR_SLEEP_TIME = "firstSleepTime";
    public static final int ERROR_SLEEP_TIME_DEFAULT = 4;

    public static final String START_FROM_FROM_TOP = "startDownloadFromTheTop";
    public static final boolean START_FROM_FROM_TOP_DEFAULT = true;

    public static final String SHOWINFO_IN_TITLE = "showInfoInFrameTitle";
    public static final boolean SHOWINFO_IN_TITLE_DEFAULT = false;

    //ukladani fronty pro pripad padu programu atd.
    public static final String AUTOSAVE_ENABLED = "autosaveEnabled";
    public static final boolean AUTOSAVE_ENABLED_DEFAULT = true;

    public static final String AUTOSAVE_TIME = "autosaveTime";
    public static final int AUTOSAVE_TIME_DEFAULT = 10;//sekundy

    public static final String CLIPBOARD_MONITORING = "clipboardMonitoring";
    public static final boolean CLIPBOARD_MONITORING_DEFAULT = false;

    public static final String ANIMATE_ICON = "animateIcon";
    public static final boolean ANIMATE_ICON_DEFAULT = true;

    public static final String GENERATE_DESCRIPT_ION_FILE = "generateDescript-ionFile";
    public static final boolean GENERATE_DESCRIPT_ION_FILE_DEFAULT = false;

    public static final String GENERATE_DESCRIPTION_BY_FILENAME = "generateDescriptionByFileName";
    public static final boolean GENERATE_DESCRIPTION_BY_FILENAME_DEFAULT = false;

    public static final String GENERATE_DESCRIPTION_FILES_HIDDEN = "descriptionFilesHidden";
    public static final boolean GENERATE_DESCRIPTION_FILES_HIDDEN_DEFAULT = false;

    public static final String SHOW_GRID_HORIZONTAL = "showHorizontalGridLines";
    public static final boolean SHOW_GRID_HORIZONTAL_DEFAULT = false;

    public static final String SHOW_GRID_VERTICAL = "showVerticalGridLines";
    public static final boolean SHOW_GRID_VERTICAL_DEFAULT = false;

    public static final String ANTI_FRAGMENT_FILES = "preCreateFile";
    public static final boolean ANTI_FRAGMENT_FILES_DEFAULT = false;

    public static final String OUTPUT_FILE_BUFFER_SIZE = "outputFileBufferSize";
    public static final String INPUT_BUFFER_SIZE = "inputBufferSize";

    public static final String ACTIVATE_WHEN_CAPTCHA = "activateOnCaptcha";
    public static final boolean ACTIVATE_WHEN_CAPTCHA_DEFAULT = true;

    public static final String PAYPAL = "paypal";
    public static final String PAYPAL_DEFAULT = "http://wordrider.net/freerapid/paypal";
    public static final String DEMO_URL = "demoURL";

    public static final String BRING_TO_FRONT_WHEN_PASTED = "bringToFrontWhenPasted";
    public static final boolean BRING_TO_FRONT_WHEN_PASTED_DEFAULT = true;

    public static final String REMOVE_COMPLETED_DOWNLOADS = "removeCompletedDownloads";
    public static final int REMOVE_COMPLETED_DOWNLOADS_NEVER = 0;
    public static final int REMOVE_COMPLETED_DOWNLOADS_DEFAULT = REMOVE_COMPLETED_DOWNLOADS_NEVER;
    public static final int REMOVE_COMPLETED_DOWNLOADS_IMMEDIATELY = 1;
    public static final int REMOVE_COMPLETED_DOWNLOADS_AT_STARTUP = 2;

    public static final String BIG_ICON_IN_HISTORY = "historyBiggerLine";
    public static final boolean BIG_ICON_IN_HISTORY_DEFAULT = true;
}
