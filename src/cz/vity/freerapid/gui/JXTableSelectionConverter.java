package cz.vity.freerapid.gui;

import com.jgoodies.binding.value.AbstractConverter;
import com.jgoodies.binding.value.ValueModel;
import org.jdesktop.swingx.JXTable;

public class JXTableSelectionConverter extends AbstractConverter {
    final JXTable table;

    public JXTableSelectionConverter(final ValueModel
            selectionIndexHolder, final JXTable table) {
        super(selectionIndexHolder);
        this.table = table;
    }

    public Object convertFromSubject(Object subjectValue) {
        int viewIndex = -1;
        int modelIndex;

        if (subjectValue != null) {
            modelIndex = (Integer)
                    subjectValue;
            if (modelIndex >= 0) {
                viewIndex =
                        table.convertRowIndexToView(modelIndex);
            }
        }
        return viewIndex;
    }

    public void setValue(Object newValue) {
        int viewIndex;
        int modelIndex = -1;

        if (newValue != null) {
            viewIndex = (Integer) newValue;
            if (viewIndex >= 0) {
                modelIndex =
                        table.convertRowIndexToModel(viewIndex);
            }
        }
        subject.setValue(modelIndex);
    }
}