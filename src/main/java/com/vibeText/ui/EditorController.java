package com.vibeText.ui;

import com.vibeText.clipboard.ClipboardService;
import com.vibeText.commands.DeleteSelectionCommand;
import com.vibeText.commands.InsertTextCommand;
import com.vibeText.commands.UndoRedoManager;
import com.vibeText.core.EditorModel;
import com.vibeText.core.EditorObserver;
import com.vibeText.core.PieceTable;
import com.vibeText.core.TextSnapshot;
import com.vibeText.io.FileService;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class EditorController implements EditorObserver {
    private final FileService fileService;
    private final ClipboardService clipboardService;

    private final List<TabInfo> tabs = new ArrayList<>();
    private int activeTabIndex = -1;

    private EditorFrame frame;
    private boolean suppressDocumentEvents;

    public EditorController(EditorModel initialModel, FileService fileService, ClipboardService clipboardService) {
        this.fileService = fileService;
        this.clipboardService = clipboardService;

        // Create the first tab from the initial model.
        TabInfo firstTab = new TabInfo(initialModel, new UndoRedoManager());
        initialModel.addObserver(this);
        tabs.add(firstTab);
        activeTabIndex = 0;
    }

    public void bind(EditorFrame frame) {
        this.frame = frame;

        // Wire tab bar callbacks.
        frame.tabBar().setOnSelect(this::selectTab);
        frame.tabBar().setOnClose(this::closeTab);
        frame.tabBar().setOnAdd(this::addNewTab);

        // Bind document listener for the first tab and show it.
        bindDocumentListener(tabs.get(0));
        refreshTabBar();
        frame.showTab(tabs.get(0));
        updateTitleForActiveTab();
    }

    // --- Tab management ---

    public void addNewTab() {
        EditorModel model = new EditorModel(new PieceTable());
        model.addObserver(this);
        TabInfo tab = new TabInfo(model, new UndoRedoManager());
        bindDocumentListener(tab);
        tabs.add(tab);
        activeTabIndex = tabs.size() - 1;
        refreshTabBar();
        frame.showTab(tab);
        updateTitleForActiveTab();
    }

    public void selectTab(int index) {
        if (index < 0 || index >= tabs.size() || index == activeTabIndex) return;
        activeTabIndex = index;
        TabInfo tab = tabs.get(index);
        refreshTabBar();
        frame.showTab(tab);
        updateTitleForActiveTab();
    }

    public void closeActiveTab() {
        closeTab(activeTabIndex);
    }

    public void closeTab(int index) {
        if (index < 0 || index >= tabs.size()) return;
        // Don't close the last tab — create a fresh one instead.
        if (tabs.size() == 1) {
            EditorModel model = new EditorModel(new PieceTable());
            model.addObserver(this);
            TabInfo fresh = new TabInfo(model, new UndoRedoManager());
            bindDocumentListener(fresh);
            tabs.set(0, fresh);
            activeTabIndex = 0;
            refreshTabBar();
            frame.showTab(fresh);
            updateTitleForActiveTab();
            return;
        }

        tabs.remove(index);
        if (activeTabIndex >= tabs.size()) {
            activeTabIndex = tabs.size() - 1;
        } else if (activeTabIndex > index) {
            activeTabIndex--;
        }
        // If we closed the active tab, show the new active one.
        refreshTabBar();
        frame.showTab(tabs.get(activeTabIndex));
        updateTitleForActiveTab();
    }

    // --- Active tab helpers ---

    private TabInfo activeTab() {
        return tabs.get(activeTabIndex);
    }

    private EditorTextArea activeTextArea() {
        return activeTab().textArea();
    }

    private EditorModel activeModel() {
        return activeTab().model();
    }

    private UndoRedoManager activeUndoRedo() {
        return activeTab().undoRedoManager();
    }

    private void refreshTabBar() {
        List<String> names = tabs.stream().map(TabInfo::displayName).collect(Collectors.toList());
        frame.tabBar().updateTabs(names, activeTabIndex);
    }

    private void updateTitleForActiveTab() {
        TabInfo tab = activeTab();
        String name = tab.displayName();
        frame.setTitle(name + " - VibeText");
        frame.setFileName(name);
    }

    // --- Document listener wiring (per tab) ---

    private void bindDocumentListener(TabInfo tab) {
        tab.textArea().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (suppressDocumentEvents) return;
                suppressDocumentEvents = true;
                try {
                    String inserted = e.getDocument().getText(e.getOffset(), e.getLength());
                    tab.undoRedoManager().execute(new InsertTextCommand(tab.model(), e.getOffset(), inserted));
                } catch (BadLocationException ex) {
                    showError("Could not read inserted text", ex);
                } finally {
                    suppressDocumentEvents = false;
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (suppressDocumentEvents) return;
                suppressDocumentEvents = true;
                try {
                    tab.undoRedoManager().execute(
                            new DeleteSelectionCommand(tab.model(), e.getOffset(), e.getOffset() + e.getLength()));
                } finally {
                    suppressDocumentEvents = false;
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) { }
        });
    }

    // --- File operations (always on active tab) ---

    public void openFile() {
        if (frame.fileChooser().showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) return;
        Path selected = frame.fileChooser().getSelectedFile().toPath();

        // If active tab is empty and untitled, load into it; otherwise open a new tab.
        TabInfo target;
        if (activeModel().text().isEmpty() && activeModel().currentFilePath() == null) {
            target = activeTab();
        } else {
            EditorModel model = new EditorModel(new PieceTable());
            model.addObserver(this);
            target = new TabInfo(model, new UndoRedoManager());
            bindDocumentListener(target);
            tabs.add(target);
            activeTabIndex = tabs.size() - 1;
        }

        TabInfo finalTarget = target;
        new Thread(() -> {
            try {
                String content = fileService.read(selected);
                SwingUtilities.invokeLater(() -> {
                    suppressDocumentEvents = true;
                    try {
                        finalTarget.model().loadText(content);
                        finalTarget.model().setCurrentFilePath(selected);
                        finalTarget.textArea().setText(content);
                        finalTarget.textArea().setCaretPosition(0);
                        finalTarget.updateDisplayName();
                        refreshTabBar();
                        frame.showTab(finalTarget);
                        updateTitleForActiveTab();
                    } finally {
                        suppressDocumentEvents = false;
                    }
                });
            } catch (IOException ex) {
                showError("Could not open file: " + selected, ex);
            }
        }, "open-file-thread").start();
    }

    public void saveFile() {
        Path target = activeModel().currentFilePath();
        if (target == null) {
            if (frame.fileChooser().showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) return;
            target = frame.fileChooser().getSelectedFile().toPath();
            activeModel().setCurrentFilePath(target);
            activeTab().updateDisplayName();
            refreshTabBar();
            updateTitleForActiveTab();
        }

        Path finalTarget = target;
        String content = activeModel().text();
        new Thread(() -> {
            try {
                fileService.write(finalTarget, content);
            } catch (IOException ex) {
                showError("Could not save file: " + finalTarget, ex);
            }
        }, "save-file-thread").start();
    }

    public void renameFile() {
        if (frame.fileChooser().showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) return;
        Path selected = frame.fileChooser().getSelectedFile().toPath();
        activeModel().setCurrentFilePath(selected);
        activeTab().updateDisplayName();
        refreshTabBar();
        updateTitleForActiveTab();

        String content = activeModel().text();
        new Thread(() -> {
            try {
                fileService.write(selected, content);
            } catch (IOException ex) {
                showError("Could not save file: " + selected, ex);
            }
        }, "rename-file-thread").start();
    }

    // --- Clipboard ---

    public void copySelection() {
        String selected = activeTextArea().getSelectedText();
        if (selected != null && !selected.isEmpty()) {
            clipboardService.setText(selected);
        }
    }

    public void cutSelection() {
        copySelection();
        activeTextArea().replaceSelection("");
    }

    public void pasteClipboard() {
        String clip = clipboardService.getText();
        if (!clip.isEmpty()) {
            activeTextArea().replaceSelection(clip);
        }
    }

    // --- Undo / Redo ---

    public void undo() {
        if (!activeUndoRedo().canUndo()) return;
        suppressDocumentEvents = true;
        try {
            activeUndoRedo().undo();
        } finally {
            suppressDocumentEvents = false;
        }
    }

    public void redo() {
        if (!activeUndoRedo().canRedo()) return;
        suppressDocumentEvents = true;
        try {
            activeUndoRedo().redo();
        } finally {
            suppressDocumentEvents = false;
        }
    }

    // --- Observer callbacks (from any model) ---

    @Override
    public void onTextChanged(TextSnapshot snapshot) {
        if (frame == null) return;
        // Find which tab this snapshot belongs to.
        SwingUtilities.invokeLater(() -> {
            for (TabInfo tab : tabs) {
                if (tab.model().text().equals(snapshot.text())) {
                    if (tab.textArea().getText().equals(snapshot.text())) continue;
                    suppressDocumentEvents = true;
                    try {
                        tab.textArea().setText(snapshot.text());
                    } finally {
                        suppressDocumentEvents = false;
                    }
                    break;
                }
            }
        });
    }

    @Override
    public void onFileChanged(Path filePath) {
        if (frame == null) return;
        SwingUtilities.invokeLater(() -> {
            // Update display name for whatever tab changed.
            for (TabInfo tab : tabs) {
                tab.updateDisplayName();
            }
            refreshTabBar();
            updateTitleForActiveTab();
        });
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
