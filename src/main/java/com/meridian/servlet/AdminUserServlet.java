package com.meridian.servlet;

import com.meridian.dao.UserDAO;
import com.meridian.exception.DataAccessException;
import com.meridian.exception.DuplicateEmailException;
import com.meridian.model.RegularUser;
import com.meridian.util.PasswordUtil;
import com.meridian.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/admin/users")
public class AdminUserServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("users", userDAO.findAll());
        } catch (DataAccessException e) {
            log("Could not load users", e);
            req.setAttribute("error", "Could not load user accounts.");
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");
        HttpSession session = req.getSession();
        try {
            if ("add".equals(action)) {
                addUser(req, session);
            } else if ("toggle".equals(action)) {
                toggleUser(req, session);
            } else if ("delete".equals(action)) {
                deleteUser(req, session);
            }
        } catch (DuplicateEmailException e) {
            session.setAttribute("adminError", e.getMessage());
        } catch (DataAccessException e) {
            log("User management failed", e);
            session.setAttribute("adminError", "Database error. Please try again.");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }

    private void addUser(HttpServletRequest req, HttpSession session) throws DuplicateEmailException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (ValidationUtil.isBlank(name) || name.trim().length() < 2) {
            session.setAttribute("adminError", "Please enter the user's full name.");
        } else if (!ValidationUtil.isValidEmail(email)) {
            session.setAttribute("adminError", "Please enter a valid email address.");
        } else if (!ValidationUtil.isStrongPassword(password)) {
            session.setAttribute("adminError",
                    "Password must be at least 8 characters and contain a letter and a digit.");
        } else {
            userDAO.create(new RegularUser(0, name, email, PasswordUtil.hash(password)));
            session.setAttribute("adminMsg", "User account created for " + name.trim() + ".");
        }
    }

    private void toggleUser(HttpServletRequest req, HttpSession session) {
        int id = ValidationUtil.parseId(req.getParameter("id"));
        boolean active = Boolean.parseBoolean(req.getParameter("active"));
        if (id > 0 && userDAO.setActive(id, active)) {
            session.setAttribute("adminMsg", active ? "Account activated." : "Account deactivated.");
        } else {
            session.setAttribute("adminError", "User not found.");
        }
    }

    private void deleteUser(HttpServletRequest req, HttpSession session) {
        int id = ValidationUtil.parseId(req.getParameter("id"));
        if (id > 0 && userDAO.delete(id)) {
            session.setAttribute("adminMsg", "User deleted.");
        } else {
            session.setAttribute("adminError", "User not found.");
        }
    }
}
