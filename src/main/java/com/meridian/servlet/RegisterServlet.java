package com.meridian.servlet;

import com.meridian.dao.UsageDAO;
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

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final UsageDAO usageDAO = new UsageDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirm");

        String error = null;
        if (ValidationUtil.isBlank(name) || name.trim().length() < 2) {
            error = "Please enter your full name.";
        } else if (!ValidationUtil.isValidEmail(email)) {
            error = "Please enter a valid email address.";
        } else if (!ValidationUtil.isStrongPassword(password)) {
            error = "Password must be at least 8 characters and contain a letter and a digit.";
        } else if (!password.equals(confirm)) {
            error = "Passwords do not match.";
        }

        if (error != null) {
            back(req, resp, name, email, error);
            return;
        }

        try {
            int newId = userDAO.create(new RegularUser(0, name, email, PasswordUtil.hash(password)));
            usageDAO.log(newId, "REGISTER");
            req.getSession(true).setAttribute("flash", "Account created. Please log in.");
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (DuplicateEmailException e) {
            back(req, resp, name, email, e.getMessage());
        } catch (DataAccessException e) {
            log("Registration failed due to database error", e);
            back(req, resp, name, email, "Server problem. Please try again shortly.");
        }
    }

    private void back(HttpServletRequest req, HttpServletResponse resp,
                      String name, String email, String msg) throws ServletException, IOException {
        req.setAttribute("error", msg);
        req.setAttribute("name", name);
        req.setAttribute("email", email);
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }
}
