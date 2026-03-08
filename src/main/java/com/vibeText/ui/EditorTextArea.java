package com.vibeText.ui;

import javax.swing.JTextArea;
import java.awt.Font;

public final class EditorTextArea extends JTextArea {
    public EditorTextArea() {
        setLineWrap(false);
        setWrapStyleWord(false);
        setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        setTabSize(4);
    }
}

