package cz.vity.freerapid.swing.models;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.utilities.Utils;

import javax.swing.*;
import java.io.File;
import java.util.*;

/**
 * @author Vity
 */
public final class RecentsFilesComboModel extends DefaultComboBoxModel {

    private final Stack<String> stack;
    private String keyProperties = null;
    private boolean autosave;
    private int maxRecentPhrasesCount;
    private boolean files;


    public RecentsFilesComboModel(final String propertyKey, final boolean autosave) {
        this(Consts.MAX_RECENT_PHRASES_COUNT, propertyKey, autosave, true);

    }

    public RecentsFilesComboModel(final int maxRecentPhrasesCount, final String keyProperties, final boolean autosave, boolean isFiles) {
        this(new Stack<String>());
        this.files = isFiles;
        this.maxRecentPhrasesCount = maxRecentPhrasesCount;
        this.keyProperties = keyProperties;
        this.autosave = autosave;
        final String[] values = AppPrefs.getProperty(keyProperties, "").split("\\|");
        for (String value : values) {
            if (isFiles && !(new File(value).exists()))
                continue;
            if (value.length() > 0) {
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
        addElement(anObject);
    }

    public final void addElement(final Object anObject) {
        if (anObject == null)
            return;
        final int index = getIndexOf(anObject);
        if (index < 0) {
            final String s = anObject.toString().trim();
            if (!"".equals(s) && !"?".equals(s)) {
                if (!(files && new File(s).exists())) {
                    super.insertElementAt(anObject, 0);
                    if (stack.size() > maxRecentPhrasesCount) {
                        this.remove(Consts.MAX_RECENT_PHRASES_COUNT - 1);
                        if (autosave)
                            storeFiles();
                    }
                }
            }
            if (autosave)
                storeFiles();
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

    public void storeFiles() {
        final StringBuilder builder = new StringBuilder();
        final Set<File> set = new HashSet<File>(stack.size());
        final boolean isWindows = Utils.isWindows();
        for (String str : stack) {
            if (isWindows && !str.endsWith("\\"))
                str = str + "\\";
            final File file = new File(str);
            if (!set.contains(file)) {
                set.add(file);
            }
        }
        for (final Iterator<File> it = set.iterator(); it.hasNext();) {
            File str = it.next();
            builder.append(str.getAbsolutePath());
            if (it.hasNext())
                builder.append("|");
        }

        AppPrefs.storeProperty(keyProperties, builder.toString());

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