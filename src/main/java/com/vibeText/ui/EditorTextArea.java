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

    /**
     * Always stretch to fill the viewport width, even when line-wrap is off.
     * Without this, JTextArea only sizes itself to its content width,
     * leaving empty space on the right side of the scroll pane.
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {
        if (getParent() instanceof javax.swing.JViewport viewport) {
            return getPreferredSize().width <= viewport.getWidth();
        }
        return super.getScrollableTracksViewportWidth();
    }
}

