package com.meridian.listener;

import com.meridian.dao.UserDAO;
import com.meridian.model.Admin;
import com.meridian.task.DeadlineChecker;
import com.meridian.util.PasswordUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class AppInitListener implements ServletContextListener {

    private static final String DEFAULT_ADMIN_EMAIL = "admin@meridian.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "Admin@123";

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            UserDAO dao = new UserDAO();
            if (!dao.existsAdmin()) {
                Admin admin = new Admin(0, "Meridian Admin", DEFAULT_ADMIN_EMAIL,
                        PasswordUtil.hash(DEFAULT_ADMIN_PASSWORD));
                dao.create(admin);
                sce.getServletContext().log("Default admin created: " + DEFAULT_ADMIN_EMAIL);
            }
        } catch (Exception e) {
            sce.getServletContext().log("Could not initialise default admin. Is MySQL running "
                    + "and schema.sql imported?", e);
        }

        scheduler = Executors.newSingleThreadScheduledExecutor(task -> {
            Thread thread = new Thread(task, "deadline-checker");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(new DeadlineChecker(sce.getServletContext()), 0, 10, TimeUnit.MINUTES);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }
}
