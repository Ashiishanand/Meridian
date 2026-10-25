package com.meridian.servlet;

import com.meridian.dao.UsageDAO;
import com.meridian.dao.UserDAO;
import com.meridian.exception.DataAccessException;
import com.meridian.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final UsageDAO usageDAO = new UsageDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Map<String, Integer> logins = usageDAO.loginsPerDay(7);
            Map<String, Integer> actions = usageDAO.actionCounts();

            req.setAttribute("totalUsers", userDAO.countUsers());
            req.setAttribute("activeUsers", userDAO.countActiveUsers());
            req.setAttribute("totalGoals", usageDAO.totalGoals());
            req.setAttribute("totalEntries", usageDAO.totalTimeEntries());
            req.setAttribute("totalHours", String.format("%.1f", usageDAO.totalHours()));
            req.setAttribute("loginLabels", JsonUtil.toJsonArray(logins.keySet()));
            req.setAttribute("loginValues", JsonUtil.toJsonArray(logins.values()));
            req.setAttribute("actionLabels", JsonUtil.toJsonArray(actions.keySet()));
            req.setAttribute("actionValues", JsonUtil.toJsonArray(actions.values()));
            req.setAttribute("hasActions", !actions.isEmpty());
        } catch (DataAccessException e) {
            log("Could not load usage data", e);
            req.setAttribute("error", "Could not load usage data.");
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}
