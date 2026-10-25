package com.meridian.servlet;

import com.meridian.dao.UsageDAO;
import com.meridian.dao.UserDAO;
import com.meridian.exception.DataAccessException;
import com.meridian.model.User;
import com.meridian.util.PasswordUtil;
import com.meridian.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final UsageDAO usageDAO = new UsageDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("currentUser") instanceof User u) {
            resp.sendRedirect(req.getContextPath() + u.getDashboardPath());
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (ValidationUtil.isBlank(email) || ValidationUtil.isBlank(password)) {
            fail(req, resp, email, "Please enter both email and password.");
            return;
        }

        try {
            Optional<User> found = userDAO.findByEmail(email);
            if (found.isEmpty() || !PasswordUtil.matches(password, found.get().getPasswordHash())) {
                fail(req, resp, email, "Invalid email or password.");
                return;
            }
            User user = found.get();
            if (!user.isActive()) {
                fail(req, resp, email, "This account has been deactivated. Contact the admin.");
                return;
            }
            HttpSession old = req.getSession(false);
            if (old != null) {
                old.invalidate();
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("currentUser", user);
            session.setMaxInactiveInterval(30 * 60);

            userDAO.updateLastLogin(user.getId());
            usageDAO.log(user.getId(), "LOGIN");
            resp.sendRedirect(req.getContextPath() + user.getDashboardPath());

        } catch (DataAccessException e) {
            log("Login failed due to database error", e);
            fail(req, resp, email, "Server problem. Please try again shortly.");
        }
    }

    private void fail(HttpServletRequest req, HttpServletResponse resp, String email, String msg)
            throws ServletException, IOException {
        req.setAttribute("error", msg);
        req.setAttribute("email", email);
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }
}
