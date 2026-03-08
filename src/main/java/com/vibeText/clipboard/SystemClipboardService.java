package com.vibeText.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

public final class SystemClipboardService implements ClipboardService {
    private final Clipboard clipboard;

    public SystemClipboardService() {
        this(Toolkit.getDefaultToolkit().getSystemClipboard());
    }

    SystemClipboardService(Clipboard clipboard) {
        this.clipboard = clipboard;
    }

    @Override
    public void setText(String text) {
        clipboard.setContents(new StringSelection(text), null);
    }

    @Override
    public String getText() {
        try {
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                Object data = clipboard.getData(DataFlavor.stringFlavor);
                return data == null ? "" : data.toString();
            }
        } catch (Exception ignored) {
            return "";
        }
        return "";
    }
}

