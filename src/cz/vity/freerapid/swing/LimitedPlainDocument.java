package cz.vity.freerapid.swing;

import java.util.regex.Pattern;

/**
 * @author Vity
 */
public final class LimitedPlainDocument extends javax.swing.text.PlainDocument {
    private final Pattern pattern;

    public LimitedPlainDocument(final String regexp) {
        pattern = Pattern.compile(regexp);
    }

    public LimitedPlainDocument(final Pattern pattern) {
        this.pattern = pattern;
    }

    public final void insertString(final int param, final String str, final javax.swing.text.AttributeSet attributeSet) throws javax.swing.text.BadLocationException {
        if (str != null && str.length() > 0) {
            if (!pattern.matcher(getText(0, getLength()) + str).matches()) {
                java.awt.Toolkit.getDefaultToolkit().beep();
                return;
            }
        }
        super.insertString(param, str, attributeSet);
    }

}
