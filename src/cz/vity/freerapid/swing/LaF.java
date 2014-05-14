package cz.vity.freerapid.swing;

/**
 * POJO nalezeneho LookAndFeelu
 *
 * @author Vity
 */
public final class LaF {
    private final String className;
    private final String name;
    private String themeClass;
    private boolean toolbarOpaque = true;

    /**
     * Konstruktor
     *
     * @param className     hlavni trida lookAndFeelu
     * @param name          jmeno lookandfeeelu
     * @param themeClass    jmeno tridy pro theme
     * @param toolbarOpaque zda ma byt toolbar pruhledny
     */
    public LaF(final String className, final String name, final String themeClass, final boolean toolbarOpaque) {
        if (themeClass == null)
            throw new IllegalArgumentException("Theme class cannot be null. Must have at least ''");
        this.className = className;
        this.name = name;
        this.themeClass = themeClass;
        this.toolbarOpaque = toolbarOpaque;
    }

    /**
     * Vraci jmeno tridy lookAndFeelu
     *
     * @return jmeno tridy
     */
    public String getClassName() {
        return className;
    }

    /**
     * Vraci jmeno lookAndFeelu
     *
     * @return jmeno lookAndFeelu
     */
    public final String getName() {
        return name;
    }

    /**
     * Vraci tridu pro theme lookandfeelu.
     *
     * @return Vraci null v pripade, ze neni zadny theme prirazen.
     */
    public final String getThemeClass() {
        return themeClass;
    }

    public boolean hasThemeClass() {
        return themeClass != null && !themeClass.isEmpty();
    }

    /**
     * Test zda na pruhlednost toolbaru pro tento lookAndFeel
     *
     * @return true v pripade, ze ma byt toolbar pruhledny, jinak false
     */
    public final boolean isToolbarOpaque() {
        return toolbarOpaque;
    }

    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LaF laF = (LaF) o;

        return className.equals(laF.className) && themeClass.equals(laF.themeClass);

    }

//    public final boolean equals(final Object obj) {
//        if (obj instanceof LaF)
//            return equals((LaF) obj);
//        return false;
//    }

    private boolean equals(final LaF obj) {
        return className.equals(obj.getClassName());
//            if (themeClass != null)
//                return themeClass.equals(obj.getThemeClass());
//            else
//                return (obj.getThemeClass() == null);
        //return false;
    }

    @Override
    public final String toString() {
        return this.getName();
    }

    /**
     * Nastavi aktualni theme pro tento lookAndFeel
     *
     * @param name jmeno tridy theme
     */
    public void setThemeClass(String name) {
        this.themeClass = name;
    }
}