package com.vibeText;

import com.vibeText.clipboard.LazyClipboardService;
import com.vibeText.config.AppConfig;
import com.vibeText.core.EditorModel;
import com.vibeText.core.PieceTable;
import com.vibeText.io.LocalFileService;
import com.vibeText.ui.EditorController;
import com.vibeText.ui.EditorFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Toolkit;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        configureToolkit();

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            EditorModel model = new EditorModel(new PieceTable());
            EditorController controller = new EditorController(
                    model, new LocalFileService(), new LazyClipboardService());
            EditorFrame frame = new EditorFrame(AppConfig.APP_NAME, controller);
            controller.bind(frame);
            frame.setVisible(true);
            // Flush the graphics pipeline to the display server.
            Toolkit.getDefaultToolkit().sync();
        });
    }

    /**
     * Selects the best AWT toolkit for the current environment.
     * On Wayland (Java 25+) requests native WLToolkit for direct
     * compositor rendering. Falls back to default (XToolkit / macOS / Win32)
     * when Wayland is unavailable. Must run before any AWT class loads.
     */
    private static void configureToolkit() {
        String waylandDisplay = System.getenv("WAYLAND_DISPLAY");
        int javaVersion = Runtime.version().feature();
        if (waylandDisplay != null && !waylandDisplay.isEmpty() && javaVersion >= 25) {
            System.setProperty("awt.toolkit.name", "WLToolkit");
        }
    }
}