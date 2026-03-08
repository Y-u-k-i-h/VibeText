package com.vibeText.ui;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

public final class LineNumberGutter extends JComponent {
    private static final int PADDING = 8;
    private final JTextArea textArea;

    public LineNumberGutter(JTextArea textArea) {
        this.textArea = textArea;
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                repaint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                repaint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                repaint();
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        Element root = textArea.getDocument().getDefaultRootElement();
        int lineCount = Math.max(1, root.getElementCount());
        int digits = Integer.toString(lineCount).length();
        FontMetrics metrics = getFontMetrics(textArea.getFont());
        int width = PADDING * 2 + metrics.charWidth('0') * digits;
        return new Dimension(width, textArea.getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(245, 245, 245));
        g.fillRect(0, 0, getWidth(), getHeight());

        FontMetrics metrics = g.getFontMetrics(textArea.getFont());
        int lineHeight = metrics.getHeight();
        int ascent = metrics.getAscent();
        int lineCount = Math.max(1, textArea.getLineCount());

        g.setColor(new Color(120, 120, 120));
        for (int line = 0; line < lineCount; line++) {
            String lineNumber = Integer.toString(line + 1);
            int y = line * lineHeight + ascent;
            int x = getWidth() - PADDING - metrics.stringWidth(lineNumber);
            g.drawString(lineNumber, x, y);
        }
    }
}

