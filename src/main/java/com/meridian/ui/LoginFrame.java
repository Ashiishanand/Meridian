package com.meridian.ui;
import com.meridian.model.User;
import com.meridian.service.AuthService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/** Login and account creation screen. */
public class LoginFrame extends JFrame {
    private final AuthService authService = new AuthService();
    private final JTextField emailField = new JTextField(22);
    private final JPasswordField passwordField = new JPasswordField(22);
    public LoginFrame() {
        setTitle("Meridian | Time Management & Goal Setting");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setSize(520, 430); setLocationRelativeTo(null); setResizable(false);
        JPanel root = new JPanel(new BorderLayout(12, 12)); root.setBorder(new EmptyBorder(28, 34, 28, 34)); root.setBackground(new Color(245,247,251));
        JLabel title = new JLabel("MERIDIAN", SwingConstants.CENTER); title.setFont(new Font("SansSerif", Font.BOLD, 28)); title.setForeground(new Color(40,61,115));
        JLabel subtitle = new JLabel("Make time for what matters.", SwingConstants.CENTER);
        JPanel header = new JPanel(new GridLayout(2,1,4,4)); header.setOpaque(false); header.add(title); header.add(subtitle); root.add(header, BorderLayout.NORTH);
        JPanel form = new JPanel(new GridBagLayout()); form.setOpaque(false); GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(8,4,8,4); c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx=0; c.gridy=0; form.add(new JLabel("Email address"),c); c.gridx=1; form.add(emailField,c);
        c.gridx=0; c.gridy=1; form.add(new JLabel("Password"),c); c.gridx=1; form.add(passwordField,c);
        JButton login = new JButton("Sign in"); JButton register = new JButton("Create account"); login.setBackground(new Color(49,75,140)); login.setForeground(Color.WHITE);
        login.addActionListener(e -> login()); register.addActionListener(e -> register());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER,12,8)); actions.setOpaque(false); actions.add(login); actions.add(register);
        JPanel center = new JPanel(new BorderLayout(8,18)); center.setOpaque(false); center.add(form,BorderLayout.CENTER); center.add(actions,BorderLayout.SOUTH); root.add(center,BorderLayout.CENTER);
        JLabel note = new JLabel("<html><center>First account created becomes the administrator.<br>Use a unique password with at least 8 characters.</center></html>",SwingConstants.CENTER);
        note.setForeground(new Color(85,92,110)); root.add(note,BorderLayout.SOUTH); setContentPane(root); getRootPane().setDefaultButton(login);
    }
    private void login() {
        char[] password = passwordField.getPassword();
        try {
            User user = authService.login(emailField.getText(), password);
            if (user == null) { JOptionPane.showMessageDialog(this,"Email or password is incorrect.","Sign-in failed",JOptionPane.WARNING_MESSAGE); return; }
            dispose(); new DashboardFrame(user).setVisible(true);
        } catch (SQLException ex) { showDatabaseError(ex); }
        catch (IllegalArgumentException ex) { JOptionPane.showMessageDialog(this,ex.getMessage(),"Sign-in failed",JOptionPane.WARNING_MESSAGE); }
        finally { java.util.Arrays.fill(password,'\0'); passwordField.setText(""); }
    }
    private void register() {
        JTextField nameField = new JTextField(20); JTextField email = new JTextField(emailField.getText(),20); JPasswordField password = new JPasswordField(20);
        JPanel panel = new JPanel(new GridLayout(0,1,4,4)); panel.add(new JLabel("Full name")); panel.add(nameField); panel.add(new JLabel("Email")); panel.add(email); panel.add(new JLabel("Password (at least 8 characters)")); panel.add(password);
        if (JOptionPane.showConfirmDialog(this,panel,"Create Meridian account",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        char[] secret = password.getPassword();
        try {
            User user = authService.register(nameField.getText(),email.getText(),secret);
            JOptionPane.showMessageDialog(this,"Account created. Assigned role: "+user.role()+". Please sign in.","Welcome to Meridian",JOptionPane.INFORMATION_MESSAGE);
            emailField.setText(user.email());
        } catch (SQLException ex) {
            String message = ex.getErrorCode()==1062 ? "An account with this email already exists." : "Could not create account. Check your database connection and schema.";
            JOptionPane.showMessageDialog(this,message,"Registration failed",JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) { JOptionPane.showMessageDialog(this,ex.getMessage(),"Registration failed",JOptionPane.WARNING_MESSAGE); }
        finally { java.util.Arrays.fill(secret,'\0'); password.setText(""); }
    }
    private void showDatabaseError(SQLException ex) {
        JOptionPane.showMessageDialog(this,"Could not connect to MySQL. Run database/schema.sql in MySQL Workbench and check database credentials.\n\nDetails: "+ex.getMessage(),"Database connection error",JOptionPane.ERROR_MESSAGE);
    }
}
