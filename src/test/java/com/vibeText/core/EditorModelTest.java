package com.vibeText.core;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EditorModelTest {
    @Test
    void observersReceiveTextAndFileEvents() {
        EditorModel model = new EditorModel(new PieceTable());
        AtomicInteger textNotifications = new AtomicInteger();
        AtomicInteger fileNotifications = new AtomicInteger();

        model.addObserver(new EditorObserver() {
            @Override
            public void onTextChanged(TextSnapshot snapshot) {
                textNotifications.incrementAndGet();
            }

            @Override
            public void onFileChanged(Path filePath) {
                fileNotifications.incrementAndGet();
            }
        });

        model.loadText("abc");
        model.insert(3, "def");
        model.delete(0, 1);
        Path path = Path.of("demo.txt");
        model.setCurrentFilePath(path);

        TextSnapshot snapshot = model.snapshot();

        assertEquals("bcdef", model.text());
        assertEquals("bcdef", snapshot.text());
        assertEquals(5, snapshot.length());
        assertEquals(path, model.currentFilePath());
        assertEquals(3, textNotifications.get());
        assertEquals(1, fileNotifications.get());
    }
}
