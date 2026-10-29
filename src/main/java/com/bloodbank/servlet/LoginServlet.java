package com.bloodbank.servlet;

import com.bloodbank.dao.UserDAO;
import com.bloodbank.model.User;
import com.bloodbank.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        try {
            User user = userDAO.findByUsername(username);
            if (user == null || password == null || !PasswordUtil.verify(password, user.getPasswordHash())) {
                req.setAttribute("error", "Invalid username or password.");
                req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, res);
                return;
            }
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            res.sendRedirect(req.getContextPath() + "/" + user.getRole().toLowerCase() + "/home");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
