package com.vibeText.performance;

import com.vibeText.core.PieceTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PieceTablePerformanceTest {
    @Test
    void handlesTenMbTextWithoutRunawayMemory() {
        String tenMb = "a".repeat(10 * 1024 * 1024);
        PieceTable table = new PieceTable();

        Runtime runtime = Runtime.getRuntime();
        System.gc();
        long before = runtime.totalMemory() - runtime.freeMemory();

        table.load(tenMb);
        table.insert(table.length(), "tail");
        table.delete(0, 128);

        long after = runtime.totalMemory() - runtime.freeMemory();
        long delta = Math.max(0, after - before);

        assertTrue(delta < 50L * 1024L * 1024L, "Memory delta exceeded 50MB: " + delta);
        assertTrue(table.length() > 0);
    }
}

