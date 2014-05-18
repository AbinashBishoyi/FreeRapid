package cz.vity.freerapid.core;

/**
 * Trida pro ukladani globalnich konstant v aplikaci
 *
 * @author Vity
 */
public class Consts {

    /**
     * Product name
     */
    public static final String PRODUCT = "FreeRapid";

    /**
     * Version
     */
    public static final String VERSION = "0.9u4";

    /**
     * verze a jmeno programu
     */
    public static final String APPVERSION = PRODUCT + " " + VERSION;


    /**
     * cesta k souboru pro nastaveni logovani - debug
     */
    public static final String LOGDEBUG = "logdebug.properties";

    /**
     * cesta k souboru pro nastaveni logovani - default info
     */
    public static final String LOGDEFAULT = "logdefault.properties";

    /**
     * jmeno adresare v resources, kde je sound
     */
    public static final String SOUNDS_DIR = "sound";
    /**
     * cesta k adresari s look&feely
     */
    public static final String LAFSDIR = "lookandfeel";


    public static final String PLUGINS_DIR = "plugins";

    public static final String PLUGINS_DIST_FILE_NAME = "instplgs.dat";

    public static final String PLUGINS_VERSION_FILE_NAME = "version.dat";

    /**
     * cesta k properties fajlu
     */
    public static final String LAFSDIRFILE = "lookandfeels.properties";
    /**
     * port na kterem bezi aplikace, aby se zamezilo dvojimu spousteni - kvuli konexeni k databazi
     */
    public static final int ONE_INSTANCE_SERVER_PORT = 39871;

    /**
     * URL adresa, kam se posilaji reporty o chybach
     */
    public static final String WEBURL_SUBMIT_ERROR = "http://wordrider.net/posterror.php";

    //public static final int DEFAULT_RECENT_FILES_MAX_COUNT = 7;

    public static final String WEBURL_CHECKNEWVERSION = "http://wordrider.net/checkfrd.php";

    public static final String WEBURL = "http://wordrider.net/freerapid";
    public static final String FORUM_URL = "http://wordrider.net/forum/index/9";

    public static final String DEMO_WEBURL = "http://wordrider.net/freerapid/demo";
    public static final String HELP_WEBURL = "http://wordrider.net/freerapid/help";

    // hodnoty pro defaultni hodnoty pripojeni

    public static final String APP_CODE = "frd";
    public static final String AUTHORS = "DevTeam (c) 2008-2014";

    public static final String LINUX_SHELL_SCRIPT = APP_CODE + ".sh";
    public static final String LINUX_ICON_NAME = APP_CODE + ".png";
    public static final String WINDOWS_ICON_NAME = APP_CODE + ".ico";
    public static final String WINDOWS_EXE_NAME = APP_CODE + ".exe";

    /**
     * Soubor pod kterym jsou polozky ulozeny
     */
    static final String DEFAULT_PROPERTIES = "frd.xml";

    public static final String PLUGIN_CHECK_UPDATE_URL = "http://wordrider.net/freerapid/pluginsCheck.php";
    /**
     * version of API
     */
    public static final String APIVERSION = "0.855";
    public static final String PLUGINSSTATUS_URL = "http://wordrider.net/freerapid/plugins.html";
    public static final String SEARCH_ENGINES_URL = "https://addons.mozilla.org/en-US/firefox/search-engines/";

    /**
     * Od teto tridy se nebudou delat zadne instance
     */
    private Consts() {
    }
}
