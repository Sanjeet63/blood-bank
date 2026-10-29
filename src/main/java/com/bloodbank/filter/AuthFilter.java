package com.bloodbank.filter;

import com.bloodbank.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/** Login filter: protects /admin/*, /donor/*, /hospital/* and lets each role open only its own pages. */
@WebFilter({"/admin/*", "/donor/*", "/hospital/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");

        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();                   // e.g. /admin/home
        String allowed = "/" + user.getRole().toLowerCase() + "/";
        if (!path.startsWith(allowed)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not allowed to open this page.");
            return;
        }
        chain.doFilter(request, response);
    }
}
