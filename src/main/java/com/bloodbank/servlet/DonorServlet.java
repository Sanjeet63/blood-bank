package com.bloodbank.servlet;

import com.bloodbank.dao.DonorDAO;
import com.bloodbank.dao.StockDAO;
import com.bloodbank.model.Donor;
import com.bloodbank.model.User;
import com.bloodbank.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/** Donor dashboard: profile, eligibility and donation history. */
@WebServlet("/donor/home")
public class DonorServlet extends HttpServlet {

    private final DonorDAO donorDAO = new DonorDAO();
    private final StockDAO stockDAO = new StockDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        try {
            Donor donor = donorDAO.findByUserId(user.getUserId());
            req.setAttribute("donor", donor);
            if (donor != null) {
                req.setAttribute("eligibility", Validator.checkEligibility(donor));
                req.setAttribute("donations", stockDAO.getDonations(donor.getDonorId()));
            }
            req.getRequestDispatcher("/WEB-INF/views/donor.jsp").forward(req, res);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        String phone = req.getParameter("phone");
        String city = req.getParameter("city");
        String msg;
        try {
            Donor donor = donorDAO.findByUserId(user.getUserId());
            if (donor == null) msg = "Donor profile not found.";
            else if (phone == null || !phone.matches("\\d{10}")) msg = "Phone must be 10 digits.";
            else msg = donorDAO.updateContact(donor.getDonorId(), phone, city) ? "Profile updated." : "Update failed.";
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getSession().setAttribute("msg", msg);
        res.sendRedirect(req.getContextPath() + "/donor/home");
    }
}
