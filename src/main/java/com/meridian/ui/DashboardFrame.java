package com.meridian.ui;
import com.meridian.model.User;
import com.meridian.util.DatabaseConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

/** Initial role-aware dashboard; admin controls are shown only to ADMIN accounts. */
public class DashboardFrame extends JFrame {
    private final User user;
    private final JPanel content = new JPanel(new BorderLayout(12,12));
    public DashboardFrame(User user) {
        this.user=user; setTitle("Meridian | "+(user.isAdmin()?"Admin Panel":"Dashboard"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setMinimumSize(new Dimension(800,520)); setSize(980,640); setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout()); root.setBackground(new Color(245,247,251));
        JPanel sidebar = new JPanel(); sidebar.setBackground(new Color(35,48,83)); sidebar.setLayout(new BoxLayout(sidebar,BoxLayout.Y_AXIS)); sidebar.setBorder(new EmptyBorder(22,14,22,14)); sidebar.setPreferredSize(new Dimension(205,0));
        JLabel brand = new JLabel("MERIDIAN"); brand.setForeground(Color.WHITE); brand.setFont(new Font("SansSerif",Font.BOLD,20)); brand.setBorder(new EmptyBorder(0,8,22,0)); sidebar.add(brand);
        addNavButton(sidebar,"Overview",this::showOverview);
        if (user.isAdmin()) { addNavButton(sidebar,"Admin: User Management",this::showUsers); addNavButton(sidebar,"Admin: System Reports",this::showReports); }
        else { addNavButton(sidebar,"My Goals",()->placeholder("Goal management")); addNavButton(sidebar,"My Tasks",()->placeholder("Task management")); addNavButton(sidebar,"Daily Planner",()->placeholder("Daily planner")); }
        sidebar.add(Box.createVerticalGlue()); addNavButton(sidebar,"Sign out",this::signOut);
        content.setBorder(new EmptyBorder(24,28,24,28)); content.setBackground(new Color(245,247,251)); root.add(sidebar,BorderLayout.WEST); root.add(content,BorderLayout.CENTER); setContentPane(root); showOverview();
    }
    private void addNavButton(JPanel sidebar,String text,Runnable action) {
        JButton button=new JButton(text); button.setAlignmentX(Component.LEFT_ALIGNMENT); button.setMaximumSize(new Dimension(200,40)); button.setFocusPainted(false); button.addActionListener(e->action.run()); sidebar.add(button); sidebar.add(Box.createVerticalStrut(8));
    }
    private void showOverview() {
        content.removeAll(); JLabel heading=new JLabel(user.isAdmin()?"Administrator Overview":"Your Overview"); heading.setFont(new Font("SansSerif",Font.BOLD,26));
        JLabel greeting=new JLabel("Welcome, "+user.fullName()+"  |  Role: "+user.role()); greeting.setForeground(new Color(83,91,111));
        JPanel top=new JPanel(new GridLayout(2,1,4,4)); top.setOpaque(false); top.add(heading); top.add(greeting);
        JPanel cards=new JPanel(new GridLayout(1,3,14,14)); cards.setOpaque(false);
        if(user.isAdmin()){cards.add(statCard("Registered users",count("SELECT COUNT(*) FROM users")));cards.add(statCard("Active goals",count("SELECT COUNT(*) FROM goals WHERE status <> 'COMPLETED'")));cards.add(statCard("Tasks completed",count("SELECT COUNT(*) FROM tasks WHERE status='COMPLETED'")));}
        else {cards.add(statCard("My goals",count("SELECT COUNT(*) FROM goals WHERE user_id=?",user.id())));cards.add(statCard("Open tasks",count("SELECT COUNT(*) FROM tasks WHERE user_id=? AND status <> 'COMPLETED'",user.id())));cards.add(statCard("Completed tasks",count("SELECT COUNT(*) FROM tasks WHERE user_id=? AND status='COMPLETED'",user.id())));}
        JTextArea info=new JTextArea(user.isAdmin()?"ADMIN PANEL\nUse administration links to inspect registered accounts and system-wide activity.":"YOUR WORKSPACE\nUse navigation to organize goals, plan tasks, and monitor your progress.");
        info.setEditable(false); info.setLineWrap(true); info.setWrapStyleWord(true); info.setFont(new Font("SansSerif",Font.PLAIN,15)); info.setBackground(Color.WHITE); info.setBorder(new EmptyBorder(18,18,18,18));
        content.add(top,BorderLayout.NORTH); content.add(cards,BorderLayout.CENTER); content.add(new JScrollPane(info),BorderLayout.SOUTH); content.revalidate(); content.repaint();
    }
    private JPanel statCard(String label,String value) {
        JPanel card=new JPanel(new BorderLayout(6,10)); card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(225,229,238)),new EmptyBorder(18,16,18,16)));
        JLabel number=new JLabel(value); number.setFont(new Font("SansSerif",Font.BOLD,27)); number.setForeground(new Color(49,75,140));
        JLabel caption=new JLabel(label); caption.setForeground(new Color(83,91,111)); card.add(number,BorderLayout.CENTER); card.add(caption,BorderLayout.SOUTH); return card;
    }
    private String count(String sql,Object... parameters) {
        try(Connection connection=DatabaseConnection.getConnection();PreparedStatement ps=connection.prepareStatement(sql)){
            for(int i=0;i<parameters.length;i++)ps.setObject(i+1,parameters[i]);
            try(ResultSet rs=ps.executeQuery()){return rs.next()?rs.getString(1):"0";}
        }catch(SQLException ex){return "—";}
    }
    private void showUsers() {
        if(!user.isAdmin()){JOptionPane.showMessageDialog(this,"Administrator access required.");return;}
        content.removeAll(); JLabel heading=new JLabel("User Management"); heading.setFont(new Font("SansSerif",Font.BOLD,25));
        String[] columns={"ID","Name","Email","Role","Status","Created"};
        javax.swing.table.DefaultTableModel model=new javax.swing.table.DefaultTableModel(columns,0){@Override public boolean isCellEditable(int row,int column){return false;}};
        JTable table=new JTable(model);
        try(Connection connection=DatabaseConnection.getConnection();PreparedStatement ps=connection.prepareStatement("SELECT user_id,full_name,email,role,account_status,created_at FROM users ORDER BY created_at DESC");ResultSet rs=ps.executeQuery()){
            while(rs.next())model.addRow(new Object[]{rs.getLong(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getTimestamp(6)});
        }catch(SQLException ex){JOptionPane.showMessageDialog(this,"Unable to load users: "+ex.getMessage());}
        JButton refresh=new JButton("Refresh"); refresh.addActionListener(e->showUsers());
        JPanel panel=new JPanel(new BorderLayout(8,8));panel.setOpaque(false);panel.add(heading,BorderLayout.NORTH);panel.add(new JScrollPane(table),BorderLayout.CENTER);panel.add(refresh,BorderLayout.SOUTH);
        content.add(panel,BorderLayout.CENTER);content.revalidate();content.repaint();
    }
    private void showReports() {
        if(!user.isAdmin()){JOptionPane.showMessageDialog(this,"Administrator access required.");return;}
        content.removeAll(); JLabel heading=new JLabel("System Reports");heading.setFont(new Font("SansSerif",Font.BOLD,25));
        JTextArea report=new JTextArea();report.setEditable(false);report.setFont(new Font("Monospaced",Font.PLAIN,15));
        report.setText("MERIDIAN SYSTEM REPORT\n\nTotal accounts: "+count("SELECT COUNT(*) FROM users")
          +"\nActive accounts: "+count("SELECT COUNT(*) FROM users WHERE account_status='ACTIVE'")
          +"\nTotal goals: "+count("SELECT COUNT(*) FROM goals")+"\nCompleted goals: "+count("SELECT COUNT(*) FROM goals WHERE status='COMPLETED'")
          +"\nTotal tasks: "+count("SELECT COUNT(*) FROM tasks")+"\nCompleted tasks: "+count("SELECT COUNT(*) FROM tasks WHERE status='COMPLETED'")+"\n");
        content.add(heading,BorderLayout.NORTH);content.add(new JScrollPane(report),BorderLayout.CENTER);content.revalidate();content.repaint();
    }
    private void placeholder(String feature){JOptionPane.showMessageDialog(this,feature+" is the next implementation stage.","Coming next",JOptionPane.INFORMATION_MESSAGE);}
    private void signOut(){dispose();new LoginFrame().setVisible(true);}
}
