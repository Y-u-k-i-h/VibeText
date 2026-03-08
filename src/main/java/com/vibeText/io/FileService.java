package com.vibeText.io;

import java.io.IOException;
import java.nio.file.Path;

public interface FileService {
    String read(Path path) throws IOException;

    void write(Path path, String content) throws IOException;
}

