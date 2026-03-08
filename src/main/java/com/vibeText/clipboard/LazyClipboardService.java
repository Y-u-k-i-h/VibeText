package com.vibeText.clipboard;

public final class LazyClipboardService implements ClipboardService {
    private final Object lock = new Object();
    private volatile ClipboardService delegate;
    private volatile String fallbackText = "";

    @Override
    public void setText(String text) {
        ClipboardService service = resolveDelegate();
        if (service == null) {
            fallbackText = text == null ? "" : text;
            return;
        }
        service.setText(text);
    }

    @Override
    public String getText() {
        ClipboardService service = resolveDelegate();
        if (service == null) {
            return fallbackText;
        }
        return service.getText();
    }

    private ClipboardService resolveDelegate() {
        ClipboardService current = delegate;
        if (current != null) {
            return current;
        }

        synchronized (lock) {
            if (delegate != null) {
                return delegate;
            }
            try {
                delegate = new SystemClipboardService();
            } catch (Throwable ignored) {
                // Linux clipboard/toolkit init can fail or block in some environments.
                delegate = null;
            }
            return delegate;
        }
    }
}

