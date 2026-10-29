package com.bloodbank.servlet;

import com.bloodbank.dao.DonorDAO;
import com.bloodbank.dao.RequestDAO;
import com.bloodbank.dao.StockDAO;
import com.bloodbank.model.BloodRequest;
import com.bloodbank.model.Donor;
import com.bloodbank.util.BloodGroup;
import com.bloodbank.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Admin dashboard: donors, stock, donations and request approval. */
@WebServlet("/admin/home")
public class AdminServlet extends HttpServlet {

    private final DonorDAO donorDAO = new DonorDAO();
    private final StockDAO stockDAO = new StockDAO();
    private final RequestDAO requestDAO = new RequestDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            Map<String, Integer> stock = stockDAO.getAvailableStock();
            List<BloodRequest> requests = requestDAO.findAll();
            req.setAttribute("stock", stock);
            req.setAttribute("expired", stockDAO.getExpiredUnits());
            req.setAttribute("expiringSoon", stockDAO.getExpiringSoonUnits());
            req.setAttribute("criticalLevel", BloodGroup.CRITICAL_LEVEL);
            req.setAttribute("donors", donorDAO.search(req.getParameter("group")));
            req.setAttribute("requests", requests);
            req.setAttribute("totalUnits", stock.values().stream().mapToInt(Integer::intValue).sum());
            req.setAttribute("donorCount", donorDAO.search(null).size());
            req.setAttribute("pendingCount", requests.stream().filter(r -> "PENDING".equals(r.getStatus())).count());
            req.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(req, res);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = req.getParameter("action");
        String msg;
        try {
            switch (action == null ? "" : action) {
                case "addDonor" -> {
                    Donor d = new Donor();
                    d.setName(req.getParameter("name"));
                    d.setAge(Validator.toInt(req.getParameter("age")));
                    d.setBloodGroup(req.getParameter("bloodGroup"));
                    d.setPhone(req.getParameter("phone"));
                    d.setCity(req.getParameter("city"));
                    String error = Validator.validateDonor(d.getName(), d.getAge(), d.getBloodGroup(), d.getPhone());
                    msg = (error != null) ? error : (donorDAO.addDonor(d) ? "Donor added." : "Could not add donor.");
                }
                case "donation" -> {
                    int units = Validator.toInt(req.getParameter("units"));
                    msg = (units < 1 || units > 2)
                            ? "Units per donation must be 1 or 2."
                            : stockDAO.recordDonation(Validator.toInt(req.getParameter("donorId")), units);
                }
                case "approve" -> msg = requestDAO.approveAndIssue(Validator.toInt(req.getParameter("requestId")));
                case "reject" -> msg = requestDAO.reject(Validator.toInt(req.getParameter("requestId")))
                        ? "Request rejected." : "Request already processed.";
                default -> msg = "Unknown action.";
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getSession().setAttribute("msg", msg);
        res.sendRedirect(req.getContextPath() + "/admin/home");
    }
}
