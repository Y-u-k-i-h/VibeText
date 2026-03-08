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
            frame.textArea().requestFocusInWindow();
            // Flush the graphics pipeline to the display server.
            Toolkit.getDefaultToolkit().sync();
        });
    }
}