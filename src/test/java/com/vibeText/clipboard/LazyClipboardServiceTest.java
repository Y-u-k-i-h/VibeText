package com.vibeText.clipboard;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LazyClipboardServiceTest {
    @Test
    void setThenGetTextRoundTrips() {
        LazyClipboardService service = new LazyClipboardService();

        service.setText("vibe");

        assertEquals("vibe", service.getText());
    }
}
