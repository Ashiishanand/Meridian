package com.meridian;
import com.meridian.ui.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
public final class Main {
    private Main() { }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) { /* Keep Swing's default look and feel. */ }
            new LoginFrame().setVisible(true);
        });
    }
}
