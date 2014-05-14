package cz.vity.freerapid.core.application;

import org.jdesktop.application.ResourceConverter;
import org.jdesktop.application.ResourceMap;

/**
 * Konvertor k ziskani pole stringu z hodnoty resourcemapy
 *
 * @author Vity
 */
public final class ListItemsConvertor extends ResourceConverter {
    public ListItemsConvertor() {
        super(String[].class);
    }

    @Override
    public Object parseString(String s, ResourceMap r) throws ResourceConverterException {
        return s.split("\\|");
    }
}
