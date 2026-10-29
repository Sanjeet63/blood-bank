package com.bloodbank.servlet;

import com.bloodbank.dao.RequestDAO;
import com.bloodbank.dao.StockDAO;
import com.bloodbank.model.BloodRequest;
import com.bloodbank.model.User;
import com.bloodbank.util.BloodGroup;
import com.bloodbank.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/** Hospital dashboard: search availability (with compatible groups) and raise blood requests. */
@WebServlet("/hospital/home")
public class HospitalServlet extends HttpServlet {

    private final StockDAO stockDAO = new StockDAO();
    private final RequestDAO requestDAO = new RequestDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        String group = req.getParameter("group");
        try {
            req.setAttribute("stock", stockDAO.getAvailableStock());
            if (BloodGroup.isValid(group)) {
                req.setAttribute("searchGroup", group);
                req.setAttribute("compatible", BloodGroup.compatibleDonors(group));
            }
            req.setAttribute("requests", requestDAO.findByHospital(user.getUserId()));
            req.getRequestDispatcher("/WEB-INF/views/hospital.jsp").forward(req, res);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        String patient = req.getParameter("patientName");
        String group = req.getParameter("bloodGroup");
        int units = Validator.toInt(req.getParameter("units"));

        String msg;
        if (Validator.isBlank(patient)) msg = "Patient name is required.";
        else if (!BloodGroup.isValid(group)) msg = "Invalid blood group.";
        else if (units < 1 || units > 10) msg = "Units must be between 1 and 10.";
        else {
            BloodRequest r = new BloodRequest();
            r.setHospitalId(user.getUserId());
            r.setPatientName(patient.trim());
            r.setBloodGroup(group);
            r.setUnits(units);
            try {
                msg = requestDAO.addRequest(r) ? "Request submitted. Status: PENDING" : "Could not submit request.";
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        }
        req.getSession().setAttribute("msg", msg);
        res.sendRedirect(req.getContextPath() + "/hospital/home");
    }
}
