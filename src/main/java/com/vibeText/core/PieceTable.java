package com.vibeText.core;

import java.util.ArrayList;
import java.util.List;

public final class PieceTable implements TextBuffer {
    private final StringBuilder originalBuffer = new StringBuilder();
    private final StringBuilder addBuffer = new StringBuilder();
    private final List<Piece> pieces = new ArrayList<>();

    @Override
    public synchronized void load(String text) {
        originalBuffer.setLength(0);
        addBuffer.setLength(0);
        pieces.clear();

        originalBuffer.append(text);
        if (!text.isEmpty()) {
            pieces.add(new Piece(BufferType.ORIGINAL, 0, text.length()));
        }
    }

    @Override
    public synchronized void insert(int offset, String text) {
        validateOffset(offset);
        if (text == null || text.isEmpty()) {
            return;
        }

        int addStart = addBuffer.length();
        addBuffer.append(text);

        int[] location = locatePiece(offset);
        int pieceIndex = location[0];
        int pieceOffset = location[1];

        Piece insertion = new Piece(BufferType.ADD, addStart, text.length());

        if (pieceIndex == pieces.size()) {
            pieces.add(insertion);
            return;
        }

        Piece current = pieces.get(pieceIndex);
        if (pieceOffset == 0) {
            pieces.add(pieceIndex, insertion);
            return;
        }

        if (pieceOffset == current.length()) {
            pieces.add(pieceIndex + 1, insertion);
            return;
        }

        Piece left = new Piece(current.bufferType(), current.start(), pieceOffset);
        Piece right = new Piece(current.bufferType(), current.start() + pieceOffset, current.length() - pieceOffset);
        pieces.set(pieceIndex, left);
        pieces.add(pieceIndex + 1, insertion);
        pieces.add(pieceIndex + 2, right);
    }

    @Override
    public synchronized void delete(int startInclusive, int endExclusive) {
        if (startInclusive == endExclusive) {
            return;
        }
        validateRange(startInclusive, endExclusive);

        List<Piece> rebuilt = new ArrayList<>();
        int consumed = 0;

        for (Piece piece : pieces) {
            int pieceStart = consumed;
            int pieceEnd = consumed + piece.length();
            consumed = pieceEnd;

            if (pieceEnd <= startInclusive || pieceStart >= endExclusive) {
                rebuilt.add(piece);
                continue;
            }

            if (pieceStart < startInclusive) {
                int keepLength = startInclusive - pieceStart;
                rebuilt.add(new Piece(piece.bufferType(), piece.start(), keepLength));
            }

            if (pieceEnd > endExclusive) {
                int removeLengthInPiece = Math.max(0, endExclusive - pieceStart);
                int keepStart = piece.start() + removeLengthInPiece;
                int keepLength = pieceEnd - endExclusive;
                rebuilt.add(new Piece(piece.bufferType(), keepStart, keepLength));
            }
        }

        pieces.clear();
        for (Piece piece : rebuilt) {
            if (piece.length() > 0) {
                pieces.add(piece);
            }
        }
    }

    @Override
    public synchronized int length() {
        int total = 0;
        for (Piece piece : pieces) {
            total += piece.length();
        }
        return total;
    }

    @Override
    public synchronized String getText() {
        StringBuilder result = new StringBuilder(length());
        for (Piece piece : pieces) {
            StringBuilder source = piece.bufferType() == BufferType.ORIGINAL ? originalBuffer : addBuffer;
            result.append(source, piece.start(), piece.start() + piece.length());
        }
        return result.toString();
    }

    @Override
    public synchronized TextSnapshot snapshot() {
        return new TextSnapshot(getText());
    }

    private void validateOffset(int offset) {
        if (offset < 0 || offset > length()) {
            throw new IndexOutOfBoundsException("Offset out of bounds: " + offset);
        }
    }

    private void validateRange(int startInclusive, int endExclusive) {
        if (startInclusive < 0 || endExclusive < 0 || startInclusive > endExclusive || endExclusive > length()) {
            throw new IndexOutOfBoundsException("Invalid range: [" + startInclusive + ", " + endExclusive + ")");
        }
    }

    private int[] locatePiece(int offset) {
        if (offset == length()) {
            return new int[]{pieces.size(), 0};
        }

        int consumed = 0;
        for (int i = 0; i < pieces.size(); i++) {
            Piece piece = pieces.get(i);
            int next = consumed + piece.length();
            if (offset <= next) {
                return new int[]{i, offset - consumed};
            }
            consumed = next;
        }
        return new int[]{pieces.size(), 0};
    }
}

