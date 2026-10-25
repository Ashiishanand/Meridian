package com.meridian.servlet;

import com.meridian.dao.GoalDAO;
import com.meridian.dao.GoalTypeDAO;
import com.meridian.dao.UsageDAO;
import com.meridian.exception.DataAccessException;
import com.meridian.exception.InvalidGoalException;
import com.meridian.model.User;
import com.meridian.service.GoalService;
import com.meridian.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/user/goals")
public class GoalServlet extends HttpServlet {

    private final GoalDAO goalDAO = new GoalDAO();
    private final GoalTypeDAO goalTypeDAO = new GoalTypeDAO();
    private final GoalService goalService = new GoalService();
    private final UsageDAO usageDAO = new UsageDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        try {
            req.setAttribute("goals", goalDAO.findByUser(user.getId()));
            req.setAttribute("goalTypes", goalTypeDAO.findActive());
        } catch (DataAccessException e) {
            log("Could not load goals", e);
            req.setAttribute("error", "Could not load your goals.");
        }
        req.setAttribute("today", LocalDate.now().toString());
        req.getRequestDispatcher("/WEB-INF/views/user/goals.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        HttpSession session = req.getSession();
        String action = req.getParameter("action");
        try {
            if ("add".equals(action)) {
                goalService.addGoal(user.getId(), req.getParameter("typeId"), req.getParameter("name"),
                        req.getParameter("target"), req.getParameter("deadline"));
                usageDAO.log(user.getId(), "GOAL_CREATED");
                session.setAttribute("userMsg", "Goal created successfully.");
            } else if ("delete".equals(action)) {
                int id = ValidationUtil.parseId(req.getParameter("id"));
                if (id > 0 && goalDAO.delete(id, user.getId())) {
                    session.setAttribute("userMsg", "Goal deleted.");
                } else {
                    session.setAttribute("userError", "Goal not found.");
                }
            }
        } catch (InvalidGoalException e) {
            session.setAttribute("userError", e.getMessage());
        } catch (DataAccessException e) {
            log("Goal update failed", e);
            session.setAttribute("userError", "Database error. Please try again.");
        }
        resp.sendRedirect(req.getContextPath() + "/user/goals");
    }
}
