package com.vibeText.core;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EditorModel {
    private final TextBuffer textBuffer;
    private final List<EditorObserver> observers = new CopyOnWriteArrayList<>();
    private Path currentFilePath;

    public EditorModel(TextBuffer textBuffer) {
        this.textBuffer = textBuffer;
    }

    public synchronized void loadText(String text) {
        textBuffer.load(text);
        notifyTextChanged();
    }

    public synchronized void insert(int offset, String text) {
        textBuffer.insert(offset, text);
        notifyTextChanged();
    }

    public synchronized void delete(int startInclusive, int endExclusive) {
        textBuffer.delete(startInclusive, endExclusive);
        notifyTextChanged();
    }

    public synchronized String text() {
        return textBuffer.getText();
    }

    public synchronized TextSnapshot snapshot() {
        return textBuffer.snapshot();
    }

    public synchronized void setCurrentFilePath(Path currentFilePath) {
        this.currentFilePath = currentFilePath;
        for (EditorObserver observer : observers) {
            observer.onFileChanged(currentFilePath);
        }
    }

    public synchronized Path currentFilePath() {
        return currentFilePath;
    }

    public void addObserver(EditorObserver observer) {
        observers.add(observer);
    }

    private void notifyTextChanged() {
        TextSnapshot snapshot = textBuffer.snapshot();
        for (EditorObserver observer : observers) {
            observer.onTextChanged(snapshot);
        }
    }
}

