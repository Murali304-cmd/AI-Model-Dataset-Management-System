package dao;

import db.DBConnection;
import model.AIModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModelDAO {

    public boolean addModel(AIModel m) {
        String sql = "INSERT INTO models (model_name, version, algorithm, accuracy, dataset_id) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getModelName()); ps.setString(2, m.getVersion());
            ps.setString(3, m.getAlgorithm()); ps.setDouble(4, m.getAccuracy());
            ps.setInt(5, m.getDatasetId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {return false; }
    }

    public boolean updateModel(AIModel m) {
        String sql = "UPDATE models SET model_name=?, version=?, algorithm=?, accuracy=?, dataset_id=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getModelName()); ps.setString(2, m.getVersion());
            ps.setString(3, m.getAlgorithm()); ps.setDouble(4, m.getAccuracy());
            ps.setInt(5, m.getDatasetId()); ps.setInt(6, m.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {return false; }
    }

    public boolean deleteModel(int id) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM models WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {return false; }
    }

    public List<AIModel> getAllModels() {
        List<AIModel> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM models")) {
            while (rs.next()) {
                list.add(new AIModel(rs.getInt("id"), rs.getString("model_name"),
                    rs.getString("version"), rs.getString("algorithm"),
                    rs.getDouble("accuracy"), rs.getInt("dataset_id")));
            }
        } catch (SQLException e) {}
        return list;
    }
}