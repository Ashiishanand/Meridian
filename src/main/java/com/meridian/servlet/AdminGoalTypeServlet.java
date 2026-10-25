package com.meridian.servlet;

import com.meridian.dao.GoalTypeDAO;
import com.meridian.exception.DataAccessException;
import com.meridian.exception.DuplicateEntryException;
import com.meridian.model.TrackingMetric;
import com.meridian.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/admin/goal-types")
public class AdminGoalTypeServlet extends HttpServlet {

    private final GoalTypeDAO goalTypeDAO = new GoalTypeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("goalTypes", goalTypeDAO.findAll());
        } catch (DataAccessException e) {
            log("Could not load goal types", e);
            req.setAttribute("error", "Could not load goal types.");
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/goal-types.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");
        HttpSession session = req.getSession();
        try {
            if ("add".equals(action)) {
                addType(req, session);
            } else if ("toggle".equals(action)) {
                toggleType(req, session);
            } else if ("delete".equals(action)) {
                deleteType(req, session);
            }
        } catch (DuplicateEntryException e) {
            session.setAttribute("adminError", e.getMessage());
        } catch (DataAccessException e) {
            log("Goal type update failed", e);
            session.setAttribute("adminError", "Database error. Please try again.");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/goal-types");
    }

    private void addType(HttpServletRequest req, HttpSession session) throws DuplicateEntryException {
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        TrackingMetric metric = parseMetric(req.getParameter("metric"));

        if (ValidationUtil.isBlank(name) || name.trim().length() < 2 || name.trim().length() > 60) {
            session.setAttribute("adminError", "Goal type name must be 2 to 60 characters.");
        } else if (description != null && description.trim().length() > 255) {
            session.setAttribute("adminError", "Description must be at most 255 characters.");
        } else if (metric == null) {
            session.setAttribute("adminError", "Please choose a tracking metric.");
        } else {
            goalTypeDAO.create(name, description, metric);
            session.setAttribute("adminMsg", "Goal type '" + name.trim() + "' configured successfully.");
        }
    }

    private void toggleType(HttpServletRequest req, HttpSession session) {
        int id = ValidationUtil.parseId(req.getParameter("id"));
        boolean active = Boolean.parseBoolean(req.getParameter("active"));
        if (id > 0 && goalTypeDAO.setActive(id, active)) {
            session.setAttribute("adminMsg", active ? "Goal type enabled." : "Goal type disabled.");
        } else {
            session.setAttribute("adminError", "Goal type not found.");
        }
    }

    private void deleteType(HttpServletRequest req, HttpSession session) {
        int id = ValidationUtil.parseId(req.getParameter("id"));
        if (id <= 0) {
            session.setAttribute("adminError", "Goal type not found.");
        } else if (goalTypeDAO.isInUse(id)) {
            session.setAttribute("adminError",
                    "This goal type is used by existing goals. Disable it instead of deleting.");
        } else if (goalTypeDAO.delete(id)) {
            session.setAttribute("adminMsg", "Goal type deleted.");
        } else {
            session.setAttribute("adminError", "Goal type not found.");
        }
    }

    private TrackingMetric parseMetric(String text) {
        if (text == null) {
            return null;
        }
        try {
            return TrackingMetric.valueOf(text);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
