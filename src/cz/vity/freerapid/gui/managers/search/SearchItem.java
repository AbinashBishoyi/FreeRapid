package cz.vity.freerapid.gui.managers.search;

import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.DataURI;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class SearchItem {
    private final String id;
    private OpenSearchDescription searchDescription;
    private ImageIcon image;
    private final static Logger logger = Logger.getLogger(SearchItem.class.getName());

    public SearchItem(String id, OpenSearchDescription searchDescription) {
        this.id = id;
        this.searchDescription = searchDescription;
    }

    public String getId() {
        return id;
    }

    public ImageIcon getImage() {
        if (image == null) {
            String imagePath = searchDescription.getImage();
            if (imagePath != null) {
                imagePath = imagePath.trim();
                if (imagePath.toLowerCase(Locale.ENGLISH).startsWith("http://")) {
                    try {
                        final URL url = new URL(imagePath);
                        image = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
                    } catch (Exception e) {
                        logger.warning("Invalid URL for search item image: " + imagePath + " " + e.getMessage());
                    }
                } else {
                    final DataURI data = DataURI.parse(imagePath);
                    if (data != null) {
                        image = new ImageIcon(data.toImage());
                    }
                }
            }
            if (image == null) {
                return image = getDefaultSearchIcon();
            }
        }
        return image;
    }

    private ImageIcon getDefaultSearchIcon() {
        return Swinger.getIconImage("searchDefaultIcon");
    }


    public OpenSearchDescription getSearchDescription() {
        return searchDescription;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchItem that = (SearchItem) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }
}
