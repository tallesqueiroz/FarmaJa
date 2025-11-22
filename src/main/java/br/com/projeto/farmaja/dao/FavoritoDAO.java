package br.com.projeto.farmaja.dao;

import br.com.projeto.farmaja.database.DatabaseConnection;
import br.com.projeto.farmaja.model.Favorito;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavoritoDAO {

    // CREATE
    public void criar(Favorito favorito) {
        String sql = """
            INSERT INTO favoritos (usuario_id, medicamento_id, fornecedor_id, tipo_favorito)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, favorito.getUsuarioId());

            if (favorito.getMedicamentoId() != null) {
                stmt.setInt(2, favorito.getMedicamentoId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            if (favorito.getFornecedorId() != null) {
                stmt.setInt(3, favorito.getFornecedorId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, favorito.getTipoFavorito());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    favorito.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar favorito: " + e.getMessage(), e);
        }
    }

    // LEITURA POR ID
    public Favorito buscarPorId(int id) {
        String sql = "SELECT * FROM favoritos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToFavorito(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar favorito: " + e.getMessage(), e);
        }

        return null;
    }

    // LISTAR FAVORITOS DE UM USUÁRIO
    public List<Favorito> buscarPorUsuario(int usuarioId) {
        String sql = "SELECT * FROM favoritos WHERE usuario_id = ? ORDER BY data_adicionado DESC";
        List<Favorito> favoritos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                favoritos.add(mapResultSetToFavorito(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar favoritos do usuário: " + e.getMessage(), e);
        }

        return favoritos;
    }

    // LISTAR MEDICAMENTOS FAVORITOS DE UM USUÁRIO
    public List<Favorito> buscarMedicamentosFavoritos(int usuarioId) {
        String sql = "SELECT * FROM favoritos WHERE usuario_id = ? AND tipo_favorito = 'MEDICAMENTO' ORDER BY data_adicionado DESC";
        List<Favorito> favoritos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                favoritos.add(mapResultSetToFavorito(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar medicamentos favoritos: " + e.getMessage(), e);
        }

        return favoritos;
    }

    // LISTAR FORNECEDORES FAVORITOS DE UM USUÁRIO
    public List<Favorito> buscarFornecedoresFavoritos(int usuarioId) {
        String sql = "SELECT * FROM favoritos WHERE usuario_id = ? AND tipo_favorito = 'FORNECEDOR' ORDER BY data_adicionado DESC";
        List<Favorito> favoritos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                favoritos.add(mapResultSetToFavorito(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar fornecedores favoritos: " + e.getMessage(), e);
        }

        return favoritos;
    }

    // VERIFICAR SE JÁ EXISTE FAVORITO
    public boolean existeFavorito(int usuarioId, Integer medicamentoId, Integer fornecedorId) {
        String sql = """
            SELECT COUNT(*) FROM favoritos 
            WHERE usuario_id = ? 
            AND (medicamento_id = ? OR fornecedor_id = ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);

            if (medicamentoId != null) {
                stmt.setInt(2, medicamentoId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            if (fornecedorId != null) {
                stmt.setInt(3, fornecedorId);
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar favorito: " + e.getMessage(), e);
        }

        return false;
    }

    // DELETE (Remover favorito)
    public void deletar(int id) {
        String sql = "DELETE FROM favoritos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("Favorito não encontrado!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar favorito: " + e.getMessage(), e);
        }
    }

    // DELETE POR USUÁRIO E MEDICAMENTO
    public void deletarPorMedicamento(int usuarioId, int medicamentoId) {
        String sql = "DELETE FROM favoritos WHERE usuario_id = ? AND medicamento_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            stmt.setInt(2, medicamentoId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar favorito de medicamento: " + e.getMessage(), e);
        }
    }

    // DELETE POR USUÁRIO E FORNECEDOR
    public void deletarPorFornecedor(int usuarioId, int fornecedorId) {
        String sql = "DELETE FROM favoritos WHERE usuario_id = ? AND fornecedor_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            stmt.setInt(2, fornecedorId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar favorito de fornecedor: " + e.getMessage(), e);
        }
    }

    private Favorito mapResultSetToFavorito(ResultSet rs) throws SQLException {
        Favorito favorito = new Favorito();
        favorito.setId(rs.getInt("id"));
        favorito.setUsuarioId(rs.getInt("usuario_id"));

        int medicamentoId = rs.getInt("medicamento_id");
        if (!rs.wasNull()) {
            favorito.setMedicamentoId(medicamentoId);
        }

        int fornecedorId = rs.getInt("fornecedor_id");
        if (!rs.wasNull()) {
            favorito.setFornecedorId(fornecedorId);
        }

        favorito.setTipoFavorito(rs.getString("tipo_favorito"));
        favorito.setDataAdicionado(rs.getTimestamp("data_adicionado").toLocalDateTime());
        return favorito;
    }
}