package ui;

import dao.DatasetDAO;
import model.Dataset;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class DatasetPanel extends JPanel {

    private DatasetDAO dao = new DatasetDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtName, txtCategory, txtFormat, txtSize, txtUrl, txtSearch;
    private JTextArea txtDesc;
    private JComboBox<String> cmbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch;
    private int selectedId = -1;

    // ── Color palette ────────────────────────────────────────────────────────
    private static final Color BG_PAGE    = new Color(248, 250, 252);
    private static final Color BG_CARD    = Color.WHITE;
    private static final Color BG_FIELD   = Color.WHITE;
    private static final Color BORDER     = new Color(203, 213, 225);
    private static final Color ACCENT     = new Color(37, 99, 235);
    private static final Color TXT_LABEL  = new Color(51, 65, 85);
    private static final Color TXT_HINT   = new Color(148, 163, 184);
    private static final Color BTN_ADD    = new Color(22, 163, 74);
    private static final Color BTN_UPD    = new Color(234, 88, 12);
    private static final Color BTN_DEL    = new Color(220, 38, 38);
    private static final Color BTN_CLR    = new Color(100, 116, 139);
    private static final Color BTN_SRC    = new Color(37, 99, 235);
    private static final Color TBL_HDR    = new Color(37, 99, 235);
    private static final Color TBL_ALT    = new Color(248, 250, 252);

    public DatasetPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(BG_PAGE);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(buildFormCard(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);
        loadTable(dao.getAllDatasets());
    }

    // ── Form card ────────────────────────────────────────────────────────────
    private JPanel buildFormCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        // Card title row
        JLabel cardTitle = new JLabel("Dataset Details");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cardTitle.setForeground(ACCENT);
        cardTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BG_CARD);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        txtName     = field(); txtCategory = field();
        txtFormat   = field(); txtSize     = field();
        txtUrl      = field(); txtSearch   = field();

        txtDesc = new JTextArea(2, 20);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDesc.setForeground(TXT_LABEL);
        txtDesc.setBackground(BG_FIELD);
        txtDesc.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        txtDesc.setLineWrap(true);

        cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive", "Archived"});
        styleCombo(cmbStatus);

        // Row 0
        g.gridy = 0; g.gridx = 0; form.add(lbl("Name *"),     g);
        g.gridx = 1; form.add(txtName, g);
        g.gridx = 2; form.add(lbl("Category *"), g);
        g.gridx = 3; form.add(txtCategory, g);

        // Row 1
        g.gridy = 1; g.gridx = 0; form.add(lbl("Format *"),  g);
        g.gridx = 1; form.add(txtFormat, g);
        g.gridx = 2; form.add(lbl("Size (MB)"), g);
        g.gridx = 3; form.add(txtSize, g);

        // Row 2
        g.gridy = 2; g.gridx = 0; form.add(lbl("Source URL"), g);
        g.gridx = 1; form.add(txtUrl, g);
        g.gridx = 2; form.add(lbl("Status"),     g);
        g.gridx = 3; form.add(cmbStatus, g);

        // Row 3 - description
        g.gridy = 3; g.gridx = 0; form.add(lbl("Description"), g);
        g.gridx = 1; g.gridwidth = 3;
        form.add(new JScrollPane(txtDesc), g);
        g.gridwidth = 1;

        // Row 4 - search
        g.gridy = 4; g.gridx = 0; form.add(lbl("Search"), g);
        g.gridx = 1; form.add(txtSearch, g);
        btnSearch = btn("Search", BTN_SRC);
        g.gridx = 2; form.add(btnSearch, g);

        // Row 5 - action buttons
        btnAdd    = btn("Add Dataset", BTN_ADD);
        btnUpdate = btn("Update",      BTN_UPD);
        btnDelete = btn("Delete",      BTN_DEL);
        btnClear  = btn("Clear",       BTN_CLR);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnAdd); btnRow.add(btnUpdate);
        btnRow.add(btnDelete); btnRow.add(btnClear);

        g.gridy = 5; g.gridx = 0; g.gridwidth = 4;
        g.insets = new Insets(10, 6, 0, 6);
        form.add(btnRow, g);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);

        // Listeners
        btnAdd.addActionListener(e -> addDataset());
        btnUpdate.addActionListener(e -> updateDataset());
        btnDelete.addActionListener(e -> deleteDataset());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> {
            String kw = txtSearch.getText().trim();
            loadTable(kw.isEmpty() ? dao.getAllDatasets() : dao.searchDatasets(kw));
        });

        return card;
    }

    // ── Table card ───────────────────────────────────────────────────────────
    private JPanel buildTableCard() {
        String[] cols = {"ID", "Name", "Category", "Format", "Size (MB)", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0)
                populateForm();
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        sp.getViewport().setBackground(BG_CARD);

        JLabel cardTitle = new JLabel("Dataset Records");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cardTitle.setForeground(ACCENT);
        cardTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        card.add(cardTitle, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    // ── Data operations ──────────────────────────────────────────────────────
    private void loadTable(List<Dataset> list) {
        tableModel.setRowCount(0);
        for (Dataset d : list)
            tableModel.addRow(new Object[]{
                d.getId(), d.getName(), d.getCategory(),
                d.getFormat(), d.getSizeMb(), d.getStatus()
            });
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedId = (int) tableModel.getValueAt(row, 0);
        for (Dataset d : dao.getAllDatasets()) {
            if (d.getId() == selectedId) {
                txtName.setText(d.getName());
                txtCategory.setText(d.getCategory());
                txtFormat.setText(d.getFormat());
                txtSize.setText(String.valueOf(d.getSizeMb()));
                txtUrl.setText(d.getSourceUrl() == null ? "" : d.getSourceUrl());
                txtDesc.setText(d.getDescription() == null ? "" : d.getDescription());
                cmbStatus.setSelectedItem(d.getStatus());
                break;
            }
        }
    }

    private void addDataset() {
        String name     = txtName.getText().trim();
        String category = txtCategory.getText().trim();
        String format   = txtFormat.getText().trim();
        if (name.isEmpty() || category.isEmpty() || format.isEmpty()) {
            msg("Fields Name, Category and Format are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double size = 0;
        try {
            if (!txtSize.getText().trim().isEmpty())
                size = Double.parseDouble(txtSize.getText().trim());
        } catch (NumberFormatException ex) {
            msg("Size must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE); return;
        }
        Dataset d = new Dataset();
        d.setName(name); d.setCategory(category); d.setFormat(format);
        d.setSizeMb(size); d.setSourceUrl(txtUrl.getText().trim());
        d.setDescription(txtDesc.getText().trim());
        d.setStatus((String) cmbStatus.getSelectedItem());
        if (dao.addDataset(d)) {
            msg("Dataset added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadTable(dao.getAllDatasets()); clearForm();
        } else {
            msg("Failed to add dataset. Check DB connection.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDataset() {
        if (selectedId < 0) { msg("Select a dataset first.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
        double size = 0;
        try {
            if (!txtSize.getText().trim().isEmpty())
                size = Double.parseDouble(txtSize.getText().trim());
        } catch (NumberFormatException ex) {
            msg("Size must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE); return;
        }
        Dataset d = new Dataset();
        d.setId(selectedId); d.setName(txtName.getText().trim());
        d.setCategory(txtCategory.getText().trim()); d.setFormat(txtFormat.getText().trim());
        d.setSizeMb(size); d.setSourceUrl(txtUrl.getText().trim());
        d.setDescription(txtDesc.getText().trim());
        d.setStatus((String) cmbStatus.getSelectedItem());
        if (dao.updateDataset(d)) {
            msg("Dataset updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadTable(dao.getAllDatasets()); clearForm();
        } else {
            msg("Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDataset() {
        if (selectedId < 0) { msg("Select a dataset first.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
        int c = JOptionPane.showConfirmDialog(this, "Delete this dataset?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            dao.deleteDataset(selectedId);
            msg("Dataset deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            loadTable(dao.getAllDatasets()); clearForm();
        }
    }

    private void clearForm() {
        txtName.setText(""); txtCategory.setText(""); txtFormat.setText("");
        txtSize.setText(""); txtUrl.setText(""); txtDesc.setText("");
        txtSearch.setText(""); cmbStatus.setSelectedIndex(0);
        selectedId = -1; table.clearSelection();
    }

    // ── Style helpers ────────────────────────────────────────────────────────
    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(TXT_LABEL);
        return l;
    }

    private JTextField field() {
        JTextField f = new JTextField(16);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setForeground(TXT_LABEL);
        f.setBackground(BG_FIELD);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(5, 9, 5, 9)));
        return f;
    }

    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        // Hover effect
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(bg.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(bg);
            }
        });
        return b;
    }

    private void styleCombo(JComboBox<?> c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setBackground(BG_FIELD);
        c.setForeground(TXT_LABEL);
        c.setBorder(BorderFactory.createLineBorder(BORDER));
    }

    private void styleTable(JTable t) {
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setForeground(TXT_LABEL);
        t.setBackground(BG_CARD);
        t.setRowHeight(30);
        t.setGridColor(new Color(226, 232, 240));
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setSelectionBackground(new Color(219, 234, 254));
        t.setSelectionForeground(new Color(30, 64, 175));
        t.setIntercellSpacing(new Dimension(0, 0));

        // Alternating row colors
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if (sel) {
                    setBackground(new Color(219, 234, 254));
                    setForeground(new Color(30, 64, 175));
                } else {
                    setBackground(row % 2 == 0 ? BG_CARD : TBL_ALT);
                    setForeground(TXT_LABEL);
                }
                return this;
            }
        });

        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 12));
        h.setBackground(TBL_HDR);
        h.setForeground(Color.WHITE);
        h.setBorder(BorderFactory.createEmptyBorder());
        h.setPreferredSize(new Dimension(h.getWidth(), 36));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel l = new JLabel(val.toString());
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                l.setForeground(Color.WHITE);
                l.setBackground(TBL_HDR);
                l.setOpaque(true);
                l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return l;
            }
        });
        h.setReorderingAllowed(false);
    }

    private void msg(String text, String title, int type) {
        JOptionPane.showMessageDialog(this, text, title, type);
    }
}