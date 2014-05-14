package cz.vity.freerapid.core;

/**
 * Application Framework properties
 *
 * @author Vity
 */
final public class FWProp {
    public static final String MINIMIZE_TO_TRAY = "minimizeToTray";
    public static final String SHOW_TRAY = "showTray";
    public static final String USER_SETTINGS_SELECTED_CARD = "userSettingsSelectedCard";
    public static final String LOOK_AND_FEEL_SELECTED_KEY = "lookAndFeel";
    public static final String LOOK_AND_FEEL_OPAQUE_KEY = "lafOpaque";
    public static final String THEME_SELECTED_KEY = "theme";
    public static final String ONEINSTANCE = "oneinstance";
    public static final String IMPORT_LAST_USED_FOLDER = "importLastUsedFolder";
    public static final String PROXY_USE = "proxy";
    public static final String PROXY_URL = "proxy.url";
    public static final String PROXY_SAVEPASSWORD = "proxy.savepassword";
    public static final String PROXY_PORT = "proxy.port";
    public static final String PROXY_LOGIN = "proxy.login";
    public static final String PROXY_USERNAME = "proxy.username";
    public static final String PROXY_PASSWORD = "proxy.password";
    public static final String SUBMIT_ERROR_EMAIL = "submitError.email";
    public static final String SUBMIT_ERROR_NAME = "submitError.name";
    public static final String MAX_RECENT_FILES = "maxRecentFiles";
    public static final String FRAME_TITLE = "frameTitleStyle";
    public static final String FRAME_TITLE_TYPE = "frameTitleType";
    public static final String CHECK_FOR_NEW_VERSION_URL = "checkForNewVersionURL";
    public static final String WEBURL = "weburl";
    public static final String ONE_INSTANCE_SERVER_PORT = "oneInstanceServerPort";
    public static final String NEW_VERSION = "checkForNewVersionAuto";
    public static final String WEBURL_SUBMIT_ERROR = "weburlSubmitError";
    public static final String DECORATED_FRAMES = "decoratedFrames";
    /**
     * A better name for this property would be CLOSE_TO_TRAY
     */
    public static final String MINIMIZE_ON_CLOSE = "minimizeOnClose";
    public static final boolean MINIMIZE_ON_CLOSE_DEFAULT = false;
    public static final String SELECTED_LANGUAGE = "selLanguage";
    public static final String DEFAULT_LANG_CODE = "EN";
    public static final boolean ONE_INSTANCE_DEFAULT = true;
    public static final String SELECTED_COUNTRY = "country";

    private FWProp() {
    }
}
