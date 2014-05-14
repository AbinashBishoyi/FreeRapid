package cz.vity.freerapid.gui.managers.search;

import jlibs.xml.sax.binding.*;
import org.xml.sax.SAXException;

/**
 * @author Vity
 */
@SuppressWarnings({"UnusedDeclaration"})
@NamespaceContext({
    @Entry(prefix = "", uri = "http://a9.com/-/spec/opensearch/1.1/")
        //@Entry(prefix="", uri="")
        })

@Binding("OpenSearchDescription")
public class OpenSearchDescriptionBinding {

    private static boolean settingUrl;
    private static boolean image16x16;

    @Binding.Start()
    public static OpenSearchDescription onStart() throws SAXException {
        settingUrl = false;
        image16x16 = false;
        return new OpenSearchDescription();
    }

    @Binding.Text({"ShortName", "Description", "Image", "InputEncoding", "SearchForm"})
    public static String onText(String text) {
        return text;
    }

    @Relation.Finish("ShortName")
    public static void relateShortName(OpenSearchDescription openSearchDescription, String shortName) {
        openSearchDescription.setShortName(shortName);
    }

    @Relation.Finish("SearchForm")
    public static void relateSearchForm(OpenSearchDescription openSearchDescription, String searchForm) {
        openSearchDescription.setSearchForm(searchForm);
    }

    @Relation.Finish("Description")
    public static void relateDescription(OpenSearchDescription openSearchDescription, String description) {
        openSearchDescription.setDescription(description);
    }

    @Relation.Finish("Image")
    public static void relateImage(OpenSearchDescription openSearchDescription, String image) {
        if (image16x16) {
            openSearchDescription.setImage(image);
        }
        image16x16 = false;
    }

    @Relation.Finish("InputEncoding")
    public static void relateInputEncoding(OpenSearchDescription openSearchDescription, String inputEncoding) {
        openSearchDescription.setInputEncoding(inputEncoding);
    }

    @Binding.Start("Image")
    public static void onImage(OpenSearchDescription openSearchDescription, @Attr String width, @Attr String height) {
        image16x16 = "16".equals(width) && "16".equals(height);
    }


    @Binding.Start("Url")
    public static void onUrl(OpenSearchDescription openSearchDescription, @Attr String type, @Attr String template, @Attr String method) {
        if ("text/html".equalsIgnoreCase(type)) {
            openSearchDescription.setUrlMethod(method);
            openSearchDescription.setUrlTemplate(template);
            openSearchDescription.getUrlParams().clear();
            settingUrl = true;
        } else settingUrl = false;
    }


    @Binding.Start("Url/Param")
    public static void onParam(OpenSearchDescription openSearchDescription, @Attr String name, @Attr String value) {
        if (settingUrl)
            openSearchDescription.getUrlParams().put(name, value);
    }
}
