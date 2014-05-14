package cz.vity.freerapid.core;

import jlibs.xml.sax.binding.Attr;
import jlibs.xml.sax.binding.Binding;
import org.xml.sax.SAXException;

import java.util.LinkedList;
import java.util.List;

/**
 * WARNING - if you get class not found error for LanguageBindingImpl, you  have to enable annotation compiler support in your IDE.
 * For intellij Idea: Settings-> Compiler->Annotation processors
 * @author Vity
 */

@SuppressWarnings({"UnusedDeclaration"})
@Binding("languages")
public class LanguageBinding {

    private static final String LANG_NONAME_ICON = "blank.gif";

    @Binding.Start()
    public static List<SupportedLanguage> onStart() throws SAXException {
        return new LinkedList<SupportedLanguage>();
    }

    @Binding.Start("lng")
    public static void onLng(List<SupportedLanguage> list, @Attr String name, @Attr String code, @Attr String flags, @Attr String country, @Attr String icon) {
        if (name == null) {
            throw new IllegalArgumentException("'name' attribute cannot be null");
        }
        if (code == null) {
            throw new IllegalArgumentException("'code' attribute cannot be null");
        }
        if (flags == null) {
            flags = "";
        }
        if (country == null) {
            country = "";
        }
        if (icon == null) {
            icon = LANG_NONAME_ICON;
        }
        SupportedLanguage language = new SupportedLanguage(code, name, icon, country);
        language.setFlags(flags);
        list.add(language);
    }


}
