package cz.vity.freerapid.swing;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.FWProp;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Nacita definovany look&feel
 *
 * @author Vity
 */
public final class LookAndFeels {
    private final static String DEFAULT_LAF = "com.jtattoo.plaf.aero.AeroLookAndFeel";
    private final static String DEFAULT_THEME = "";
    /**
     * Trida Kunstoffu
     */
    private static final String KUNSTSTOFF = "com.incors.plaf.kunststoff.KunststoffLookAndFeel";

    /**
     * aktualne zvoleny lookandfeel
     */
    private LaF selectedLookAndFeel;
    /**
     * instance loggeru
     */
    private final static Logger logger = Logger.getLogger(LookAndFeels.class.getName());
    /**
     * Instance teto tridy
     */
    private static final LookAndFeels instance = new LookAndFeels();

    /**
     * default MacOS LaF
     */
    private static final String AQUA = "ch.randelshofer.quaqua.QuaquaLookAndFeel";

    /**
     * Classloader pro look and feely
     */
    private ClassLoader classLoader = null;

    /**
     * Seznam nactenych (dostupnych) lookandfeelu
     */
    private java.util.List<LaF> availableLaFs = null;


    /**
     * Vraci instanci tridy LookAndFeel (singleton)
     *
     * @return Instance tridy LookAndFeel
     */
    public static LookAndFeels getInstance() {
        return instance;
    }

    /**
     * Privatni konstruktor - je ho mozne volat pouze z teto tridy
     */
    private LookAndFeels() {
        classLoader = initClassLoader();

        String selectedLookAndFeelClassName = AppPrefs.getProperty(FWProp.LOOK_AND_FEEL_SELECTED_KEY, null);

        String selectedTheme = AppPrefs.getProperty(FWProp.THEME_SELECTED_KEY, DEFAULT_THEME);

        if (selectedLookAndFeelClassName == null) {
            final String value = Swinger.getResourceMap().getString("Application.lookAndFeelDefault");
            final String s = (value == null) ? "" : value.trim();
            selectedTheme = null;
            if ("system".equals(s)) {
                selectedLookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
            } else if (!"default".equals(s) && s != null && !s.isEmpty()) {
                selectedLookAndFeelClassName = s;
            } else
                selectedLookAndFeelClassName = org.jdesktop.swingx.util.OS.isMacOSX() ? AQUA : DEFAULT_LAF;
        }

        final boolean opaque = AppPrefs.getProperty(FWProp.LOOK_AND_FEEL_OPAQUE_KEY, true);

        if (selectedTheme == null && KUNSTSTOFF.equals(selectedLookAndFeelClassName))
            selectedTheme = KunstoffMetalTheme.class.getName();
        if (selectedTheme == null)
            selectedTheme = "";
        selectedLookAndFeel = new LaF(selectedLookAndFeelClassName, "", selectedTheme, opaque);
    }

    /**
     * Inicializuje classloader pro lookandfeely
     *
     * @return classloader pro lookandfeel
     */
    private ClassLoader initClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();//podiva se na aktualni vlakno a vraci jeho classloader
        if (Utils.getAppPath().isEmpty()) {//webstart
            return classLoader;
        }
        final String path = Utils.addFileSeparator(Utils.getAppPath()) + Consts.LAFSDIR;
        logger.info("Loading lookandfeel path " + path);
        final File file = new File(path);

        try {
            if (file.exists()) {
                final File[] jars = file.listFiles(new FilenameFilter() {//vylistuje vsechny soubory, ktere konci na .jar

                    public boolean accept(final File dir, final String name) {
                        return name.endsWith(".jar");
                    }
                });
                final int jarsCount = jars.length; //pocet jaru
                final URL[] urls = new URL[jarsCount]; //vytvori pole URL
                final boolean isDebug = logger.isLoggable(Level.INFO);
                for (int i = 0; i < jarsCount; ++i) {
                    urls[i] = jars[i].toURI().toURL();
                    if (isDebug)
                        logger.info("Loading URL with a jar " + urls[i]);
                }
                classLoader = new URLClassLoader(urls, classLoader);
            }
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
        return classLoader;
    }

    /**
     * Vraci seznam vsech dostupnych lookfeelu v aplikaci - parsovani lookandfeels.properties
     *
     * @return seznam
     */
    public final java.util.List<LaF> getAvailableLookAndFeels() {
        if (availableLaFs == null) {
            availableLaFs = new ArrayList<LaF>();
            final Properties properties = Utils.loadProperties(Consts.LAFSDIRFILE, true);
            final String namePostfix = ".name", themePostfix = ".theme", opaquePostfix = ".opaque", alonePostfix = ".alone";
            final String[] lafs = properties.getProperty("lafs", "").split("\\|");
            String className, theme, nameLaF, themeCode, themeName;
            boolean opaque;
            for (String lafID : lafs) {
                className = properties.getProperty(lafID + ".class");
                if (className != null && isPresent(className) != null) {
                    opaque = "true".equals(properties.getProperty(lafID + opaquePostfix, "true"));
                    int themeCounter = -1;
                    nameLaF = properties.getProperty(lafID + namePostfix, className);
                    if ("true".equals(properties.getProperty(lafID + alonePostfix, "true")))
                        availableLaFs.add(new LaF(className, nameLaF, "", opaque));
                    while (!(theme = properties.getProperty(themeCode = (lafID + themePostfix + ++themeCounter), "")).isEmpty()) {
                        if (isPresent(theme) != null) {
                            themeName = nameLaF + " - " + properties.getProperty(themeCode + namePostfix, " - theme");
                            availableLaFs.add(new LaF(className, themeName, theme, opaque));
                        }
                    }
                }
            }
        }
        //final String crossSystem = UIManager.getSystemLookAndFeelClassName();
        //availableLaFs.add(new LaF(crossSystem, "System", "", false));
        return availableLaFs;
    }

    /**
     * Vraci aktualne vybrany lookandfeel
     *
     * @return zvoleny lookandfeel
     */
    public final LaF getSelectedLaF() {
        return selectedLookAndFeel;
    }

    /**
     * Uklada do uzivatelskych properties aktualne zvoleny lookandfeel
     *
     * @param laf
     */
    public final void storeSelectedLaF(final LaF laf) {
        AppPrefs.storeProperty(FWProp.LOOK_AND_FEEL_SELECTED_KEY, laf.getClassName());
        AppPrefs.storeProperty(FWProp.LOOK_AND_FEEL_OPAQUE_KEY, laf.isToolbarOpaque());
        if (!laf.hasThemeClass()) {
            AppPrefs.removeProperty(FWProp.THEME_SELECTED_KEY);
        } else
            AppPrefs.storeProperty(FWProp.THEME_SELECTED_KEY, laf.getThemeClass());
        //  selectedLookAndFeel = laf;
        selectedLookAndFeel = laf;
        logger.config("LaF " + laf + " has been set. It will be effective on restart.");
    }

    /**
     * Z uzivatelskych nastaveni nacte aktualne zvoleny lookandfeel a nacte ho do aplikace jako aktualni
     */
    public final void loadLookAndFeelSettings() {
        loadLookAndFeel(selectedLookAndFeel, false);
    }

    /**
     * Testuje zda je dana trida dostupna v classloaderu
     *
     * @param className trida k otestovani
     * @return vraci instanci tridy, pokud existuje, pokud neexistuje vraci null
     */
    private Class<?> isPresent(final String className) {
        try {
            return classLoader.loadClass(className);
        } catch (UnsupportedClassVersionError ex) {
            logger.info("Look and feel class/theme " + className + " cannot be instantied. Probably higher version of the JDK is required.");
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Provadi vlastni nacitani lookandfeelu v aplikace (pro zobrazeni)
     *
     * @param laf        definice lookandfeelu
     * @param updateTree true - updatne komponentovy strom
     * @return vraci true, pokud nacteni probehlo v poradku, jinak false
     */
    public final boolean loadLookAndFeel(final LaF laf, final boolean updateTree) {
        final String lookAndFeelClassName = laf.getClassName();
        final String themeClassName = laf.getThemeClass();
        try {
            Thread.currentThread().setContextClassLoader(classLoader); //nastavi aktualni classloader vlakna na classloader lookandfeelu
            if (lookAndFeelClassName.equals(KUNSTSTOFF))
                initKunststoff();
            else
                initLaf(lookAndFeelClassName, themeClassName);
        } catch (Exception e) {
            logger.warning(lookAndFeelClassName + " was not found. Reason: " + e.getMessage());
            if (!lookAndFeelClassName.equals(KUNSTSTOFF)) { //nastala chyba, jeste zkusime nacist Kunstoff
                if (logger.isLoggable(Level.INFO))
                    LogUtils.processException(logger, e);
                try {
                    initKunststoff();
                } catch (Exception ex) {
                    logger.warning("Kunststoff Look and Feel was not found. Using a default metal theme.");
                    return false;
                }
            }
            return false;
        } finally {
            if (updateTree) {
                final JFrame mainFrame = MainApp.getInstance(MainApp.class).getMainFrame();
                updateAllUIs();
                UIManager.getLookAndFeel().getDefaults();
                //MainApp.getInstance().getMainAppFrame().pack();
                mainFrame.invalidate();
                mainFrame.validate();
                mainFrame.repaint();

                //  UIManager.getLookAndFeel().getDefaults();
            }
        }
        return true;
    }

    /**
     * Provede inicializaci Kunstoffu LaF
     *
     * @throws Exception chyba v pripade natahovani LaF
     */
    private void initKunststoff() throws Exception {
        initLafWithTheme(KUNSTSTOFF, new KunstoffMetalTheme());
    }

    @SuppressWarnings({"RedundantArrayCreation"})
    /**
     *
     */
    private void initLafWithTheme(final String lookAndFeelClassName, final MetalTheme metalTheme) throws Exception {
        final LookAndFeel laf = (LookAndFeel) classLoader.loadClass(lookAndFeelClassName).newInstance(); //javovska trida LookAndFeel

        if (metalTheme != null && laf instanceof MetalLookAndFeel) {
            laf.getClass().getMethod("setCurrentTheme", new Class[]{MetalTheme.class}).invoke(laf, new Object[]{metalTheme});//dynamicke volani metody setCurrentTheme
        }

        UIManager.put("ClassLoader", classLoader); //nastavi aktualni classloader UIManagera na nas vytvoreny classloader
        UIManager.setLookAndFeel(laf); //nastavi globalni vzhled
        UIManager.put("ClassLoader", classLoader);

        final Font font = (Font) UIManager.get("TitledBorder.font"); //
        if (font != null) {
            UIManager.put("TitledBorder.font", font.deriveFont(Font.BOLD));
        }
    }

    /**
     * Inicializuje LaF z konkretnich nazvu trid
     *
     * @param lookAndFeelClassName trida lookandfeelu
     * @param themeClassName       theme lookandfeelu
     * @throws Exception vyjimka, pokud nastala chyba pri vytvareni instance trid themeClassName nebo lookAndFeelClassName
     */
    private void initLaf(final String lookAndFeelClassName, final String themeClassName) throws Exception {
        if (themeClassName != null && !themeClassName.isEmpty()) {
            final Class<?> themeClass = isPresent(themeClassName);
            if (themeClass != null) {
                final Object instanceTheme = themeClass.newInstance();
                if (instanceTheme instanceof MetalTheme)
                    initLafWithTheme(lookAndFeelClassName, (MetalTheme) instanceTheme);
                else {
                    logger.warning("Theme " + instanceTheme.getClass().getName() + " cannot be set.Theme is not an instance of Metaltheme");
                    initLafWithTheme(lookAndFeelClassName, null);
                }
            }
        } else
            initLafWithTheme(lookAndFeelClassName, null);
    }

    /**
     * Method to attempt a dynamic update for any GUI accessible by this JVM. It will filter through all frames and
     * sub-components of the frames.
     */
    private static void updateAllUIs() {
        final Frame[] frames = Frame.getFrames();
        for (Frame frame : frames) {
            updateWindowUI(frame);
        }
    }

    /**
     * Method to attempt a dynamic update for all components of the given <code>Window</code>.
     *
     * @param window The <code>Window</code> for which the look and feel update has to be performed against.
     */
    public static void updateWindowUI(final Window window) {
        try {
            updateComponentTreeUI(window);
        } catch (Exception exception) {
            //empty
        }

        final Window windows[] = window.getOwnedWindows();

        for (Window window1 : windows) updateWindowUI(window1);
    }

    /**
     * A simple minded look and feel change: ask each node in the tree to <code>updateUI()</code> -- that is, to
     * initialize its UI property with the current look and feel.
     * <p/>
     * Based on the Sun SwingUtilities.updateComponentTreeUI, but ensures that the update happens on the components of a
     * JToolbar before the JToolbar itself.
     */
    public static void updateComponentTreeUI(final Component c) {
        updateComponentTreeUI0(c);
        c.invalidate();
        c.validate();
        c.repaint();
    }

    /**
     * Update komponentove stromu
     *
     * @param c komponenta k prekresleni
     */
    private static void updateComponentTreeUI0(final Component c) {

        Component[] children = null;

        if (c instanceof JToolBar) {
            children = ((JToolBar) c).getComponents();

            if (children != null) {
                final boolean opaque = LookAndFeels.getInstance().getSelectedLaF().isToolbarOpaque();
                for (Component aChildren : children) {
                    updateComponentTreeUI0(aChildren);
                    if (aChildren instanceof JComponent)
                        ((JComponent) aChildren).setOpaque(!opaque);
                }
            }

            ((JComponent) c).updateUI();
        } else {
            if (c instanceof JComponent) {
                ((JComponent) c).updateUI();
            }

            if (c instanceof JMenu) {
                children = ((JMenu) c).getMenuComponents();
            } else if (c instanceof Container) {
                children = ((Container) c).getComponents();
            }

            if (children != null) {
                for (Component aChildren : children) {
                    updateComponentTreeUI0(aChildren);
                }
            }
        }
    }

}