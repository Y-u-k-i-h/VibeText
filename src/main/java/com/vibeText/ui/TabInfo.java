package com.vibeText.ui;

import com.vibeText.commands.UndoRedoManager;
import com.vibeText.core.EditorModel;

import javax.swing.JScrollPane;
import java.nio.file.Path;

/**
 * Holds all state for a single editor tab.
 */
public final class TabInfo {
    private final EditorModel model;
    private final EditorTextArea textArea;
    private final LineNumberGutter gutter;
    private final JScrollPane scrollPane;
    private final UndoRedoManager undoRedoManager;
    private String displayName;

    public TabInfo(EditorModel model, UndoRedoManager undoRedoManager) {
        this.model = model;
        this.undoRedoManager = undoRedoManager;
        this.displayName = "Untitled";

        this.textArea = new EditorTextArea();
        textArea.setOpaque(true);
        textArea.setDoubleBuffered(true);

        this.gutter = new LineNumberGutter(textArea);

        this.scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(gutter);
        scrollPane.setOpaque(true);
        scrollPane.getViewport().setOpaque(true);
    }

    public EditorModel model() { return model; }
    public EditorTextArea textArea() { return textArea; }
    public JScrollPane scrollPane() { return scrollPane; }
    public UndoRedoManager undoRedoManager() { return undoRedoManager; }

    public String displayName() { return displayName; }

    public void updateDisplayName() {
        Path path = model.currentFilePath();
        this.displayName = path == null ? "Untitled" : path.getFileName().toString();
    }

    public void setDisplayName(String name) {
        this.displayName = name == null || name.isBlank() ? "Untitled" : name;
    }
}

