package com.meridian.servlet;

import com.meridian.exception.DataAccessException;
import com.meridian.model.ProgressSummary;
import com.meridian.model.User;
import com.meridian.service.ProgressService;
import com.meridian.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/user/dashboard")
public class UserDashboardServlet extends HttpServlet {

    private final ProgressService progressService = new ProgressService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        try {
            ProgressSummary summary = progressService.summarize(user.getId());
            req.setAttribute("summary", summary);
            req.setAttribute("dayLabels", JsonUtil.toJsonArray(summary.getHoursPerDay().keySet()));
            req.setAttribute("dayValues", JsonUtil.toJsonArray(summary.getHoursPerDay().values()));
        } catch (DataAccessException e) {
            log("Could not load progress summary", e);
            req.setAttribute("error", "Could not load your progress.");
        }
        req.getRequestDispatcher("/WEB-INF/views/user/dashboard.jsp").forward(req, resp);
    }
}
