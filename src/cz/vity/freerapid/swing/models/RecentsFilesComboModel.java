package cz.vity.freerapid.swing.models;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.Consts;

import javax.swing.*;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

/**
 * @author Vity
 */
public final class RecentsFilesComboModel extends DefaultComboBoxModel {

    private final Stack<String> stack;
    private String keyProperties = null;
    private boolean autosave;


    public RecentsFilesComboModel(final String keyProperties, final boolean autosave) {
        this(new Stack<String>());
        this.keyProperties = keyProperties;
        this.autosave = autosave;
        final String[] values = AppPrefs.getProperty(keyProperties, "").split("\\|");
        for (String value : values) {
            if (value.length() > 0 && new File(value).exists()) {
                stack.add(0, value);
//                System.out.println("Loading :" + searched);
                //       AppPrefs.removeProperty(key);
            }
        }
    }

    private RecentsFilesComboModel(final Stack<String> v) {
        super(v);    //call to super
        this.stack = v;
        autosave = false;
    }

    public void setSelectedItem(Object anObject) {
        super.setSelectedItem(anObject);
        //addElement(anObject);
    }

    public final void addElement(final Object anObject) {
        if (anObject == null)
            return;
        if (getIndexOf(anObject) >= 0) {

        } else if (!"".equals(anObject) && !"?".equals(anObject) && new File(anObject.toString()).exists()) {
            super.insertElementAt(anObject, 0);
            if (stack.size() > Consts.MAX_RECENT_PHRASES_COUNT) {
                this.remove(Consts.MAX_RECENT_PHRASES_COUNT - 1);
            }
            if (autosave)
                store();
        }
    }


    private void remove(int index) {
        setSelectedItem(getElementAt(0));
        stack.removeElementAt(index);
        fireIntervalRemoved(this, index, index);
    }

    public final Collection<String> getList() {
        return stack;
    }

    public void removeAllProperties() {
        AppPrefs.removeProperty(keyProperties);
    }

    public void store() {
        StringBuilder builder = new StringBuilder();
        for (final Iterator<String> it = stack.iterator(); it.hasNext();) {
            builder.append(it.next());
            if (it.hasNext())
                builder.append("|");
        }
        AppPrefs.storeProperty(keyProperties, builder.toString());

    }
}