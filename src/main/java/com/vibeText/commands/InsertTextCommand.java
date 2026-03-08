package com.vibeText.commands;

import com.vibeText.core.EditorModel;

public final class InsertTextCommand implements Command {
    private final EditorModel model;
    private final int offset;
    private final String text;

    public InsertTextCommand(EditorModel model, int offset, String text) {
        this.model = model;
        this.offset = offset;
        this.text = text;
    }

    @Override
    public void execute() {
        model.insert(offset, text);
    }

    @Override
    public void undo() {
        model.delete(offset, offset + text.length());
    }
}

