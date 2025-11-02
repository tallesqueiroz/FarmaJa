package br.com.projeto.farmaja.dao;

import br.com.projeto.farmaja.database.DatabaseConnection;
import br.com.projeto.farmaja.model.ItemPedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemPedidoDAO {

    // CREATE
    public void criar(ItemPedido itemPedido) {
        String sql = """
            INSERT INTO itens_pedido (pedido_id, medicamento_id, quantidade, preco_unitario, subtotal)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, itemPedido.getPedidoId());
            stmt.setInt(2, itemPedido.getMedicamentoId());
            stmt.setInt(3, itemPedido.getQuantidade());
            stmt.setBigDecimal(4, itemPedido.getPrecoUnitario());
            stmt.setBigDecimal(5, itemPedido.getSubtotal());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    itemPedido.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar item do pedido: " + e.getMessage(), e);
        }
    }

    // LEITURA POR ID
    public ItemPedido buscarPorId(int id) {
        String sql = "SELECT * FROM itens_pedido WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToItemPedido(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar item do pedido: " + e.getMessage(), e);
        }

        return null;
    }

    // LEITURA POR PEDIDO
    public List<ItemPedido> buscarPorPedido(int pedidoId) {
        String sql = "SELECT * FROM itens_pedido WHERE pedido_id = ?";
        List<ItemPedido> itens = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pedidoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(mapResultSetToItemPedido(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar itens do pedido: " + e.getMessage(), e);
        }

        return itens;
    }

    // UPDATE
    public void atualizar(ItemPedido itemPedido) {
        String sql = """
            UPDATE itens_pedido 
            SET pedido_id = ?, medicamento_id = ?, quantidade = ?, 
                preco_unitario = ?, subtotal = ?
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemPedido.getPedidoId());
            stmt.setInt(2, itemPedido.getMedicamentoId());
            stmt.setInt(3, itemPedido.getQuantidade());
            stmt.setBigDecimal(4, itemPedido.getPrecoUnitario());
            stmt.setBigDecimal(5, itemPedido.getSubtotal());
            stmt.setInt(6, itemPedido.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Item do pedido não encontrado!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar item do pedido: " + e.getMessage(), e);
        }
    }

    // DELETE
    public void deletar(int id) {
        String sql = "DELETE FROM itens_pedido WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("Item do pedido não encontrado!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar item do pedido: " + e.getMessage(), e);
        }
    }

    // DELETE POR PEDIDO
    public void deletarPorPedido(int pedidoId) {
        String sql = "DELETE FROM itens_pedido WHERE pedido_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pedidoId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar itens do pedido: " + e.getMessage(), e);
        }
    }

    private ItemPedido mapResultSetToItemPedido(ResultSet rs) throws SQLException {
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setId(rs.getInt("id"));
        itemPedido.setPedidoId(rs.getInt("pedido_id"));
        itemPedido.setMedicamentoId(rs.getInt("medicamento_id"));
        itemPedido.setQuantidade(rs.getInt("quantidade"));
        itemPedido.setPrecoUnitario(rs.getBigDecimal("preco_unitario"));
        itemPedido.setSubtotal(rs.getBigDecimal("subtotal"));
        return itemPedido;
    }
}
