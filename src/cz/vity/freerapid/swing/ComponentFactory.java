package cz.vity.freerapid.swing;

import com.jgoodies.forms.layout.ColumnSpec;
import cz.vity.freerapid.swing.components.EditorPaneLinkDetector;
import cz.vity.freerapid.swing.components.PopdownButton;
import cz.vity.freerapid.swing.models.NaiiveComboModel;
import cz.vity.freerapid.swing.renderers.ComboBoxRenderer;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DateFormat;

/**
 * Trida slouzici k instanciovani upravenych zakladnich komponent
 *
 * @author Vity
 */

public class ComponentFactory {
    private FocusListener focusListener;

    private static ComponentFactory instance;

    public final static ColumnSpec DATEPICKER_COLUMN_SPEC = ColumnSpec.decode("max(pref;65dlu)");

    public static final ColumnSpec BUTTON_COLSPEC = ColumnSpec.decode("max(pref;42dlu)");

    private synchronized static ComponentFactory getInstance() {
        if (instance == null) {
            instance = new ComponentFactory();
        }
        return instance;
    }

    private ComponentFactory() {
        focusListener = new SelectAllOnFocusListener();
    }

    private FocusListener getFocusListener() {
        return focusListener;
    }

    public static JSpinner getTimeSpinner() {
        final JSpinner spinner = new JSpinner(new SpinnerDateModel());
        ((JSpinner.DateEditor) spinner.getEditor()).getTextField().setFormatterFactory(new DefaultFormatterFactory(new DateFormatter(DateFormat.getTimeInstance(DateFormat.SHORT))));
        return spinner;
    }

//    public static JXDatePicker getDatePicker() {
//        final JXDatePicker picker = new JXDatePicker();
//        picker.setFormats(new SimpleDateFormat(Swinger.getResourceMap().getString("shortDateFormat")));
//        return picker;
//    }

    public static JComboBox getComboBox() {
        JComboBox combo = new JComboBox(new NaiiveComboModel());
        combo.setRenderer(new ComboBoxRenderer());
        return combo;
    }

//    public static ColorComboBox getColorComboBox() {
//        return new ColorComboBox();
//    }

    public static JTextArea getTextArea() {
        final JTextArea textArea = new JTextArea();
        textArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        return textArea;
    }

//    public static FindBar getToolbarFindBar(JTextComponent component) {
//        return new FindBar(component);
//    }

//    public static JEditorPane getSQLArea() {
//        final JEditorPane textArea = new JEditorPane();
//        final EditorKit editorKit = new StyledEditorKit() {
//            public Document createDefaultDocument() {
//                return new SQLSyntaxDocument();
//            }
//        };
//        textArea.setCaret(new RiderCaret());
//        textArea.setEditorKitForContentType("text/sql", editorKit);
//        textArea.setContentType("text/sql");
//        textArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
//        return textArea;
//    }

    public static JTextField getTextField() {
        final JTextField field = new JTextField();
        field.addFocusListener(ComponentFactory.getInstance().getFocusListener());
        return field;
    }

    public static JButton getToolbarButton() {
        final JButton btn = new JButton();
        btn.setFocusable(false);
        btn.setPreferredSize(new Dimension(26, 23));
        btn.setMinimumSize(btn.getPreferredSize());
        return btn;
    }

    public static JToggleButton getToolbarToggleButton() {
        final JToggleButton btn = new JToggleButton();
        btn.setFocusable(false);
        btn.setPreferredSize(new Dimension(26, 23));
        btn.setMinimumSize(btn.getPreferredSize());
        return btn;
    }

    public static JPasswordField getPasswordField() {
        final JPasswordField field = new JPasswordField();
        field.addFocusListener(ComponentFactory.getInstance().getFocusListener());
        return field;
    }

    public static EditorPaneLinkDetector getURLsEditorPane() {
        return new EditorPaneLinkDetector();
    }

    public static PopdownButton getPopdownButton() {
        return new PopdownButton();
    }


    /**
     * Focus listener pouzivany v textovych komponentach. Na vstup do komponenty vybere celej text.
     */
    public static final class SelectAllOnFocusListener implements FocusListener {
        public final void focusGained(final FocusEvent e) {
            if (!e.isTemporary()) {
                //final Component component = ;
                ((JTextComponent) e.getComponent()).selectAll();
            }
        }

        public final void focusLost(final FocusEvent e) {
        }
    }
}
