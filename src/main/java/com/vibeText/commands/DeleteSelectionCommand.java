package com.vibeText.commands;

import com.vibeText.core.EditorModel;

public final class DeleteSelectionCommand implements Command {
    private final EditorModel model;
    private final int start;
    private final int end;
    private final String deletedText;

    public DeleteSelectionCommand(EditorModel model, int start, int end) {
        this.model = model;
        this.start = start;
        this.end = end;
        this.deletedText = model.text().substring(start, end);
    }

    @Override
    public void execute() {
        model.delete(start, end);
    }

    @Override
    public void undo() {
        model.insert(start, deletedText);
    }
}

