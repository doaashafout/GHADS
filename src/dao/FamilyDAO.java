package dao;

import config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.Family;

import java.util.logging.Logger;
import java.util.logging.Level;

public class FamilyDAO {

    public List<Family> findAll() {
        List<Family> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM families ORDER BY family_id DESC";
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                list.add(extractFamily(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(FamilyDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public Family findById(int id) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM families WHERE family_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extractFamily(rs);
        } catch (SQLException ex) {
            Logger.getLogger(FamilyDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Family findByNationalId(String nationalId) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM families WHERE national_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nationalId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extractFamily(rs);
        } catch (SQLException ex) {
            Logger.getLogger(FamilyDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean nationalIdExists(String nationalId) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT COUNT(*) FROM families WHERE national_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nationalId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException ex) {
            Logger.getLogger(FamilyDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public List<Family> findNotServed() {
        List<Family> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT f.* FROM families f LEFT JOIN aid_distributions ad ON f.family_id=ad.family_id "
                       + "WHERE ad.distribution_id IS NULL ORDER BY "
                       + "CASE f.vulnerability_level WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 WHEN 'LOW' THEN 3 END";
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                list.add(extractFamily(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(FamilyDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<Family> findByVulnerabilityPriority() {
        List<Family> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM families ORDER BY CASE vulnerability_level "
                       + "WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 WHEN 'LOW' THEN 3 END";
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                list.add(extractFamily(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(FamilyDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public boolean insertOne(Family f) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "INSERT INTO families (household_name, phone, location, family_size, "
                       + "national_id, vulnerability_level, registration_date, last_aid_date) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, f.getHouseholdName());
            ps.setString(2, f.getPhone());
            ps.setString(3, f.getLocation());
            ps.setInt(4, f.getFamilySize());
            ps.setString(5, f.getNationalId());
            ps.setString(6, f.getVulnerabilityLevel());
            ps.setString(7, f.getRegistrationDate());
            ps.setString(8, f.getLastAidDate());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(FamilyDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateOne(Family f) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "UPDATE families SET household_name=?, phone=?, location=?, family_size=?, "
                       + "national_id=?, vulnerability_level=?, last_aid_date=? WHERE family_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, f.getHouseholdName());
            ps.setString(2, f.getPhone());
            ps.setString(3, f.getLocation());
            ps.setInt(4, f.getFamilySize());
            ps.setString(5, f.getNationalId());
            ps.setString(6, f.getVulnerabilityLevel());
            ps.setString(7, f.getLastAidDate());
            ps.setInt(8, f.getFamilyId());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            Logger.getLogger(FamilyDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deleteOne(Family f) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "DELETE FROM families WHERE family_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, f.getFamilyId());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            Logger.getLogger(FamilyDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateLastAidDate(int familyId, String date) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "UPDATE families SET last_aid_date=? WHERE family_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, date);
            ps.setInt(2, familyId);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(FamilyDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private Family extractFamily(ResultSet rs) throws SQLException {
        return new Family(
            rs.getInt("family_id"),
            rs.getString("household_name"),
            rs.getString("phone"),
            rs.getString("location"),
            rs.getInt("family_size"),
            rs.getString("national_id"),
            rs.getString("vulnerability_level"),
            rs.getString("registration_date"),
            rs.getString("last_aid_date")
        );
    }
}
