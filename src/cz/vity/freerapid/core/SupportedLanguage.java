package cz.vity.freerapid.core;

/**
 * @author Vity
 */
public final class SupportedLanguage {
    private final String languageCode;
    private final String name;
    private final String icon;
    private final Integer mnemonic;

    public SupportedLanguage(final String languageCode, final String name, final String icon, final Integer mnemonic) {
        this.languageCode = languageCode;
        this.name = name;
        this.icon = icon;
        this.mnemonic = mnemonic;
    }

    public SupportedLanguage(String selLanguageCode) {
        this.languageCode = selLanguageCode;
        this.name = null;
        this.icon = null;
        this.mnemonic = null;
    }

    public final String getLanguageCode() {
        return languageCode;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SupportedLanguage that = (SupportedLanguage) o;

        return languageCode.equals(that.languageCode);

    }

    public int hashCode() {
        return languageCode.hashCode();
    }

    public final String getName() {
        return name;
    }

    public final String getIcon() {
        return icon;
    }

    public final Integer getMnemonic() {
        return mnemonic;
    }

    @Override
    public String toString() {
        return getLanguageCode();
    }
}
