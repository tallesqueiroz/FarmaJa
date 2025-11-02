package br.com.projeto.farmaja.dao;

import br.com.projeto.farmaja.database.DatabaseConnection;
import br.com.projeto.farmaja.model.HistoricoEntrega;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoricoEntregaDAO {

    // CREATE
    public void criar(HistoricoEntrega historico) {
        String sql = """
            INSERT INTO historico_entregas (pedido_id, entregador_id, status_anterior, 
                                           status_novo, observacao)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, historico.getPedidoId());

            if (historico.getEntregadorId() != null) {
                stmt.setInt(2, historico.getEntregadorId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setString(3, historico.getStatusAnterior());
            stmt.setString(4, historico.getStatusNovo());
            stmt.setString(5, historico.getObservacao());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    historico.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar histórico de entrega: " + e.getMessage(), e);
        }
    }

    // LEITURA POR ID
    public HistoricoEntrega buscarPorId(int id) {
        String sql = "SELECT * FROM historico_entregas WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToHistorico(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar histórico de entrega: " + e.getMessage(), e);
        }

        return null;
    }

    // LEITURA ORDENADA
    public List<HistoricoEntrega> buscarPorPedido(int pedidoId) {
        String sql = "SELECT * FROM historico_entregas WHERE pedido_id = ? ORDER BY data_atualizacao";
        List<HistoricoEntrega> historicos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pedidoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                historicos.add(mapResultSetToHistorico(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar histórico do pedido: " + e.getMessage(), e);
        }

        return historicos;
    }

    public List<HistoricoEntrega> buscarPorEntregador(int entregadorId) {
        String sql = "SELECT * FROM historico_entregas WHERE entregador_id = ? ORDER BY data_atualizacao DESC";
        List<HistoricoEntrega> historicos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entregadorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                historicos.add(mapResultSetToHistorico(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar histórico do entregador: " + e.getMessage(), e);
        }

        return historicos;
    }

    public List<HistoricoEntrega> buscarTodos() {
        String sql = "SELECT * FROM historico_entregas ORDER BY data_atualizacao DESC";
        List<HistoricoEntrega> historicos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                historicos.add(mapResultSetToHistorico(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar histórico de entregas: " + e.getMessage(), e);
        }

        return historicos;
    }

    // DELETE
    public void deletar(int id) {
        String sql = "DELETE FROM historico_entregas WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("Histórico de entrega não encontrado!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar histórico de entrega: " + e.getMessage(), e);
        }
    }

    private HistoricoEntrega mapResultSetToHistorico(ResultSet rs) throws SQLException {
        HistoricoEntrega historico = new HistoricoEntrega();
        historico.setId(rs.getInt("id"));
        historico.setPedidoId(rs.getInt("pedido_id"));

        int entregadorId = rs.getInt("entregador_id");
        if (!rs.wasNull()) {
            historico.setEntregadorId(entregadorId);
        }

        historico.setStatusAnterior(rs.getString("status_anterior"));
        historico.setStatusNovo(rs.getString("status_novo"));
        historico.setDataAtualizacao(rs.getTimestamp("data_atualizacao").toLocalDateTime());
        historico.setObservacao(rs.getString("observacao"));
        return historico;
    }
}
