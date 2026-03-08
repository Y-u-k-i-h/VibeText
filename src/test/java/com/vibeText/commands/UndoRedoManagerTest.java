package com.vibeText.commands;

import com.vibeText.core.EditorModel;
import com.vibeText.core.PieceTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UndoRedoManagerTest {
    @Test
    void executeUndoRedoInsertAndDelete() {
        EditorModel model = new EditorModel(new PieceTable());
        model.loadText("Hello");

        UndoRedoManager manager = new UndoRedoManager();
        manager.execute(new InsertTextCommand(model, 5, "!"));
        manager.execute(new DeleteSelectionCommand(model, 0, 1));

        assertEquals("ello!", model.text());
        assertTrue(manager.canUndo());

        manager.undo();
        assertEquals("Hello!", model.text());
        manager.undo();
        assertEquals("Hello", model.text());

        assertTrue(manager.canRedo());
        manager.redo();
        manager.redo();
        assertEquals("ello!", model.text());
        assertFalse(manager.canRedo());
    }
}

