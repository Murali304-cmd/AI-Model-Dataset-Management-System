package ui;

import dao.ModelDAO;
import model.AIModel;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ModelPanel extends JPanel {

    private ModelDAO dao = new ModelDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtName, txtVersion, txtAlgorithm, txtAccuracy, txtDatasetId;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private int selectedId = -1;

    private static final Color BG_PAGE   = new Color(248, 250, 252);
    private static final Color BG_CARD   = Color.WHITE;
    private static final Color BG_FIELD  = Color.WHITE;
    private static final Color BORDER    = new Color(203, 213, 225);
    private static final Color ACCENT    = new Color(109, 40, 217);
    private static final Color TXT_LABEL = new Color(51, 65, 85);
    private static final Color BTN_ADD   = new Color(22, 163, 74);
    private static final Color BTN_UPD   = new Color(234, 88, 12);
    private static final Color BTN_DEL   = new Color(220, 38, 38);
    private static final Color BTN_CLR   = new Color(100, 116, 139);
    private static final Color TBL_HDR   = new Color(109, 40, 217);
    private static final Color TBL_ALT   = new Color(248, 250, 252);

    public ModelPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(BG_PAGE);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(buildFormCard(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);
        loadTable();
    }

    private JPanel buildFormCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        JLabel cardTitle = new JLabel("AI Model Details");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cardTitle.setForeground(ACCENT);
        cardTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BG_CARD);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        txtName      = field(); txtVersion   = field();
        txtAlgorithm = field(); txtAccuracy  = field();
        txtDatasetId = field();

        g.gridy = 0; g.gridx = 0; form.add(lbl("Model Name *"), g);
        g.gridx = 1; form.add(txtName, g);
        g.gridx = 2; form.add(lbl("Version"), g);
        g.gridx = 3; form.add(txtVersion, g);

        g.gridy = 1; g.gridx = 0; form.add(lbl("Algorithm *"), g);
        g.gridx = 1; form.add(txtAlgorithm, g);
        g.gridx = 2; form.add(lbl("Accuracy (%)"), g);
        g.gridx = 3; form.add(txtAccuracy, g);

        g.gridy = 2; g.gridx = 0; form.add(lbl("Dataset ID *"), g);
        g.gridx = 1; form.add(txtDatasetId, g);

        JLabel hint = new JLabel("Enter an existing Dataset ID from the Datasets tab");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(new Color(148, 163, 184));
        g.gridx = 2; g.gridwidth = 2; form.add(hint, g);
        g.gridwidth = 1;

        btnAdd    = btn("Add Model", BTN_ADD);
        btnUpdate = btn("Update",    BTN_UPD);
        btnDelete = btn("Delete",    BTN_DEL);
        btnClear  = btn("Clear",     BTN_CLR);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnAdd); btnRow.add(btnUpdate);
        btnRow.add(btnDelete); btnRow.add(btnClear);

        g.gridy = 3; g.gridx = 0; g.gridwidth = 4;
        g.insets = new Insets(10, 6, 0, 6);
        form.add(btnRow, g);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> addModel());
        btnUpdate.addActionListener(e -> updateModel());
        btnDelete.addActionListener(e -> deleteModel());
        btnClear.addActionListener(e -> clearForm());

        return card;
    }

    private JPanel buildTableCard() {
        String[] cols = {"ID", "Model Name", "Version", "Algorithm", "Accuracy (%)", "Dataset ID"};
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

        JLabel cardTitle = new JLabel("AI Model Records");
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

    private void loadTable() {
        tableModel.setRowCount(0);
        for (AIModel m : dao.getAllModels())
            tableModel.addRow(new Object[]{
                m.getId(), m.getModelName(), m.getVersion(),
                m.getAlgorithm(), m.getAccuracy(), m.getDatasetId()
            });
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedId = (int) tableModel.getValueAt(row, 0);
        for (AIModel m : dao.getAllModels()) {
            if (m.getId() == selectedId) {
                txtName.setText(m.getModelName());
                txtVersion.setText(m.getVersion() == null ? "" : m.getVersion());
                txtAlgorithm.setText(m.getAlgorithm());
                txtAccuracy.setText(String.valueOf(m.getAccuracy()));
                txtDatasetId.setText(String.valueOf(m.getDatasetId()));
                break;
            }
        }
    }

    private void addModel() {
        String name = txtName.getText().trim();
        String algo = txtAlgorithm.getText().trim();
        String dsId = txtDatasetId.getText().trim();
        if (name.isEmpty() || algo.isEmpty() || dsId.isEmpty()) {
            msg("Model Name, Algorithm and Dataset ID are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int datasetId;
        try { datasetId = Integer.parseInt(dsId); }
        catch (NumberFormatException ex) {
            msg("Dataset ID must be a whole number.", "Input Error", JOptionPane.ERROR_MESSAGE); return;
        }
        double accuracy = 0;
        try {
            if (!txtAccuracy.getText().trim().isEmpty())
                accuracy = Double.parseDouble(txtAccuracy.getText().trim());
        } catch (NumberFormatException ex) {
            msg("Accuracy must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE); return;
        }
        AIModel m = new AIModel();
        m.setModelName(name); m.setVersion(txtVersion.getText().trim());
        m.setAlgorithm(algo); m.setAccuracy(accuracy); m.setDatasetId(datasetId);
        if (dao.addModel(m)) {
            msg("Model added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadTable(); clearForm();
        } else {
            msg("Failed. Make sure Dataset ID exists.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateModel() {
        if (selectedId < 0) { msg("Select a model first.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
        try {
            AIModel m = new AIModel();
            m.setId(selectedId); m.setModelName(txtName.getText().trim());
            m.setVersion(txtVersion.getText().trim()); m.setAlgorithm(txtAlgorithm.getText().trim());
            m.setAccuracy(txtAccuracy.getText().trim().isEmpty() ? 0
                : Double.parseDouble(txtAccuracy.getText().trim()));
            m.setDatasetId(Integer.parseInt(txtDatasetId.getText().trim()));
            if (dao.updateModel(m)) {
                msg("Model updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTable(); clearForm();
            }
        } catch (NumberFormatException ex) {
            msg("Check numeric fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteModel() {
        if (selectedId < 0) { msg("Select a model first.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
        int c = JOptionPane.showConfirmDialog(this, "Delete this model?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            dao.deleteModel(selectedId);
            msg("Model deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            loadTable(); clearForm();
        }
    }

    private void clearForm() {
        txtName.setText(""); txtVersion.setText(""); txtAlgorithm.setText("");
        txtAccuracy.setText(""); txtDatasetId.setText("");
        selectedId = -1; table.clearSelection();
    }

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
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false); b.setOpaque(true);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(bg.darker()); }
            public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    private void styleTable(JTable t) {
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setForeground(TXT_LABEL);
        t.setBackground(BG_CARD);
        t.setRowHeight(30);
        t.setGridColor(new Color(226, 232, 240));
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setSelectionBackground(new Color(237, 233, 254));
        t.setSelectionForeground(new Color(76, 29, 149));
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if (sel) {
                    setBackground(new Color(237, 233, 254));
                    setForeground(new Color(76, 29, 149));
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