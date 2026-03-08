package com.vibeText.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalFileServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void writeThenReadRoundTrip() throws IOException {
        LocalFileService service = new LocalFileService();
        Path file = tempDir.resolve("vibe.txt");

        service.write(file, "hello\nworld");
        String text = service.read(file);

        assertEquals("hello\nworld", text);
    }
}

