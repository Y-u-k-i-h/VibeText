package com.vibeText.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextSnapshotTest {
    @Test
    void computesLengthAndLineMetadata() {
        TextSnapshot snapshot = new TextSnapshot("a\nb\n");

        assertEquals("a\nb\n", snapshot.text());
        assertEquals(4, snapshot.length());
        assertEquals(3, snapshot.lineCount());
        assertEquals(0, snapshot.lineStartOffsets().get(0));
        assertEquals(2, snapshot.lineStartOffsets().get(1));
        assertEquals(4, snapshot.lineStartOffsets().get(2));
    }
}

