package com.vibeText.ui;

import com.vibeText.config.AppConfig;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public final class EditorFrame extends JFrame {
    private final EditorTextArea textArea;
    private JFileChooser fileChooser;

    public EditorFrame(String title, EditorController controller) {
        super(title);

        this.textArea = new EditorTextArea();
        textArea.setOpaque(true);
        textArea.setDoubleBuffered(true);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        LineNumberGutter gutter = new LineNumberGutter(textArea);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(gutter);
        scrollPane.setOpaque(true);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setPreferredSize(AppConfig.DEFAULT_WINDOW_SIZE);

        // Use getContentPane().add() — never replace the content pane.
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        setJMenuBar(buildMenuBar(controller));
        pack();
        setLocationRelativeTo(null);
    }

    public EditorTextArea textArea() {
        return textArea;
    }

    public JFileChooser fileChooser() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
        }
        return fileChooser;
    }

    private JMenuBar buildMenuBar(EditorController controller) {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem(new AbstractAction("Open") {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.openFile();
            }
        }));
        fileMenu.add(new JMenuItem(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.saveFile();
            }
        }));

        JMenu editMenu = new JMenu("Edit");
        editMenu.add(new JMenuItem(new AbstractAction("Undo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.undo();
            }
        }));
        editMenu.add(new JMenuItem(new AbstractAction("Redo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.redo();
            }
        }));
        editMenu.addSeparator();
        editMenu.add(new JMenuItem(new AbstractAction("Copy") {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.copySelection();
            }
        }));
        editMenu.add(new JMenuItem(new AbstractAction("Cut") {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.cutSelection();
            }
        }));
        editMenu.add(new JMenuItem(new AbstractAction("Paste") {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.pasteClipboard();
            }
        }));

        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        registerShortcut("open", KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), controller::openFile);
        registerShortcut("save", KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), controller::saveFile);
        registerShortcut("undo", KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), controller::undo);
        registerShortcut("redo", KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), controller::redo);
        registerShortcut("copy", KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), controller::copySelection);
        registerShortcut("cut", KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK), controller::cutSelection);
        registerShortcut("paste", KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), controller::pasteClipboard);

        return menuBar;
    }

    private void registerShortcut(String id, KeyStroke keyStroke, Runnable action) {
        textArea.getInputMap().put(keyStroke, id);
        textArea.getActionMap().put(id, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }
}
