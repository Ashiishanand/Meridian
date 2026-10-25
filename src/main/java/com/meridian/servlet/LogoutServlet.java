package com.meridian.servlet;

import com.meridian.dao.UsageDAO;
import com.meridian.exception.DataAccessException;
import com.meridian.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private final UsageDAO usageDAO = new UsageDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Object current = session.getAttribute("currentUser");
            if (current instanceof User user) {
                try {
                    usageDAO.log(user.getId(), "LOGOUT");
                } catch (DataAccessException e) {
                    log("Could not record logout", e);
                }
            }
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }
}
