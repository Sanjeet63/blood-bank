package com.bloodbank.dao;

import com.bloodbank.model.User;
import com.bloodbank.util.DBConnection;

import java.sql.*;

public class UserDAO {

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User(rs.getString("username"), rs.getString("password_hash"), rs.getString("role"));
                    u.setUserId(rs.getInt("user_id"));
                    return u;
                }
            }
        }
        return null;
    }

    /** Inserts the user and returns the generated user_id. */
    public int addUser(User u) throws SQLException {
        String sql = "INSERT INTO users(username, password_hash, role) VALUES(?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getRole());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
