package com.bloodbank.dao;

import com.bloodbank.model.Donation;
import com.bloodbank.model.Donor;
import com.bloodbank.util.BloodGroup;
import com.bloodbank.util.DBConnection;
import com.bloodbank.util.Validator;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StockDAO {

    public static final int SHELF_LIFE_DAYS = 35;

    /** Non-expired units per blood group (all 8 groups, 0 if none). */
    public Map<String, Integer> getAvailableStock() throws SQLException {
        Map<String, Integer> stock = new LinkedHashMap<>();
        for (String g : BloodGroup.ALL) stock.put(g, 0);

        String sql = "SELECT blood_group, SUM(units) AS total FROM blood_stock "
                   + "WHERE expiry_date >= CURDATE() GROUP BY blood_group";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) stock.put(rs.getString("blood_group"), rs.getInt("total"));
        }
        return stock;
    }

    public int getExpiredUnits() throws SQLException {
        return sumUnits("expiry_date < CURDATE()");
    }

    public int getExpiringSoonUnits() throws SQLException {
        return sumUnits("expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)");
    }

    private int sumUnits(String condition) throws SQLException {
        String sql = "SELECT COALESCE(SUM(units), 0) FROM blood_stock WHERE units > 0 AND " + condition;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /**
     * Records a donation in ONE transaction:
     * insert donation + add stock + update donor's last donation date.
     */
    public String recordDonation(int donorId, int units) throws SQLException {
        Donor d = new DonorDAO().findById(donorId);
        if (d == null) return "Donor not found.";
        String notEligible = Validator.checkEligibility(d);
        if (notEligible != null) return notEligible;

        LocalDate today = LocalDate.now();
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO donation(donor_id, units, donation_date) VALUES(?,?,?)")) {
                    ps.setInt(1, donorId);
                    ps.setInt(2, units);
                    ps.setDate(3, Date.valueOf(today));
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO blood_stock(blood_group, units, expiry_date) VALUES(?,?,?)")) {
                    ps.setString(1, d.getBloodGroup());
                    ps.setInt(2, units);
                    ps.setDate(3, Date.valueOf(today.plusDays(SHELF_LIFE_DAYS)));
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE donor SET last_donation = ? WHERE donor_id = ?")) {
                    ps.setDate(1, Date.valueOf(today));
                    ps.setInt(2, donorId);
                    ps.executeUpdate();
                }
                con.commit();
                return "Donation recorded: " + units + " unit(s) of " + d.getBloodGroup() + " added to stock.";
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public List<Donation> getDonations(int donorId) throws SQLException {
        String sql = "SELECT * FROM donation WHERE donor_id = ? ORDER BY donation_date DESC";
        List<Donation> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, donorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Donation dn = new Donation();
                    dn.setDonationId(rs.getInt("donation_id"));
                    dn.setDonorId(rs.getInt("donor_id"));
                    dn.setUnits(rs.getInt("units"));
                    dn.setDonationDate(rs.getDate("donation_date"));
                    list.add(dn);
                }
            }
        }
        return list;
    }

    /**
     * Deducts units using the caller's connection (part of the caller's transaction).
     * Oldest-expiring stock is used first. Returns false if there is not enough stock.
     */
    static boolean deduct(Connection con, String bloodGroup, int units) throws SQLException {
        String select = "SELECT stock_id, units FROM blood_stock WHERE blood_group = ? "
                      + "AND expiry_date >= CURDATE() AND units > 0 ORDER BY expiry_date FOR UPDATE";
        int remaining = units;
        try (PreparedStatement ps = con.prepareStatement(select);
             PreparedStatement upd = con.prepareStatement("UPDATE blood_stock SET units = units - ? WHERE stock_id = ?")) {
            ps.setString(1, bloodGroup);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next() && remaining > 0) {
                    int take = Math.min(remaining, rs.getInt("units"));
                    upd.setInt(1, take);
                    upd.setInt(2, rs.getInt("stock_id"));
                    upd.executeUpdate();
                    remaining -= take;
                }
            }
        }
        return remaining == 0;
    }
}
