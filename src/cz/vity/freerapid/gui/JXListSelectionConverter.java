package cz.vity.freerapid.gui;

import com.jgoodies.binding.value.AbstractConverter;
import com.jgoodies.binding.value.ValueModel;
import org.jdesktop.swingx.JXList;

public class JXListSelectionConverter extends AbstractConverter {
    final JXList list;

    public JXListSelectionConverter(final ValueModel
            selectionIndexHolder, final JXList list) {
        super(selectionIndexHolder);
        this.list = list;
    }

    public Object convertFromSubject(Object subjectValue) {
        int viewIndex = -1;
        int modelIndex;

        if (subjectValue != null) {
            modelIndex = (Integer)
                    subjectValue;
            if (modelIndex >= 0) {
                viewIndex =
                        list.convertIndexToView(modelIndex);
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
                        list.convertIndexToModel(viewIndex);
            }
        }
        subject.setValue(modelIndex);
    }
}