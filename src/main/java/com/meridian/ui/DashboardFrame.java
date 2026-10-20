package com.meridian.ui;

import com.meridian.dao.GoalDAO;
import com.meridian.dao.TaskDAO;
import com.meridian.model.Goal;
import com.meridian.model.Task;
import com.meridian.model.User;
import com.meridian.util.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Main workspace for Meridian. The screen stays intentionally simple so the
 * important things (goals, tasks, and progress) are easy to find.
 */
public class DashboardFrame extends JFrame {
    private static final Color NAVY = new Color(27, 39, 72);
    private static final Color TEAL = new Color(24, 139, 148);
    private static final Color PAGE = new Color(244, 247, 251);
    private static final Color INK = new Color(36, 48, 70);
    private final User user;
    private final GoalDAO goalDAO = new GoalDAO();
    private final TaskDAO taskDAO = new TaskDAO();
    private final JPanel content = new JPanel(new BorderLayout(16, 16));

    public DashboardFrame(User user) {
        this.user = user;
        setTitle("Meridian | " + (user.isAdmin() ? "Admin Panel" : "My Workspace"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setSize(1120, 720);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PAGE);
        JPanel sidebar = new JPanel();
        sidebar.setBackground(NAVY);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(24, 14, 22, 14));
        sidebar.setPreferredSize(new Dimension(220, 0));

        JLabel brand = new JLabel("MERIDIAN");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("SansSerif", Font.BOLD, 22));
        brand.setBorder(new EmptyBorder(0, 8, 8, 8));
        sidebar.add(brand);
        JLabel tagline = new JLabel("Make time count");
        tagline.setForeground(new Color(163, 215, 221));
        tagline.setBorder(new EmptyBorder(0, 8, 28, 8));
        sidebar.add(tagline);

        nav(sidebar, "Overview", this::showOverview);
        if (user.isAdmin()) {
            nav(sidebar, "Admin: Users", this::showUsers);
            nav(sidebar, "Admin: Reports", this::showReports);
        } else {
            nav(sidebar, "My Goals", this::showGoals);
            nav(sidebar, "My Tasks", this::showTasks);
            nav(sidebar, "Daily Planner", this::showPlanner);
        }
        sidebar.add(Box.createVerticalGlue());
        nav(sidebar, "Sign out", this::signOut);

        content.setBorder(new EmptyBorder(26, 28, 26, 28));
        content.setBackground(PAGE);
        root.add(sidebar, BorderLayout.WEST);
        root.add(content, BorderLayout.CENTER);
        setContentPane(root);
        showOverview();
    }

    private void nav(JPanel sidebar, String text, Runnable action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(192, 42));
        button.setPreferredSize(new Dimension(192, 42));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(43, 58, 96));
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 8));
        button.addActionListener(e -> action.run());
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(8));
    }

    private void heading(String title, String subtitle) {
        JPanel top = new JPanel(new BorderLayout(4, 6));
        top.setOpaque(false);
        JLabel h = new JLabel(title);
        h.setFont(new Font("SansSerif", Font.BOLD, 28));
        h.setForeground(NAVY);
        JLabel sub = new JLabel(subtitle);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setForeground(new Color(104, 116, 137));
        top.add(h, BorderLayout.NORTH);
        top.add(sub, BorderLayout.SOUTH);
        content.add(top, BorderLayout.NORTH);
    }

    private void showOverview() {
        content.removeAll();
        heading(user.isAdmin() ? "Admin overview" : "Welcome back, " + user.fullName(),
                user.isAdmin() ? "A quick look at activity across Meridian." : "A little progress every day adds up.");
        JPanel body = new JPanel(new BorderLayout(14, 14));
        body.setOpaque(false);
        JPanel cards = new JPanel(new GridLayout(1, 3, 12, 12));
        cards.setOpaque(false);
        if (user.isAdmin()) {
            cards.add(statCard("Registered users", count("SELECT COUNT(*) FROM users")));
            cards.add(statCard("Goals created", count("SELECT COUNT(*) FROM goals")));
            cards.add(statCard("Tasks completed", count("SELECT COUNT(*) FROM tasks WHERE status='COMPLETED'")));
        } else {
            cards.add(statCard("My goals", count("SELECT COUNT(*) FROM goals WHERE user_id=?", user.id())));
            cards.add(statCard("Open tasks", count("SELECT COUNT(*) FROM tasks WHERE user_id=? AND status <> 'COMPLETED'", user.id())));
            cards.add(statCard("Completed tasks", count("SELECT COUNT(*) FROM tasks WHERE user_id=? AND status='COMPLETED'", user.id())));
        }

        JPanel lower = new JPanel(new GridLayout(1, 2, 14, 14));
        lower.setOpaque(false);
        if (user.isAdmin()) {
            lower.add(noteCard("Administrator tools", "Use Admin: Users to review registered accounts and Admin: Reports to see a simple system summary."));
            lower.add(noteCard("A quick reminder", "Account management should be handled carefully. Never share database credentials or user information."));
        } else {
            lower.add(buildTaskChart());
            lower.add(buildGoalChart());
        }
        body.add(cards, BorderLayout.NORTH);
        body.add(lower, BorderLayout.CENTER);
        content.add(body, BorderLayout.CENTER);
        refresh();
    }

    private JPanel statCard(String label, String value) {
        JPanel card = new JPanel(new BorderLayout(8, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 230, 239)),
                new EmptyBorder(18, 18, 18, 18)));
        JLabel number = new JLabel(value);
        number.setFont(new Font("SansSerif", Font.BOLD, 30));
        number.setForeground(TEAL);
        JLabel caption = new JLabel(label);
        caption.setForeground(new Color(101, 113, 134));
        card.add(number, BorderLayout.CENTER);
        card.add(caption, BorderLayout.SOUTH);
        return card;
    }

    private JPanel noteCard(String title, String text) {
        JPanel panel = new JPanel(new BorderLayout(8, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        JTextArea area = new JTextArea(text);
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setEditable(false);
        area.setOpaque(false);
        area.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(label, BorderLayout.NORTH);
        panel.add(area, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildTaskChart() {
        int pending = intCount("SELECT COUNT(*) FROM tasks WHERE user_id=? AND status='PENDING'", user.id());
        int progress = intCount("SELECT COUNT(*) FROM tasks WHERE user_id=? AND status='IN_PROGRESS'", user.id());
        int done = intCount("SELECT COUNT(*) FROM tasks WHERE user_id=? AND status='COMPLETED'", user.id());
        JPanel panel = chartCard("Task status", new PieChart(pending, progress, done));
        return panel;
    }

    private JPanel buildGoalChart() {
        int notStarted = intCount("SELECT COUNT(*) FROM goals WHERE user_id=? AND status='NOT_STARTED'", user.id());
        int inProgress = intCount("SELECT COUNT(*) FROM goals WHERE user_id=? AND status='IN_PROGRESS'", user.id());
        int completed = intCount("SELECT COUNT(*) FROM goals WHERE user_id=? AND status='COMPLETED'", user.id());
        JPanel panel = chartCard("Goal progress", new BarChart(notStarted, inProgress, completed));
        return panel;
    }

    private JPanel chartCard(String title, JComponent chart) {
        JPanel panel = new JPanel(new BorderLayout(6, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 17));
        panel.add(label, BorderLayout.NORTH);
        panel.add(chart, BorderLayout.CENTER);
        return panel;
    }

    private static class PieChart extends JPanel {
        private final int[] values;
        private final String[] labels = {"Pending", "In progress", "Completed"};
        private final Color[] colors = {new Color(239, 177, 74), new Color(82, 142, 222), new Color(49, 166, 126)};
        PieChart(int a, int b, int c) { values = new int[]{a, b, c}; setOpaque(false); }
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int total = values[0] + values[1] + values[2];
            int diameter = Math.min(130, Math.max(80, getHeight() - 28));
            int x = 8, y = Math.max(4, (getHeight() - diameter) / 2);
            if (total == 0) {
                g.setColor(new Color(232, 237, 244)); g.fillOval(x, y, diameter, diameter);
                g.setColor(new Color(105, 116, 135)); g.drawString("No tasks yet", x + 20, y + diameter / 2);
            } else {
                int start = 90;
                for (int i = 0; i < values.length; i++) {
                    int arc = (int) Math.round(360.0 * values[i] / total);
                    g.setColor(colors[i]); g.fillArc(x, y, diameter, diameter, start, -arc); start -= arc;
                }
            }
            int ly = 30;
            for (int i = 0; i < values.length; i++) {
                int lx = diameter + 28;
                g.setColor(colors[i]); g.fillRoundRect(lx, ly - 10, 10, 10, 3, 3);
                g.setColor(INK); g.drawString(labels[i] + "  " + values[i], lx + 16, ly);
                ly += 28;
            }
            g.dispose();
        }
    }

    private static class BarChart extends JPanel {
        private final int[] values;
        private final String[] labels = {"Not started", "In progress", "Completed"};
        private final Color[] colors = {new Color(239, 177, 74), new Color(82, 142, 222), new Color(49, 166, 126)};
        BarChart(int a, int b, int c) { values = new int[]{a, b, c}; setOpaque(false); }
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int max = Math.max(1, Math.max(values[0], Math.max(values[1], values[2])));
            int chartW = Math.max(100, getWidth() - 34);
            int baseY = getHeight() - 28;
            int barW = Math.max(18, (chartW - 44) / 3);
            for (int i = 0; i < 3; i++) {
                int h = (int) ((getHeight() - 62.0) * values[i] / max);
                int x = 12 + i * (barW + 14);
                g.setColor(colors[i]); g.fillRoundRect(x, baseY - h, barW, h, 8, 8);
                g.setColor(INK); g.drawString(Integer.toString(values[i]), x + 4, baseY - h - 5);
                g.setColor(new Color(100, 112, 133)); g.drawString(new String[]{"Start","Active","Done"}[i], x - 2, baseY + 16);
            }
            g.dispose();
        }
    }

    private void showGoals() {
        content.removeAll();
        heading("My goals", "Set a target, keep it realistic, and update it as you go.");
        DefaultTableModel model = model("ID", "Goal", "Start date", "Target date", "Status");
        try {
            for (Goal goal : goalDAO.findByUser(user.id()))
                model.addRow(new Object[]{goal.id(), goal.title(), goal.startDate(), goal.targetDate(), goal.status()});
        } catch (SQLException ex) { showError("Could not load your goals", ex); }
        JTable table = table(model);
        JButton add = actionButton("Add goal", e -> addGoal());
        JButton status = actionButton("Update status", e -> updateGoalStatus(table));
        JButton delete = actionButton("Delete selected", e -> deleteGoal(table));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false); actions.add(add); actions.add(status); actions.add(delete);
        JPanel panel = new JPanel(new BorderLayout(10, 12)); panel.setOpaque(false);
        panel.add(new JScrollPane(table), BorderLayout.CENTER); panel.add(actions, BorderLayout.SOUTH);
        content.add(panel, BorderLayout.CENTER); refresh();
    }

    private void addGoal() {
        JTextField title = new JTextField(24);
        JTextArea description = new JTextArea(3, 24);
        JTextField start = new JTextField(LocalDate.now().toString(), 12);
        JTextField target = new JTextField(LocalDate.now().plusWeeks(2).toString(), 12);
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Goal title")); form.add(title);
        form.add(new JLabel("Description")); form.add(new JScrollPane(description));
        form.add(new JLabel("Start date (YYYY-MM-DD)")); form.add(start);
        form.add(new JLabel("Target date (YYYY-MM-DD)")); form.add(target);
        if (JOptionPane.showConfirmDialog(this, form, "Add a goal", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try {
            goalDAO.create(user.id(), title.getText(), description.getText(),
                    LocalDate.parse(start.getText().trim()), LocalDate.parse(target.getText().trim()));
            showGoals();
        } catch (IllegalArgumentException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Check the details", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) { showError("Could not save the goal", ex); }
    }

    private void updateGoalStatus(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { notice("Select a goal first."); return; }
        String[] options = {"NOT_STARTED", "IN_PROGRESS", "COMPLETED", "ON_HOLD"};
        String status = (String) JOptionPane.showInputDialog(this, "Choose the new status:", "Goal status",
                JOptionPane.PLAIN_MESSAGE, null, options, table.getValueAt(row, 4));
        if (status == null) return;
        try { goalDAO.updateStatus(user.id(), ((Number)table.getValueAt(row,0)).longValue(), status); showGoals(); }
        catch (SQLException | IllegalArgumentException ex) { showError("Could not update the goal", ex); }
    }

    private void deleteGoal(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { notice("Select a goal first."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this goal? Related tasks will remain but lose their goal link.",
                "Confirm delete", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { goalDAO.delete(user.id(), ((Number)table.getValueAt(row,0)).longValue()); showGoals(); }
        catch (SQLException ex) { showError("Could not delete the goal", ex); }
    }

    private void showTasks() {
        content.removeAll();
        heading("My tasks", "Choose priorities, keep deadlines visible, and mark work done.");
        DefaultTableModel model = model("ID", "Task", "Due date", "Priority", "Status", "Goal ID");
        try {
            List<Task> tasks = taskDAO.findByUser(user.id());
            for (Task task : tasks) model.addRow(new Object[]{task.id(), task.title(), task.dueDate(),
                    task.priority(), task.status(), task.goalId() == null ? "—" : task.goalId()});
        } catch (SQLException ex) { showError("Could not load your tasks", ex); }
        JTable table = table(model);
        JButton add = actionButton("Add task", e -> addTask());
        JButton status = actionButton("Update status", e -> updateTaskStatus(table));
        JButton delete = actionButton("Delete selected", e -> deleteTask(table));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false); actions.add(add); actions.add(status); actions.add(delete);
        JPanel panel = new JPanel(new BorderLayout(10, 12)); panel.setOpaque(false);
        panel.add(new JScrollPane(table), BorderLayout.CENTER); panel.add(actions, BorderLayout.SOUTH);
        content.add(panel, BorderLayout.CENTER); refresh();
    }

    private void addTask() {
        JTextField title = new JTextField(24);
        JTextArea description = new JTextArea(3, 24);
        JTextField due = new JTextField(LocalDate.now().plusDays(1).toString(), 12);
        JTextField goalId = new JTextField(8);
        String[] priorities = {"LOW", "MEDIUM", "HIGH"};
        JComboBox<String> priority = new JComboBox<>(priorities);
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Task title")); form.add(title);
        form.add(new JLabel("Description")); form.add(new JScrollPane(description));
        form.add(new JLabel("Due date (YYYY-MM-DD, optional)")); form.add(due);
        form.add(new JLabel("Related goal ID (optional)")); form.add(goalId);
        form.add(new JLabel("Priority")); form.add(priority);
        if (JOptionPane.showConfirmDialog(this, form, "Add a task", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try {
            String dueText = due.getText().trim();
            String goalText = goalId.getText().trim();
            LocalDate date = dueText.isEmpty() ? null : LocalDate.parse(dueText);
            Long linkedGoal = goalText.isEmpty() ? null : Long.parseLong(goalText);
            taskDAO.create(user.id(), linkedGoal, title.getText(), description.getText(), date, (String)priority.getSelectedItem());
            showTasks();
        } catch (IllegalArgumentException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Check the title, date, and goal ID. " + ex.getMessage(),
                    "Check the details", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) { showError("Could not save the task", ex); }
    }

    private void updateTaskStatus(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { notice("Select a task first."); return; }
        String[] options = {"PENDING", "IN_PROGRESS", "COMPLETED"};
        String status = (String) JOptionPane.showInputDialog(this, "Choose the new status:", "Task status",
                JOptionPane.PLAIN_MESSAGE, null, options, table.getValueAt(row, 4));
        if (status == null) return;
        try { taskDAO.updateStatus(user.id(), ((Number)table.getValueAt(row,0)).longValue(), status); showTasks(); }
        catch (SQLException | IllegalArgumentException ex) { showError("Could not update the task", ex); }
    }

    private void deleteTask(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { notice("Select a task first."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this task?", "Confirm delete", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { taskDAO.delete(user.id(), ((Number)table.getValueAt(row,0)).longValue()); showTasks(); }
        catch (SQLException ex) { showError("Could not delete the task", ex); }
    }

    private void showPlanner() {
        content.removeAll();
        heading("Daily planner", "Your tasks due today and overdue tasks appear here.");
        DefaultTableModel model = model("ID", "Task", "Due date", "Priority", "Status");
        String sql = "SELECT task_id,title,due_date,priority,status FROM tasks WHERE user_id=? AND status<>'COMPLETED' AND (due_date IS NULL OR due_date<=?) ORDER BY due_date IS NULL,due_date";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, user.id()); p.setDate(2, Date.valueOf(LocalDate.now()));
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) model.addRow(new Object[]{r.getLong(1), r.getString(2), r.getDate(3), r.getString(4), r.getString(5)});
            }
        } catch (SQLException ex) { showError("Could not load the planner", ex); }
        JTable table = table(model);
        JButton update = actionButton("Update selected task", e -> updateTaskStatus(table));
        JPanel panel = new JPanel(new BorderLayout(8, 10)); panel.setOpaque(false);
        panel.add(new JScrollPane(table), BorderLayout.CENTER); panel.add(update, BorderLayout.SOUTH);
        content.add(panel, BorderLayout.CENTER); refresh();
    }

    private void showUsers() {
        if (!user.isAdmin()) { notice("Administrator access required."); return; }
        content.removeAll(); heading("User management", "A simple overview of registered Meridian accounts.");
        DefaultTableModel model = model("ID", "Name", "Email", "Role", "Status", "Created");
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement("SELECT user_id,full_name,email,role,account_status,created_at FROM users ORDER BY created_at DESC");
             ResultSet r = p.executeQuery()) {
            while (r.next()) model.addRow(new Object[]{r.getLong(1), r.getString(2), r.getString(3),
                    r.getString(4), r.getString(5), r.getTimestamp(6)});
        } catch (SQLException ex) { showError("Could not load users", ex); }
        JTable table = table(model);
        JButton reload = actionButton("Refresh", e -> showUsers());
        JPanel panel = new JPanel(new BorderLayout(8, 10)); panel.setOpaque(false);
        panel.add(new JScrollPane(table), BorderLayout.CENTER); panel.add(reload, BorderLayout.SOUTH);
        content.add(panel, BorderLayout.CENTER); refresh();
    }

    private void showReports() {
        if (!user.isAdmin()) { notice("Administrator access required."); return; }
        content.removeAll(); heading("System reports", "A quick summary of activity in the database.");
        String report = "MERIDIAN SYSTEM SUMMARY\n\n"
                + "Registered accounts: " + count("SELECT COUNT(*) FROM users")
                + "\nActive accounts: " + count("SELECT COUNT(*) FROM users WHERE account_status='ACTIVE'")
                + "\nTotal goals: " + count("SELECT COUNT(*) FROM goals")
                + "\nCompleted goals: " + count("SELECT COUNT(*) FROM goals WHERE status='COMPLETED'")
                + "\nTotal tasks: " + count("SELECT COUNT(*) FROM tasks")
                + "\nCompleted tasks: " + count("SELECT COUNT(*) FROM tasks WHERE status='COMPLETED'")
                + "\nOpen tasks: " + count("SELECT COUNT(*) FROM tasks WHERE status<>'COMPLETED'");
        JTextArea area = new JTextArea(report); area.setEditable(false); area.setFont(new Font("Monospaced", Font.PLAIN, 16));
        area.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.add(new JScrollPane(area), BorderLayout.CENTER); refresh();
    }

    private DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private JTable table(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        return table;
    }

    private JButton actionButton(String label, java.awt.event.ActionListener action) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setBackground(TEAL);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        button.addActionListener(action);
        return button;
    }

    private String count(String sql, Object... parameters) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) p.setObject(i + 1, parameters[i]);
            try (ResultSet r = p.executeQuery()) { return r.next() ? r.getString(1) : "0"; }
        } catch (SQLException ex) { return "—"; }
    }

    private int intCount(String sql, Object... parameters) {
        try { return Integer.parseInt(count(sql, parameters)); }
        catch (NumberFormatException ex) { return 0; }
    }

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(this, message + ". " + ex.getMessage(), "Meridian", JOptionPane.ERROR_MESSAGE);
    }

    private void notice(String message) {
        JOptionPane.showMessageDialog(this, message, "Meridian", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refresh() {
        content.revalidate();
        content.repaint();
    }

    private void signOut() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}
