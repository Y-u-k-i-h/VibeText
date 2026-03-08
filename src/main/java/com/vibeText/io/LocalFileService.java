package com.vibeText.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class LocalFileService implements FileService {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public String read(Path path) throws IOException {
        lock.readLock().lock();
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void write(Path path, String content) throws IOException {
        lock.writeLock().lock();
        try {
            Files.writeString(path, content, StandardCharsets.UTF_8);
        } finally {
            lock.writeLock().unlock();
        }
    }
}

