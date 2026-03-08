package com.vibeText.ui;

import com.vibeText.clipboard.ClipboardService;
import com.vibeText.commands.DeleteSelectionCommand;
import com.vibeText.commands.InsertTextCommand;
import com.vibeText.commands.UndoRedoManager;
import com.vibeText.core.EditorModel;
import com.vibeText.core.EditorObserver;
import com.vibeText.core.TextSnapshot;
import com.vibeText.io.FileService;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.nio.file.Path;

public final class EditorController implements EditorObserver {
    private final EditorModel model;
    private final FileService fileService;
    private final ClipboardService clipboardService;
    private final UndoRedoManager undoRedoManager = new UndoRedoManager();

    private EditorFrame frame;
    private boolean suppressDocumentEvents;

    public EditorController(EditorModel model, FileService fileService, ClipboardService clipboardService) {
        this.model = model;
        this.fileService = fileService;
        this.clipboardService = clipboardService;
        this.model.addObserver(this);
    }

    public void bind(EditorFrame frame) {
        this.frame = frame;
        frame.textArea().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (suppressDocumentEvents) {
                    return;
                }
                suppressDocumentEvents = true;
                try {
                    String inserted = e.getDocument().getText(e.getOffset(), e.getLength());
                    undoRedoManager.execute(new InsertTextCommand(model, e.getOffset(), inserted));
                } catch (BadLocationException ex) {
                    showError("Could not read inserted text", ex);
                } finally {
                    suppressDocumentEvents = false;
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (suppressDocumentEvents) {
                    return;
                }
                suppressDocumentEvents = true;
                try {
                    undoRedoManager.execute(new DeleteSelectionCommand(model, e.getOffset(), e.getOffset() + e.getLength()));
                } finally {
                    suppressDocumentEvents = false;
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // no-op for plain text components
            }
        });
    }

    public void openFile() {
        if (frame.fileChooser().showOpenDialog(frame) != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path selected = frame.fileChooser().getSelectedFile().toPath();
        new Thread(() -> {
            try {
                String content = fileService.read(selected);
                model.loadText(content);
                model.setCurrentFilePath(selected);
            } catch (IOException ex) {
                showError("Could not open file: " + selected, ex);
            }
        }, "open-file-thread").start();
    }

    public void saveFile() {
        Path target = model.currentFilePath();
        if (target == null) {
            if (frame.fileChooser().showSaveDialog(frame) != javax.swing.JFileChooser.APPROVE_OPTION) {
                return;
            }
            target = frame.fileChooser().getSelectedFile().toPath();
            model.setCurrentFilePath(target);
        }

        Path finalTarget = target;
        String content = model.text();
        new Thread(() -> {
            try {
                fileService.write(finalTarget, content);
            } catch (IOException ex) {
                showError("Could not save file: " + finalTarget, ex);
            }
        }, "save-file-thread").start();
    }

    public void copySelection() {
        String selected = frame.textArea().getSelectedText();
        if (selected != null && !selected.isEmpty()) {
            clipboardService.setText(selected);
        }
    }

    public void cutSelection() {
        copySelection();
        frame.textArea().replaceSelection("");
    }

    public void pasteClipboard() {
        String clip = clipboardService.getText();
        if (!clip.isEmpty()) {
            frame.textArea().replaceSelection(clip);
        }
    }

    public void undo() {
        if (!undoRedoManager.canUndo()) {
            return;
        }
        suppressDocumentEvents = true;
        try {
            undoRedoManager.undo();
        } finally {
            suppressDocumentEvents = false;
        }
    }

    public void redo() {
        if (!undoRedoManager.canRedo()) {
            return;
        }
        suppressDocumentEvents = true;
        try {
            undoRedoManager.redo();
        } finally {
            suppressDocumentEvents = false;
        }
    }

    @Override
    public void onTextChanged(TextSnapshot snapshot) {
        if (frame == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            if (frame == null) {
                return;
            }
            if (frame.textArea().getText().equals(snapshot.text())) {
                return;
            }
            suppressDocumentEvents = true;
            try {
                frame.textArea().setText(snapshot.text());
            } finally {
                suppressDocumentEvents = false;
            }
        });
    }

    @Override
    public void onFileChanged(Path filePath) {
        if (frame == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> frame.setTitle(filePath == null
            ? "VibeText"
            : filePath.getFileName() + " - VibeText"));
    }

    private void showError(String message, Exception ex) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
            frame,
            message + "\n" + ex.getMessage(),
            "VibeText Error",
            JOptionPane.ERROR_MESSAGE
        ));
    }
}
