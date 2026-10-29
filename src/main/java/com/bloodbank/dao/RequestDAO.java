package com.bloodbank.dao;

import com.bloodbank.model.BloodRequest;
import com.bloodbank.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    public boolean addRequest(BloodRequest r) throws SQLException {
        String sql = "INSERT INTO blood_request(hospital_id, patient_name, blood_group, units, status, request_date) "
                   + "VALUES(?,?,?,?,'PENDING',?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getHospitalId());
            ps.setString(2, r.getPatientName());
            ps.setString(3, r.getBloodGroup());
            ps.setInt(4, r.getUnits());
            ps.setDate(5, Date.valueOf(LocalDate.now()));
            return ps.executeUpdate() > 0;
        }
    }

    public List<BloodRequest> findAll() throws SQLException {
        return query("SELECT * FROM blood_request ORDER BY request_id DESC", 0);
    }

    public List<BloodRequest> findByHospital(int hospitalId) throws SQLException {
        return query("SELECT * FROM blood_request WHERE hospital_id = ? ORDER BY request_id DESC", hospitalId);
    }

    public boolean reject(int requestId) throws SQLException {
        String sql = "UPDATE blood_request SET status = 'REJECTED' WHERE request_id = ? AND status = 'PENDING'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Approve & issue in ONE transaction: reduce stock and mark request ISSUED.
     * If stock is insufficient everything is rolled back.
     */
    public String approveAndIssue(int requestId) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                String group;
                int units;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT blood_group, units, status FROM blood_request WHERE request_id = ? FOR UPDATE")) {
                    ps.setInt(1, requestId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || !"PENDING".equals(rs.getString("status"))) {
                            con.rollback();
                            return "Request not found or already processed.";
                        }
                        group = rs.getString("blood_group");
                        units = rs.getInt("units");
                    }
                }

                if (!StockDAO.deduct(con, group, units)) {
                    con.rollback();
                    return "Insufficient " + group + " stock for request #" + requestId + ". Request is still pending.";
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE blood_request SET status = 'ISSUED' WHERE request_id = ?")) {
                    ps.setInt(1, requestId);
                    ps.executeUpdate();
                }
                con.commit();
                return "Request #" + requestId + " approved and " + units + " unit(s) of " + group + " issued.";
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    private List<BloodRequest> query(String sql, int hospitalId) throws SQLException {
        List<BloodRequest> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (hospitalId > 0) ps.setInt(1, hospitalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BloodRequest r = new BloodRequest();
                    r.setRequestId(rs.getInt("request_id"));
                    r.setHospitalId(rs.getInt("hospital_id"));
                    r.setPatientName(rs.getString("patient_name"));
                    r.setBloodGroup(rs.getString("blood_group"));
                    r.setUnits(rs.getInt("units"));
                    r.setStatus(rs.getString("status"));
                    r.setRequestDate(rs.getDate("request_date"));
                    list.add(r);
                }
            }
        }
        return list;
    }
}
