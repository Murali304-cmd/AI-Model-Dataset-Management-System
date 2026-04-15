package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("AI Model Dataset Management System");
        setSize(1000, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(37, 99, 235));
        header.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));

        JLabel title = new JLabel("AI Model Dataset Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Manage your datasets and AI models in one place");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(191, 219, 254));

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(3));
        titleBlock.add(subtitle);

        header.add(titleBlock, BorderLayout.WEST);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(Color.WHITE);
        tabs.setForeground(new Color(30, 64, 175));
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        tabs.addTab("  Datasets  ", new DatasetPanel());
        tabs.addTab("  AI Models  ", new ModelPanel());

        // Status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        statusBar.setBackground(new Color(241, 245, 249));
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(203, 213, 225)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        JLabel dot = new JLabel("●");
        dot.setForeground(new Color(22, 163, 74));
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 10));

        JLabel statusText = new JLabel("Connected  |  ai_dataset_db  |  MySQL 5.7");
        statusText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusText.setForeground(new Color(100, 116, 139));

        statusBar.add(dot);
        statusBar.add(statusText);

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}