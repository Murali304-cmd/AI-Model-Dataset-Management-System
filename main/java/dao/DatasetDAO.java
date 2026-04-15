package dao;

import db.DBConnection;
import model.Dataset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatasetDAO {

    public boolean addDataset(Dataset d) {
        String sql = "INSERT INTO datasets (name, description, category, format, size_mb, source_url, status) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getName()); ps.setString(2, d.getDescription());
            ps.setString(3, d.getCategory()); ps.setString(4, d.getFormat());
            ps.setDouble(5, d.getSizeMb()); ps.setString(6, d.getSourceUrl());
            ps.setString(7, d.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {return false; }
    }

    public boolean updateDataset(Dataset d) {
        String sql = "UPDATE datasets SET name=?, description=?, category=?, format=?, size_mb=?, source_url=?, status=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getName()); ps.setString(2, d.getDescription());
            ps.setString(3, d.getCategory()); ps.setString(4, d.getFormat());
            ps.setDouble(5, d.getSizeMb()); ps.setString(6, d.getSourceUrl());
            ps.setString(7, d.getStatus()); ps.setInt(8, d.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {return false; }
    }

    public boolean deleteDataset(int id) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM datasets WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {return false; }
    }

    public List<Dataset> getAllDatasets() {
        List<Dataset> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM datasets")) {
            while (rs.next()) {
                list.add(new Dataset(rs.getInt("id"), rs.getString("name"),
                    rs.getString("description"), rs.getString("category"),
                    rs.getString("format"), rs.getDouble("size_mb"),
                    rs.getString("source_url"), rs.getString("status")));
            }
        } catch (SQLException e) {}
        return list;
    }

    public List<Dataset> searchDatasets(String keyword) {
        List<Dataset> list = new ArrayList<>();
        String sql = "SELECT * FROM datasets WHERE name LIKE ? OR category LIKE ? OR status LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k); ps.setString(3, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Dataset(rs.getInt("id"), rs.getString("name"),
                    rs.getString("description"), rs.getString("category"),
                    rs.getString("format"), rs.getDouble("size_mb"),
                    rs.getString("source_url"), rs.getString("status")));
            }
        } catch (SQLException e) {}
        return list;
    }
}