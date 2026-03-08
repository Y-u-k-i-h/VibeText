package com.vibeText.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PieceTableTest {
    @Test
    void loadInsertDeleteProducesExpectedText() {
        PieceTable table = new PieceTable();
        table.load("Hello World");

        table.insert(5, ",");
        table.delete(6, 12);
        table.insert(6, " VibeText");

        assertEquals("Hello, VibeText", table.getText());
    }

    @Test
    void insertAtEndWorks() {
        PieceTable table = new PieceTable();
        table.load("abc");

        table.insert(3, "def");

        assertEquals("abcdef", table.getText());
        assertEquals(6, table.length());
    }

    @Test
    void invalidDeleteThrows() {
        PieceTable table = new PieceTable();
        table.load("abc");

        assertThrows(IndexOutOfBoundsException.class, () -> table.delete(1, 5));
    }

    @Test
    void snapshotContainsLineStarts() {
        PieceTable table = new PieceTable();
        table.load("a\nb\n");

        TextSnapshot snapshot = table.snapshot();

        assertEquals(3, snapshot.lineCount());
        assertEquals(3, snapshot.lineStartOffsets().size());
    }
}
