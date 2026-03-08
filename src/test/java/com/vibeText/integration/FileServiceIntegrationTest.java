package com.vibeText.integration;

import com.vibeText.core.EditorModel;
import com.vibeText.core.PieceTable;
import com.vibeText.io.LocalFileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileServiceIntegrationTest {
    @TempDir
    Path tempDir;

    @Test
    void modelAndFileServiceRoundTrip() throws IOException {
        EditorModel model = new EditorModel(new PieceTable());
        LocalFileService fileService = new LocalFileService();
        Path path = tempDir.resolve("integration.txt");

        model.loadText("line1\nline2");
        fileService.write(path, model.text());

        String readBack = fileService.read(path);
        assertEquals(model.text(), readBack);
    }
}

