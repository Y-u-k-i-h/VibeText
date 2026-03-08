package com.vibeText.clipboard;

import org.junit.jupiter.api.Test;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SystemClipboardServiceTest {
    @Test
    void setThenGetText() {
        Clipboard clipboard = new Clipboard("test");
        SystemClipboardService service = new SystemClipboardService(clipboard);

        service.setText("vibe");

        assertEquals("vibe", service.getText());
    }

    @Test
    void returnsEmptyWhenNoTextFlavor() {
        Clipboard clipboard = new Clipboard("empty");
        SystemClipboardService service = new SystemClipboardService(clipboard);

        assertEquals("", service.getText());
    }

    @Test
    void returnsEmptyWhenClipboardThrows() {
        Clipboard clipboard = new Clipboard("broken") {
            @Override
            public boolean isDataFlavorAvailable(DataFlavor flavor) {
                return true;
            }

            @Override
            public Object getData(DataFlavor flavor) throws IOException {
                throw new IOException("simulated clipboard error");
            }
        };

        SystemClipboardService service = new SystemClipboardService(clipboard);

        assertEquals("", service.getText());
    }
}
