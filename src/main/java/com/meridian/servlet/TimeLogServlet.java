package com.meridian.servlet;

import com.meridian.dao.GoalDAO;
import com.meridian.dao.TimeEntryDAO;
import com.meridian.dao.UsageDAO;
import com.meridian.exception.DataAccessException;
import com.meridian.exception.InvalidTimeEntryException;
import com.meridian.model.User;
import com.meridian.service.TimeEntryService;
import com.meridian.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/user/time-logs")
public class TimeLogServlet extends HttpServlet {

    private final GoalDAO goalDAO = new GoalDAO();
    private final TimeEntryDAO timeEntryDAO = new TimeEntryDAO();
    private final TimeEntryService timeEntryService = new TimeEntryService();
    private final UsageDAO usageDAO = new UsageDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        try {
            req.setAttribute("entries", timeEntryDAO.findByUser(user.getId()));
            req.setAttribute("openGoals", goalDAO.findOpenByUser(user.getId()));
        } catch (DataAccessException e) {
            log("Could not load time logs", e);
            req.setAttribute("error", "Could not load your time logs.");
        }
        req.getRequestDispatcher("/WEB-INF/views/user/time-logs.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        HttpSession session = req.getSession();
        String action = req.getParameter("action");
        try {
            if ("add".equals(action)) {
                boolean completed = timeEntryService.logTime(user.getId(), req.getParameter("goalId"),
                        req.getParameter("start"), req.getParameter("end"), req.getParameter("notes"));
                usageDAO.log(user.getId(), "TIME_LOGGED");
                session.setAttribute("userMsg", completed
                        ? "Time logged and your goal is now complete. Well done!"
                        : "Time entry saved.");
            } else if ("delete".equals(action)) {
                int id = ValidationUtil.parseId(req.getParameter("id"));
                if (id > 0 && timeEntryDAO.delete(id, user.getId())) {
                    session.setAttribute("userMsg", "Time entry deleted.");
                } else {
                    session.setAttribute("userError", "Time entry not found.");
                }
            }
        } catch (InvalidTimeEntryException e) {
            session.setAttribute("userError", e.getMessage());
        } catch (DataAccessException e) {
            log("Time entry update failed", e);
            session.setAttribute("userError", "Database error. Please try again.");
        }
        resp.sendRedirect(req.getContextPath() + "/user/time-logs");
    }
}
