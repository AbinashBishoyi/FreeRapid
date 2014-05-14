package cz.vity.freerapid.gui.managers.search;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vity
 */
public class OpenSearchDescription {
    private String shortName;
    private String description;
    private String image;
    private String inputEncoding;
    private String urlTemplate;
    private String urlMethod;
    private String searchForm;
    private Map<String, String> urlParams = new HashMap<String, String>(0);

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInputEncoding() {
        return inputEncoding == null ? "UTF-8" : inputEncoding;
    }

    public void setInputEncoding(String inputEncoding) {
        this.inputEncoding = inputEncoding;
    }

    public String getUrlTemplate() {
        return urlTemplate;
    }

    public void setUrlTemplate(String urlTemplate) {
        this.urlTemplate = urlTemplate;
    }

    public String getUrlMethod() {
        return urlMethod;
    }

    public void setUrlMethod(String urlMethod) {
        this.urlMethod = urlMethod;
    }

    public Map<String, String> getUrlParams() {
        return urlParams;
    }

    public void setUrlParams(Map<String, String> urlParams) {
        this.urlParams = urlParams;
    }

    public String getSearchForm() {
        return searchForm;
    }

    public void setSearchForm(String searchForm) {
        this.searchForm = searchForm;
    }

    @Override
    public String toString() {
        return "OpenSearchDescription{" +
                "shortName='" + shortName + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", inputEncoding='" + inputEncoding + '\'' +
                ", urlTemplate='" + urlTemplate + '\'' +
                ", urlMethod='" + urlMethod + '\'' +
                ", urlParams=" + urlParams +
                '}';
    }
}
