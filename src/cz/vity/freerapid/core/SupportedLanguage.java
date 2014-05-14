package cz.vity.freerapid.core;

import java.text.Collator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Vity
 */
public final class SupportedLanguage implements Comparable<SupportedLanguage> {
    private final String languageCode;
    private final String name;
    private final String icon;
    private final String country;
    private final Map<String, String> flags = new HashMap<String, String>(2);

    public SupportedLanguage(final String languageCode, final String name, final String icon, final String country) {
        this.languageCode = languageCode;
        this.name = name;
        this.icon = icon;        
        this.country = country;
    }

    public SupportedLanguage(final String selLanguageCode, final String country) {
        this(selLanguageCode, "", null, country);
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


    public int compareTo(SupportedLanguage o) {
        return Collator.getInstance().compare(this.getName(), o.getName());
    }

    public final String getName() {
        return name;
    }

    public final String getIcon() {
        final String country = Locale.getDefault().getCountry();
        if (flags.containsKey(country))
            return flags.get(country);
        return icon;
    }


    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return getLanguageCode();
    }

    public void setFlags(String flags) {
        final String[] f = flags.split("\\|");
        for (String s : f) {
            if (!s.trim().isEmpty()) {
                final String[] country = s.split("=");
                for (int i = 0; i < country.length; i += 2) {
                    this.flags.put(country[i], country[i + 1]);
                }
            }
        }
    }
}
