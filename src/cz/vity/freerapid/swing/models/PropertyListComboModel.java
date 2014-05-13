package cz.vity.freerapid.swing.models;

import javax.swing.*;

/**
 * Model podporujici '-' jako separator mezi polozkami Item '-' nelze vybrat.
 *
 * @author Vity
 */
public class PropertyListComboModel extends DefaultComboBoxModel {
    public PropertyListComboModel(String property) {
        super();

    }

    public PropertyListComboModel(Object items[]) {
        super(items);
    }

    public void setSelectedItem(Object o) {
        //Object currentItem = getSelectedItem();
        if (!"-".equals(o)) {
            super.setSelectedItem(o);
        }
    }
}