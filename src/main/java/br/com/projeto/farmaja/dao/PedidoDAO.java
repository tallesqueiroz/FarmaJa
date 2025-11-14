package br.com.projeto.farmaja.dao;

import br.com.projeto.farmaja.database.DatabaseConnection;
import br.com.projeto.farmaja.model.Pedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    // CREATE
    public void criar(Pedido pedido) {
        String sql = """
            INSERT INTO pedidos (cliente_id, entregador_id, endereco_id, valor_total, 
                                status, forma_pagamento, observacoes)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, pedido.getClienteId());

            if (pedido.getEntregadorId() != null) {
                stmt.setInt(2, pedido.getEntregadorId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setInt(3, pedido.getEnderecoId());
            stmt.setBigDecimal(4, pedido.getValorTotal());
            stmt.setString(5, pedido.getStatus());
            stmt.setString(6, pedido.getFormaPagamento());
            stmt.setString(7, pedido.getObservacoes());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pedido.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar pedido: " + e.getMessage(), e);
        }
    }

    // READ BY ID
    public Pedido buscarPorId(int id) {
        String sql = "SELECT * FROM pedidos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPedido(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedido: " + e.getMessage(), e);
        }

        return null;
    }

    // READ ALL
    public List<Pedido> buscarTodos() {
        String sql = "SELECT * FROM pedidos ORDER BY data_pedido DESC";
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pedidos.add(mapResultSetToPedido(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos: " + e.getMessage(), e);
        }

        return pedidos;
    }

    // READ BY CLIENT
    public List<Pedido> buscarPorCliente(int clienteId) {
        String sql = "SELECT * FROM pedidos WHERE cliente_id = ? ORDER BY data_pedido DESC";
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                pedidos.add(mapResultSetToPedido(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos do cliente: " + e.getMessage(), e);
        }

        return pedidos;
    }

    // READ BY DELIVERY
    public List<Pedido> buscarPorEntregador(int entregadorId) {
        String sql = "SELECT * FROM pedidos WHERE entregador_id = ? ORDER BY data_pedido DESC";
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entregadorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                pedidos.add(mapResultSetToPedido(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos do entregador: " + e.getMessage(), e);
        }

        return pedidos;
    }

    // READ BY STATUS
    public List<Pedido> buscarPorStatus(String status) {
        String sql = "SELECT * FROM pedidos WHERE status = ? ORDER BY data_pedido DESC";
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                pedidos.add(mapResultSetToPedido(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos por status: " + e.getMessage(), e);
        }

        return pedidos;
    }

    // READ PENDING ASSIGNMENT
    public List<Pedido> buscarPendentesAtribuicao() {
        String sql = """
            SELECT * FROM pedidos 
            WHERE status = 'PRONTO_PARA_ENTREGA' AND entregador_id IS NULL 
            ORDER BY data_pedido
        """;
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pedidos.add(mapResultSetToPedido(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos pendentes de atribuição: " + e.getMessage(), e);
        }

        return pedidos;
    }

    // UPDATE
    public void atualizar(Pedido pedido) {
        String sql = """
            UPDATE pedidos 
            SET cliente_id = ?, entregador_id = ?, endereco_id = ?, valor_total = ?,
                status = ?, forma_pagamento = ?, data_entrega = ?, observacoes = ?
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pedido.getClienteId());

            if (pedido.getEntregadorId() != null) {
                stmt.setInt(2, pedido.getEntregadorId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setInt(3, pedido.getEnderecoId());
            stmt.setBigDecimal(4, pedido.getValorTotal());
            stmt.setString(5, pedido.getStatus());
            stmt.setString(6, pedido.getFormaPagamento());

            if (pedido.getDataEntrega() != null) {
                stmt.setTimestamp(7, Timestamp.valueOf(pedido.getDataEntrega()));
            } else {
                stmt.setNull(7, Types.TIMESTAMP);
            }

            stmt.setString(8, pedido.getObservacoes());
            stmt.setInt(9, pedido.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Pedido não encontrado!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar pedido: " + e.getMessage(), e);
        }
    }

    // UPDATE STATUS
    public void atualizarStatus(int id, String status) {
        String sql = "UPDATE pedidos SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Pedido não encontrado!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status do pedido: " + e.getMessage(), e);
        }
    }

    // ASSIGN DELIVERY
    public void atribuirEntregador(int pedidoId, int entregadorId) {
        String sql = "UPDATE pedidos SET entregador_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entregadorId);
            stmt.setInt(2, pedidoId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Pedido não encontrado!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atribuir entregador: " + e.getMessage(), e);
        }
    }

    // DELETE
    public void deletar(int id) {
        String sql = "DELETE FROM pedidos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("Pedido não encontrado!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar pedido: " + e.getMessage(), e);
        }
    }

    // HELPER METHOD
    private Pedido mapResultSetToPedido(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setId(rs.getInt("id"));
        pedido.setClienteId(rs.getInt("cliente_id"));

        int entregadorId = rs.getInt("entregador_id");
        if (!rs.wasNull()) {
            pedido.setEntregadorId(entregadorId);
        }

        pedido.setEnderecoId(rs.getInt("endereco_id"));
        pedido.setValorTotal(rs.getBigDecimal("valor_total"));
        pedido.setStatus(rs.getString("status"));
        pedido.setFormaPagamento(rs.getString("forma_pagamento"));
        pedido.setDataPedido(rs.getTimestamp("data_pedido").toLocalDateTime());

        Timestamp dataEntrega = rs.getTimestamp("data_entrega");
        if (dataEntrega != null) {
            pedido.setDataEntrega(dataEntrega.toLocalDateTime());
        }

        pedido.setObservacoes(rs.getString("observacoes"));
        return pedido;
    }
}