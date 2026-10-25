package com.meridian.filter;

import com.meridian.model.Role;
import com.meridian.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = {"/admin/*", "/user/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String ctx = request.getContextPath();
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("currentUser");

        if (user == null) {
            response.sendRedirect(ctx + "/login");
            return;
        }

        String path = request.getRequestURI().substring(ctx.length());
        boolean adminArea = path.startsWith("/admin/");
        if (adminArea && user.getRole() != Role.ADMIN) {
            response.sendRedirect(ctx + user.getDashboardPath());
            return;
        }
        if (!adminArea && user.getRole() == Role.ADMIN) {
            response.sendRedirect(ctx + user.getDashboardPath());
            return;
        }
        chain.doFilter(req, res);
    }
}
