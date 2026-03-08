package com.vibeText.core;

import java.nio.file.Path;

public interface EditorObserver {
    void onTextChanged(TextSnapshot snapshot);

    void onFileChanged(Path filePath);
}

