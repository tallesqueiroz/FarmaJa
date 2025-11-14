package br.com.projeto.farmaja.dao;

import br.com.projeto.farmaja.database.DatabaseConnection;
import br.com.projeto.farmaja.model.Fornecedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FornecedorDAO {

    // CREATE
    public void criar(Fornecedor fornecedor) {
        String sql = """
            INSERT INTO fornecedores (nome, cnpj, telefone, email, ativo)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, fornecedor.getNome());
            stmt.setString(2, fornecedor.getCnpj());
            stmt.setString(3, fornecedor.getTelefone());
            stmt.setString(4, fornecedor.getEmail());
            stmt.setBoolean(5, fornecedor.getAtivo());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    fornecedor.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar fornecedor: " + e.getMessage(), e);
        }
    }

    // LEITURA POR ID
    public Fornecedor buscarPorId(int id) {
        String sql = "SELECT * FROM fornecedores WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToFornecedor(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar fornecedor: " + e.getMessage(), e);
        }

        return null;
    }

    // LEITURA POR CNPJ
    public Fornecedor buscarPorCnpj(String cnpj) {
        String sql = "SELECT * FROM fornecedores WHERE cnpj = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cnpj);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToFornecedor(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar fornecedor por CNPJ: " + e.getMessage(), e);
        }

        return null;
    }

    // LEITURA
    public List<Fornecedor> buscarTodos() {
        String sql = "SELECT * FROM fornecedores ORDER BY nome";
        List<Fornecedor> fornecedores = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                fornecedores.add(mapResultSetToFornecedor(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar fornecedores: " + e.getMessage(), e);
        }

        return fornecedores;
    }

    // LEITURA POR FORNECEDORES ATIVOS
    public List<Fornecedor> buscarAtivos() {
        String sql = "SELECT * FROM fornecedores WHERE ativo = TRUE ORDER BY nome";
        List<Fornecedor> fornecedores = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                fornecedores.add(mapResultSetToFornecedor(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar fornecedores ativos: " + e.getMessage(), e);
        }

        return fornecedores;
    }

    // UPDATE
    public void atualizar(Fornecedor fornecedor) {
        String sql = """
            UPDATE fornecedores 
            SET nome = ?, cnpj = ?, telefone = ?, email = ?, ativo = ?
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fornecedor.getNome());
            stmt.setString(2, fornecedor.getCnpj());
            stmt.setString(3, fornecedor.getTelefone());
            stmt.setString(4, fornecedor.getEmail());
            stmt.setBoolean(5, fornecedor.getAtivo());
            stmt.setInt(6, fornecedor.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Fornecedor não encontrado!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar fornecedor: " + e.getMessage(), e);
        }
    }

    // DELETE
    public void deletar(int id) {
        // Verifica se existem medicamentos associados
        String checkSql = "SELECT COUNT(*) FROM medicamentos WHERE fornecedor_id = ?";
        String deleteSql = "DELETE FROM fornecedores WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Verifica medicamentos associados
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, id);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new RuntimeException("Não é possível excluir! Existem medicamentos vinculados a este fornecedor.");
                }
            }

            // Deleta o fornecedor
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, id);
                int rowsAffected = deleteStmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new RuntimeException("Fornecedor não encontrado!");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar fornecedor: " + e.getMessage(), e);
        }
    }

    // ATIVAR/DESATIVAR
    public void ativarDesativar(int id, boolean ativo) {
        String sql = "UPDATE fornecedores SET ativo = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, ativo);
            stmt.setInt(2, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Fornecedor não encontrado!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ativar/desativar fornecedor: " + e.getMessage(), e);
        }
    }

    private Fornecedor mapResultSetToFornecedor(ResultSet rs) throws SQLException {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(rs.getInt("id"));
        fornecedor.setNome(rs.getString("nome"));
        fornecedor.setCnpj(rs.getString("cnpj"));
        fornecedor.setTelefone(rs.getString("telefone"));
        fornecedor.setEmail(rs.getString("email"));
        fornecedor.setAtivo(rs.getBoolean("ativo"));
        return fornecedor;
    }
}