package com.bloodbank.listener;

import com.bloodbank.dao.UserDAO;
import com.bloodbank.model.User;
import com.bloodbank.util.BloodGroup;
import com.bloodbank.util.DBConnection;
import com.bloodbank.util.PasswordUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

/** Runs once when the app starts: loads the JDBC driver, creates tables and the default admin. */
@WebListener
public class AppListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("bloodGroups", BloodGroup.ALL);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            createTables();

            UserDAO userDAO = new UserDAO();
            if (userDAO.findByUsername("admin") == null) {
                userDAO.addUser(new User("admin", PasswordUtil.hash("admin123"), "ADMIN"));
                System.out.println("[BloodBank] Default admin created (admin / admin123)");
            }
            System.out.println("[BloodBank] Application started, database connected.");
        } catch (Exception e) {
            System.err.println("[BloodBank] Startup failed - check MySQL and DBConnection settings: " + e.getMessage());
        }
    }

    /** Runs schema.sql (CREATE TABLE IF NOT EXISTS ...) so tables are created automatically. */
    private void createTables() throws Exception {
        String sql;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("schema.sql")) {
            sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement()) {
            for (String part : sql.split(";")) {
                String stmt = part.replaceAll("(?m)^\\s*--.*$", "").trim();
                if (!stmt.isEmpty()) st.execute(stmt);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[BloodBank] Application stopped.");
    }
}
