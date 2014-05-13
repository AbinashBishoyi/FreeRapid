package cz.vity.freerapid.core;

/**
 * @author Vity
 */
public final class SupportedLanguage {
    private final String languageCode;
    private final String name;
    private final String icon;
    private final String country;
    private final Integer mnemonic;

    public SupportedLanguage(final String languageCode, final String name, final String icon, final Integer mnemonic, final String country) {
        this.languageCode = languageCode;
        this.name = name;
        this.icon = icon;
        this.mnemonic = mnemonic;
        this.country = country;
    }

    public SupportedLanguage(final String selLanguageCode, final String country) {
        this(selLanguageCode, "", null, 0, country);
    }

    public final String getLanguageCode() {
        return languageCode;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SupportedLanguage that = (SupportedLanguage) o;

        return country.equalsIgnoreCase(that.country) && languageCode.equalsIgnoreCase(that.languageCode);

    }

    public int hashCode() {
        int result;
        result = languageCode.hashCode();
        result = 31 * result + country.hashCode();
        return result;
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

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return getLanguageCode();
    }
}
