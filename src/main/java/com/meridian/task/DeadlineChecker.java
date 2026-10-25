package com.meridian.task;

import com.meridian.dao.GoalDAO;
import jakarta.servlet.ServletContext;

public class DeadlineChecker implements Runnable {

    private final ServletContext context;
    private final GoalDAO goalDAO = new GoalDAO();

    public DeadlineChecker(ServletContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        try {
            int updated = goalDAO.markOverdueGoals();
            if (updated > 0) {
                context.log(updated + " goal(s) marked as overdue");
            }
        } catch (RuntimeException e) {
            context.log("Deadline check failed", e);
        }
    }
}
