package com.bloodbank.servlet;

import com.bloodbank.dao.DonorDAO;
import com.bloodbank.dao.UserDAO;
import com.bloodbank.model.Donor;
import com.bloodbank.model.User;
import com.bloodbank.util.PasswordUtil;
import com.bloodbank.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/** Self-registration for Donors and Hospitals. */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final DonorDAO donorDAO = new DonorDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String role = req.getParameter("role");
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        String error = null;
        if (!"DONOR".equals(role) && !"HOSPITAL".equals(role)) error = "Please choose a valid role.";
        else if (Validator.isBlank(username) || username.length() < 3) error = "Username must have at least 3 characters.";
        else if (password == null || password.length() < 6) error = "Password must have at least 6 characters.";

        Donor donor = null;
        if (error == null && "DONOR".equals(role)) {
            donor = new Donor();
            donor.setName(req.getParameter("name"));
            donor.setAge(Validator.toInt(req.getParameter("age")));
            donor.setBloodGroup(req.getParameter("bloodGroup"));
            donor.setPhone(req.getParameter("phone"));
            donor.setCity(req.getParameter("city"));
            error = Validator.validateDonor(donor.getName(), donor.getAge(), donor.getBloodGroup(), donor.getPhone());
        }

        try {
            if (error == null && userDAO.findByUsername(username.trim()) != null) error = "Username already taken.";

            if (error != null) {
                req.setAttribute("error", error);
                req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, res);
                return;
            }

            int userId = userDAO.addUser(new User(username.trim(), PasswordUtil.hash(password), role));
            if (donor != null) {
                donor.setUserId(userId);
                donorDAO.addDonor(donor);
            }
            req.setAttribute("success", "Registration successful. Please login.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, res);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
