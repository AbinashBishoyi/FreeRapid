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
    public static final String VERSION = "0.6";

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
    /**
     * cesta k properties fajlu
     */
    public static final String LAFSDIRFILE = "lookandfeels.properties";
    /**
     * port na kterem bezi aplikace, aby se zamezilo dvojimu spousteni - kvuli konexeni k databazi
     */
    public static final int ONE_INSTANCE_SERVER_PORT = 28871;

    /**
     * URL adresa, kam se posilaji reporty o chybach
     */
    public static final String WEBURL_SUBMIT_ERROR = "http://wordrider.net/posterror.php";

    public static final int DEFAULT_RECENT_FILES_MAX_COUNT = 7;

    public static final String WEBURL_CHECKNEWVERSION = "http://wordrider.net/checkfrd.php";

    public static final String WEBURL = "http://wordrider.net/freerapid";

    // hodnoty pro defaultni hodnoty pripojeni

    protected static final String APP_CODE = "frd";
    public static final String AUTHORS = "Authors (c) 2008: Vity";
    /**
     * Soubor pod kterym jsou polozky ulozeny
     */
    static final String DEFAULT_PROPERTIES = "frd.xml";

    public static final int MAX_RECENT_PHRASES_COUNT = 5;

    /**
     * Od teto tridy se nebudou delat zadne instance
     */
    private Consts() {
    }
}
