package com.vibeText.ui;

import com.vibeText.config.AppConfig;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class EditorFrame extends JFrame {
    private final JLabel titleLabel;
    private final TabBar tabBar;
    private final JPanel editorPanel;
    private JFileChooser fileChooser;

    /** The currently visible tab's scroll pane (may be null before first tab). */
    private JScrollPane currentScrollPane;

    public EditorFrame(String title, EditorController controller) {
        super(title);

        this.titleLabel = new JLabel("Untitled");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        titleLabel.setForeground(new Color(80, 80, 80));
        titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        titleLabel.setToolTipText("Click to rename / Save As");
        titleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.renameFile();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                titleLabel.setForeground(new Color(30, 30, 30));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                titleLabel.setForeground(new Color(80, 80, 80));
            }
        });

        this.tabBar = new TabBar();
        this.editorPanel = new JPanel(new BorderLayout());
        editorPanel.setPreferredSize(AppConfig.DEFAULT_WINDOW_SIZE);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(tabBar, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(editorPanel, BorderLayout.CENTER);

        setJMenuBar(buildMenuBar(controller));
        pack();
        // Now clear the fixed preferred size so the editor panel fills
        // whatever size the window manager / user gives us on resize.
        editorPanel.setPreferredSize(null);
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    public TabBar tabBar() {
        return tabBar;
    }

    /** Swaps the visible editor content to the given tab's scroll pane. */
    public void showTab(TabInfo tab) {
        if (currentScrollPane != null) {
            editorPanel.remove(currentScrollPane);
        }
        currentScrollPane = tab.scrollPane();
        editorPanel.add(currentScrollPane, BorderLayout.CENTER);
        editorPanel.revalidate();
        editorPanel.repaint();
        tab.textArea().requestFocusInWindow();
    }

    /** Returns the text area of the currently visible tab (for shortcuts). */
    public EditorTextArea currentTextArea() {
        // Controller always keeps this in sync, but guard against null.
        if (currentScrollPane != null) {
            return (EditorTextArea) currentScrollPane.getViewport().getView();
        }
        return null;
    }

    public void setFileName(String name) {
        titleLabel.setText(name == null || name.isBlank() ? "Untitled" : name);
    }

    public JFileChooser fileChooser() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
        }
        return fileChooser;
    }

    /**
     * Creates a flat button that shows a popup menu directly below itself.
     * Replaces JMenu to work around WLToolkit's broken popup grab that
     * causes dropdown menus to dismiss immediately on Wayland.
     */
    private static JButton createMenuButton(String label, JPopupMenu popup) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setMargin(new Insets(4, 8, 4, 8));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font(Font.DIALOG, Font.PLAIN, 13));

        button.addActionListener(e -> {
            if (popup.isVisible()) {
                popup.setVisible(false);
            } else {
                popup.show(button, 0, button.getHeight());
            }
        });

        return button;
    }

    private JMenuBar buildMenuBar(EditorController controller) {
        JMenuBar menuBar = new JMenuBar();

        // File popup
        JPopupMenu filePopup = new JPopupMenu();
        filePopup.add(new JMenuItem(new AbstractAction("Open") {
            @Override public void actionPerformed(ActionEvent e) { controller.openFile(); }
        }));
        filePopup.add(new JMenuItem(new AbstractAction("Save") {
            @Override public void actionPerformed(ActionEvent e) { controller.saveFile(); }
        }));

        // Edit popup
        JPopupMenu editPopup = new JPopupMenu();
        editPopup.add(new JMenuItem(new AbstractAction("Undo") {
            @Override public void actionPerformed(ActionEvent e) { controller.undo(); }
        }));
        editPopup.add(new JMenuItem(new AbstractAction("Redo") {
            @Override public void actionPerformed(ActionEvent e) { controller.redo(); }
        }));
        editPopup.addSeparator();
        editPopup.add(new JMenuItem(new AbstractAction("Copy") {
            @Override public void actionPerformed(ActionEvent e) { controller.copySelection(); }
        }));
        editPopup.add(new JMenuItem(new AbstractAction("Cut") {
            @Override public void actionPerformed(ActionEvent e) { controller.cutSelection(); }
        }));
        editPopup.add(new JMenuItem(new AbstractAction("Paste") {
            @Override public void actionPerformed(ActionEvent e) { controller.pasteClipboard(); }
        }));

        menuBar.add(createMenuButton("File", filePopup));
        menuBar.add(createMenuButton("Edit", editPopup));

        // Glue on both sides centers the title label.
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(titleLabel);
        menuBar.add(Box.createHorizontalGlue());

        // Register global keyboard shortcuts on the root pane.
        registerShortcut("open", KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), controller::openFile);
        registerShortcut("save", KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), controller::saveFile);
        registerShortcut("undo", KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), controller::undo);
        registerShortcut("redo", KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), controller::redo);
        registerShortcut("newTab", KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK), controller::addNewTab);
        registerShortcut("closeTab", KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK),
                controller::closeActiveTab);

        return menuBar;
    }

    private void registerShortcut(String id, KeyStroke keyStroke, Runnable action) {
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, id);
        getRootPane().getActionMap().put(id, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }
}
