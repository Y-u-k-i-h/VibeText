package com.vibeText.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * A horizontal tab strip with closable tabs and a "+" button.
 * Layout: [tab1 x] [tab2 x] ... [+]
 */
public final class TabBar extends JPanel {
    private static final Color BG = new Color(240, 240, 240);
    private static final Color ACTIVE_BG = Color.WHITE;
    private static final Color INACTIVE_FG = new Color(100, 100, 100);
    private static final Color ACTIVE_FG = new Color(20, 20, 20);
    private static final Color CLOSE_HOVER = new Color(200, 60, 60);
    private static final Font TAB_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

    private final List<JPanel> tabPanels = new ArrayList<>();
    private final JPanel tabStrip;
    private final JButton addButton;

    private int activeIndex = -1;
    private IntConsumer onSelect;
    private IntConsumer onClose;
    private Runnable onAdd;

    public TabBar() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(BG);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 210, 210)));

        tabStrip = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabStrip.setOpaque(false);
        add(tabStrip);

        add(Box.createHorizontalGlue());

        addButton = new JButton("+");
        addButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setContentAreaFilled(false);
        addButton.setMargin(new Insets(2, 8, 2, 8));
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.setToolTipText("New tab");
        addButton.addActionListener(e -> { if (onAdd != null) onAdd.run(); });
        add(addButton);
    }

    public void setOnSelect(IntConsumer onSelect) { this.onSelect = onSelect; }
    public void setOnClose(IntConsumer onClose) { this.onClose = onClose; }
    public void setOnAdd(Runnable onAdd) { this.onAdd = onAdd; }

    /** Rebuilds the tab strip from the given list of display names. */
    public void updateTabs(List<String> names, int selectedIndex) {
        tabStrip.removeAll();
        tabPanels.clear();
        activeIndex = selectedIndex;

        for (int i = 0; i < names.size(); i++) {
            JPanel tab = createTab(names.get(i), i, i == selectedIndex);
            tabPanels.add(tab);
            tabStrip.add(tab);
        }

        tabStrip.revalidate();
        tabStrip.repaint();
    }

    private JPanel createTab(String name, int index, boolean active) {
        JPanel tab = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        tab.setOpaque(true);
        tab.setBackground(active ? ACTIVE_BG : BG);
        tab.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(0, 6, 0, 2)
        ));

        JLabel label = new JLabel(name);
        label.setFont(TAB_FONT);
        label.setForeground(active ? ACTIVE_FG : INACTIVE_FG);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onSelect != null) onSelect.accept(index);
            }
        });
        tab.add(label);

        JLabel closeBtn = new JLabel(" ×");
        closeBtn.setFont(TAB_FONT);
        closeBtn.setForeground(INACTIVE_FG);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClose != null) onClose.accept(index);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                closeBtn.setForeground(CLOSE_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeBtn.setForeground(INACTIVE_FG);
            }
        });
        tab.add(closeBtn);

        tab.setMaximumSize(new Dimension(200, 30));
        return tab;
    }
}

