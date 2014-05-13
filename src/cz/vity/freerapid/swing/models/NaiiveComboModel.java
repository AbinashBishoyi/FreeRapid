package cz.vity.freerapid.swing.models;

import javax.swing.*;

/**
 * Model podporujici '-' jako separator mezi polozkami Item '-' nelze vybrat.
 *
 * @author Vity
 */
public class NaiiveComboModel extends DefaultComboBoxModel {
    public NaiiveComboModel() {
        super();
    }

    public NaiiveComboModel(Object items[]) {
        super(items);
    }

    public void setSelectedItem(Object o) {
        //Object currentItem = getSelectedItem();
        if (!"-".equals(o)) {
            super.setSelectedItem(o);
        }
    }
}
