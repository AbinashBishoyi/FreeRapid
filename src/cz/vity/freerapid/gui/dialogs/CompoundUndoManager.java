package cz.vity.freerapid.gui.dialogs;

import cz.vity.freerapid.swing.SwingUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public class CompoundUndoManager extends UndoManager implements UndoableEditListener {
    public CompoundEdit compoundEdit;
    private JTextComponent editor;
    private int lastOffset;

    public CompoundUndoManager(JTextComponent editor) {
        this.editor = editor;
        editor.getDocument().addUndoableEditListener(this);
        editor.getActionMap().put("undo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (CompoundUndoManager.this.canUndo())
                    CompoundUndoManager.this.undo();
            }
        });
        editor.getActionMap().put("redo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (CompoundUndoManager.this.canRedo())
                    CompoundUndoManager.this.redo();
            }
        });
        editor.getInputMap().put(SwingUtils.getCtrlKeyStroke(KeyEvent.VK_Z), "undo");
        editor.getInputMap().put(SwingUtils.getCtrlShiftKeyStroke(KeyEvent.VK_Z), "redo");

    }

    /*
     **  Whenever an UndoableEdit happens the edit will either be absorbed
     **  by the current compound edit or a new compound edit will be started
     */
    public void undoableEditHappened(UndoableEditEvent e) {
        //  Start a new compound edit

        if (compoundEdit == null) {
            compoundEdit = startCompoundEdit(e.getEdit());
            return;
        }

        //  Check for an incremental edit, backspace or attribute change

        AbstractDocument.DefaultDocumentEvent event =
                (AbstractDocument.DefaultDocumentEvent) e.getEdit();

        //System.out.println(event.getLength());

        int diff = editor.getCaretPosition() - lastOffset;

        if (Math.abs(diff) == 1 || event.getType().equals(DocumentEvent.EventType.CHANGE)) {
            compoundEdit.addEdit(e.getEdit());
            lastOffset += diff;
            return;
        }

        //  Not incremental edit, end previous edit and start a new one

        compoundEdit.end();
        compoundEdit = startCompoundEdit(e.getEdit());
    }

    /*
     **  Each CompoundEdit will store a group of related incremental edits
     **  (ie. each character typed or backspaced is an incremental edit)
     */
    private CompoundEdit startCompoundEdit(UndoableEdit anEdit) {
        //  Track the starting offset of this compound edit

        lastOffset = editor.getCaretPosition();

        //  The compound edit is used to store incremental edits

        compoundEdit = new MyCompoundEdit();
        compoundEdit.addEdit(anEdit);

        //  The compound edit is added to the UndoManager. All incremental
        //  edits stored in the compound edit will be undone/redone at once

        addEdit(compoundEdit);
        return compoundEdit;
    }

    class MyCompoundEdit extends CompoundEdit {
        public boolean isInProgress() {
            //  in order for the canUndo() and canRedo() methods to work
            //  assume that the compound edit is never in progress

            return false;
        }

        public void undo() throws CannotUndoException {
            //  End the edit so future edits don't get absorbed by this edit

            if (compoundEdit != null)
                compoundEdit.end();

            super.undo();

            //  Always start a new compound edit after an undo

            compoundEdit = null;
        }

    }


}