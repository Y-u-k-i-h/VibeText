package com.vibeText.core;

public interface TextBuffer {
    void load(String text);

    void insert(int offset, String text);

    void delete(int startInclusive, int endExclusive);

    int length();

    String getText();

    TextSnapshot snapshot();
}

