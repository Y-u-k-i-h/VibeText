package com.vibeText.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TextSnapshot {
    private final String text;
    private final List<Integer> lineStartOffsets;

    public TextSnapshot(String text) {
        this.text = text;
        this.lineStartOffsets = Collections.unmodifiableList(computeLineStarts(text));
    }

    public String text() {
        return text;
    }

    public int length() {
        return text.length();
    }

    public int lineCount() {
        return lineStartOffsets.size();
    }

    public List<Integer> lineStartOffsets() {
        return lineStartOffsets;
    }

    private static List<Integer> computeLineStarts(String text) {
        List<Integer> starts = new ArrayList<>();
        starts.add(0);
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                starts.add(i + 1);
            }
        }
        return starts;
    }
}

