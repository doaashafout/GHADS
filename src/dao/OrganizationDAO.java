package dao;

import config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.Organization;

import java.util.logging.Logger;
import java.util.logging.Level;

public class OrganizationDAO {

    public List<Organization> findAll() {
        List<Organization> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM organizations";
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                Organization o = new Organization(
                    rs.getInt("org_id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getString("contact_info")
                );
                list.add(o);
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrganizationDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public Organization findById(int id) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM organizations WHERE org_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Organization(
                    rs.getInt("org_id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getString("contact_info")
                );
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrganizationDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean insertOne(Organization o) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "INSERT INTO organizations (name, type, contact_info) VALUES (?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, o.getName());
            ps.setString(2, o.getType());
            ps.setString(3, o.getContactInfo());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(OrganizationDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateOne(Organization o) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "UPDATE organizations SET name=?, type=?, contact_info=? WHERE org_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, o.getName());
            ps.setString(2, o.getType());
            ps.setString(3, o.getContactInfo());
            ps.setInt(4, o.getOrgId());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            Logger.getLogger(OrganizationDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deleteOne(Organization o) {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "DELETE FROM organizations WHERE org_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, o.getOrgId());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            Logger.getLogger(OrganizationDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
