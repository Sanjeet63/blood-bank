package com.bloodbank.servlet;

import com.bloodbank.dao.StockDAO;
import com.bloodbank.util.BloodGroup;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/** Public landing page: live stock and compatibility chart. */
@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private final StockDAO stockDAO = new StockDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            req.setAttribute("stock", stockDAO.getAvailableStock());
        } catch (SQLException e) {
            req.setAttribute("dbError", "Live stock is unavailable right now.");
        }
        Map<String, String> chart = new LinkedHashMap<>();
        for (String g : BloodGroup.ALL) chart.put(g, String.join(", ", BloodGroup.compatibleDonors(g)));
        req.setAttribute("chart", chart);
        req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, res);
    }
}
