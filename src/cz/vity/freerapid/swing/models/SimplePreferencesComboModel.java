package cz.vity.freerapid.swing.models;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;

import javax.swing.*;
import java.util.*;

/**
 * @author Vity
 */
public final class SimplePreferencesComboModel extends DefaultComboBoxModel {

    private final Stack<String> stack;
    private String keyProperties = null;
    private boolean autosave;
    private int maxRecentPhrasesCount;


    public SimplePreferencesComboModel(final String propertyKey, final boolean autosave) {
        this(AppPrefs.getProperty(UserProp.MAX_RECENT_PHRASES_COUNT, UserProp.MAX_RECENT_PHRASES_COUNT_DEFAULT), propertyKey, autosave);

    }

    public SimplePreferencesComboModel(final int maxRecentPhrasesCount, final String keyProperties, final boolean autosave) {
        this(new Stack<String>());
        this.maxRecentPhrasesCount = maxRecentPhrasesCount;
        this.keyProperties = keyProperties;
        this.autosave = autosave;
        final String[] values = AppPrefs.getProperty(keyProperties, "").split("\\|");
        for (String value : values) {
            if (value.length() > 0) {
                stack.add(0, value);
            }
        }
    }

    private SimplePreferencesComboModel(final Stack<String> v) {
        super(v);    //call to super
        this.stack = v;
        autosave = false;
    }

    public void setSelectedItem(Object anObject) {
        super.setSelectedItem(anObject);
        addElement(anObject);
    }

    public final void addElement(final Object anObject) {
        if (anObject == null)
            return;
        final int index = getIndexOf(anObject);
        if (index < 0) {
            final String s = anObject.toString().trim();
            if (!"".equals(s) && !"?".equals(s)) {
                super.insertElementAt(anObject, 0);
                if (stack.size() > maxRecentPhrasesCount) {
                    this.remove(this.maxRecentPhrasesCount - 1);
                    if (autosave)
                        store();
                }
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
        final StringBuilder builder = new StringBuilder();
        final Set<String> set = new HashSet<String>(stack);
        for (final Iterator<String> it = set.iterator(); it.hasNext();) {
            String str = it.next();
            builder.append(str);
            if (it.hasNext())
                builder.append("|");
        }

        AppPrefs.storeProperty(keyProperties, builder.toString());

    }
}