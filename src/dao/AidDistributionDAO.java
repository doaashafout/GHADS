package dao;

import config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.AidDistribution;

import java.util.logging.Logger;
import java.util.logging.Level;

public class AidDistributionDAO {

    public List<AidDistribution> findAll() {
        List<AidDistribution> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT ad.*, f.household_name AS family_name, f.vulnerability_level AS family_vulnerability, o.name AS org_name "
                       + "FROM aid_distributions ad "
                       + "JOIN families f ON ad.family_id=f.family_id "
                       + "JOIN organizations o ON ad.org_id=o.org_id "
                       + "ORDER BY ad.distribution_id DESC";
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                list.add(extractDistribution(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AidDistributionDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<AidDistribution> findByOrgId(int orgId) {
        List<AidDistribution> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT ad.*, f.household_name AS family_name, f.vulnerability_level AS family_vulnerability, o.name AS org_name "
                       + "FROM aid_distributions ad "
                       + "JOIN families f ON ad.family_id=f.family_id "
                       + "JOIN organizations o ON ad.org_id=o.org_id "
                       + "WHERE ad.org_id=? ORDER BY ad.distribution_id DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orgId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractDistribution(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AidDistributionDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<AidDistribution> findByFamilyId(int familyId) {
        List<AidDistribution> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT ad.*, f.household_name AS family_name, f.vulnerability_level AS family_vulnerability, o.name AS org_name "
                       + "FROM aid_distributions ad "
                       + "JOIN families f ON ad.family_id=f.family_id "
                       + "JOIN organizations o ON ad.org_id=o.org_id "
                       + "WHERE ad.family_id=? ORDER BY ad.distribution_date DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, familyId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractDistribution(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AidDistributionDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public AidDistribution findLatestByFamilyId(int familyId) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT ad.*, f.household_name AS family_name, f.vulnerability_level AS family_vulnerability, o.name AS org_name "
                       + "FROM aid_distributions ad "
                       + "JOIN families f ON ad.family_id=f.family_id "
                       + "JOIN organizations o ON ad.org_id=o.org_id "
                       + "WHERE ad.family_id=? ORDER BY ad.distribution_date DESC LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, familyId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extractDistribution(rs);
        } catch (SQLException ex) {
            Logger.getLogger(AidDistributionDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean hasRecentDistributionByType(int familyId, String aidType, String distributionDate, int days) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT COUNT(*) FROM aid_distributions WHERE family_id=? AND aid_type=? "
                       + "AND distribution_date >= DATE_SUB(?, INTERVAL ? DAY)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, familyId);
            ps.setString(2, aidType);
            ps.setString(3, distributionDate);
            ps.setInt(4, days);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException ex) {
            Logger.getLogger(AidDistributionDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean insertOne(AidDistribution ad) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "INSERT INTO aid_distributions (family_id, org_id, distributed_by, distribution_date, aid_type) "
                       + "VALUES (?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, ad.getFamilyId());
            ps.setInt(2, ad.getOrgId());
            ps.setInt(3, ad.getDistributedBy());
            ps.setString(4, ad.getDistributionDate());
            ps.setString(5, ad.getAidType());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(AidDistributionDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deleteOne(AidDistribution ad) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "DELETE FROM aid_distributions WHERE distribution_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, ad.getDistributionId());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            Logger.getLogger(AidDistributionDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public int countByOrgId(int orgId) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT COUNT(*) FROM aid_distributions WHERE org_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orgId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(AidDistributionDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public int countDistinctFamiliesByOrg(int orgId) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT COUNT(DISTINCT family_id) FROM aid_distributions WHERE org_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orgId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(AidDistributionDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private AidDistribution extractDistribution(ResultSet rs) throws SQLException {
        AidDistribution ad = new AidDistribution(
            rs.getInt("distribution_id"),
            rs.getInt("family_id"),
            rs.getInt("org_id"),
            rs.getInt("distributed_by"),
            rs.getString("distribution_date"),
            rs.getString("aid_type")
        );
        try { ad.setFamilyName(rs.getString("family_name")); } catch (SQLException e) {}
        try { ad.setFamilyVulnerability(rs.getString("family_vulnerability")); } catch (SQLException e) {}
        try { ad.setOrgName(rs.getString("org_name")); } catch (SQLException e) {}
        return ad;
    }
}
