package com.meridian.ui;

import com.meridian.dao.GoalDAO;
import com.meridian.dao.TimeEntryDAO;
import com.meridian.model.Goal;
import com.meridian.model.TimeEntry;
import com.meridian.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Simple form and history for recording time spent on personal goals. */
public class TimeTrackingFrame extends JFrame {
    private final User user;
    private final TimeEntryDAO entries = new TimeEntryDAO();
    private final GoalDAO goals = new GoalDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Goal", "Start", "End", "Minutes", "Notes"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel total = new JLabel("Total tracked: 0 minutes");
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TimeTrackingFrame(User user) {
        this.user = user;
        setTitle("Meridian | Time Tracking");
        setSize(850, 520);
        setMinimumSize(new Dimension(700, 420));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(14, 16, 4, 16));
        JLabel title = new JLabel("Time towards goals");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        top.add(title, BorderLayout.NORTH);
        top.add(new JLabel("Record start and end times to see how much time you invest in each goal."), BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        table.setRowHeight(26);
        add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setBorder(BorderFactory.createEmptyBorder(4, 16, 14, 16));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Log time");
        JButton delete = new JButton("Delete selected");
        JButton refresh = new JButton("Refresh");
        add.addActionListener(e -> addEntry());
        delete.addActionListener(e -> deleteEntry());
        refresh.addActionListener(e -> loadEntries());
        buttons.add(add); buttons.add(delete); buttons.add(refresh);
        bottom.add(buttons, BorderLayout.NORTH);
        bottom.add(total, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
        loadEntries();
    }

    private void addEntry() {
        try {
            List<Goal> available = goals.findByUser(user.id());
            if (available.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Create a goal first, then log time against it.");
                return;
            }
            JComboBox<Goal> goalBox = new JComboBox<>(available.toArray(new Goal[0]));
            goalBox.setRenderer(new DefaultListCellRenderer() {
                @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                    super.getListCellRendererComponent(list, value, index, selected, focus);
                    if (value instanceof Goal g) setText(g.title());
                    return this;
                }
            });
            JTextField start = new JTextField(LocalDateTime.now().minusHours(1).format(FORMAT), 18);
            JTextField end = new JTextField(LocalDateTime.now().format(FORMAT), 18);
            JTextField notes = new JTextField(22);
            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.add(new JLabel("Goal")); form.add(goalBox);
            form.add(new JLabel("Start (yyyy-MM-dd HH:mm)")); form.add(start);
            form.add(new JLabel("End (yyyy-MM-dd HH:mm)")); form.add(end);
            form.add(new JLabel("Notes")); form.add(notes);
            if (JOptionPane.showConfirmDialog(this, form, "Log time", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
            Goal goal = (Goal) goalBox.getSelectedItem();
            if (goal == null) return;
            entries.create(user.id(), goal.id(), LocalDateTime.parse(start.getText().trim(), FORMAT),
                    LocalDateTime.parse(end.getText().trim(), FORMAT), notes.getText());
            loadEntries();
            JOptionPane.showMessageDialog(this, "Time entry saved.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Check the time entry", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not save time entry: " + ex.getMessage(), "Time tracking", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEntry() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a time entry first."); return; }
        try {
            entries.delete(user.id(), ((Number) model.getValueAt(row, 0)).longValue());
            loadEntries();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not delete entry: " + ex.getMessage());
        }
    }

    private void loadEntries() {
        model.setRowCount(0);
        try {
            for (TimeEntry entry : entries.findByUser(user.id()))
                model.addRow(new Object[]{entry.id(), entry.goalTitle(), entry.startTime().format(FORMAT),
                        entry.endTime().format(FORMAT), entry.minutes(), entry.notes()});
            long minutes = entries.totalMinutes(user.id());
            total.setText("Total tracked: " + (minutes / 60) + " hours " + (minutes % 60) + " minutes");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load time entries. Ensure the updated schema is installed. " + ex.getMessage());
        }
    }
}
