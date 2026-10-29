package com.bloodbank.dao;

import com.bloodbank.model.Donor;
import com.bloodbank.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonorDAO {

    public boolean addDonor(Donor d) throws SQLException {
        String sql = "INSERT INTO donor(user_id, name, age, blood_group, phone, city) VALUES(?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (d.getUserId() > 0) ps.setInt(1, d.getUserId());
            else ps.setNull(1, Types.INTEGER);
            ps.setString(2, d.getName());
            ps.setInt(3, d.getAge());
            ps.setString(4, d.getBloodGroup());
            ps.setString(5, d.getPhone());
            ps.setString(6, d.getCity());
            return ps.executeUpdate() > 0;
        }
    }

    /** All donors, or only the given blood group if one is passed. */
    public List<Donor> search(String bloodGroup) throws SQLException {
        boolean filter = bloodGroup != null && !bloodGroup.isEmpty();
        String sql = "SELECT * FROM donor" + (filter ? " WHERE blood_group = ?" : "") + " ORDER BY name";
        List<Donor> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (filter) ps.setString(1, bloodGroup);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public Donor findById(int donorId) throws SQLException {
        return findOne("SELECT * FROM donor WHERE donor_id = ?", donorId);
    }

    public Donor findByUserId(int userId) throws SQLException {
        return findOne("SELECT * FROM donor WHERE user_id = ?", userId);
    }

    public boolean updateContact(int donorId, String phone, String city) throws SQLException {
        String sql = "UPDATE donor SET phone = ?, city = ? WHERE donor_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setString(2, city);
            ps.setInt(3, donorId);
            return ps.executeUpdate() > 0;
        }
    }

    private Donor findOne(String sql, int id) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    private Donor map(ResultSet rs) throws SQLException {
        Donor d = new Donor();
        d.setDonorId(rs.getInt("donor_id"));
        d.setUserId(rs.getInt("user_id"));
        d.setName(rs.getString("name"));
        d.setAge(rs.getInt("age"));
        d.setBloodGroup(rs.getString("blood_group"));
        d.setPhone(rs.getString("phone"));
        d.setCity(rs.getString("city"));
        d.setLastDonation(rs.getDate("last_donation"));
        return d;
    }
}
