package com.vibeText.core;

final class Piece {
    private final BufferType bufferType;
    private final int start;
    private final int length;

    Piece(BufferType bufferType, int start, int length) {
        this.bufferType = bufferType;
        this.start = start;
        this.length = length;
    }

    BufferType bufferType() {
        return bufferType;
    }

    int start() {
        return start;
    }

    int length() {
        return length;
    }
}

