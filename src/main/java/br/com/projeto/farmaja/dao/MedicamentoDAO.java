package br.com.projeto.farmaja.dao;

import br.com.projeto.farmaja.database.DatabaseConnection;
import br.com.projeto.farmaja.model.Medicamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoDAO {

    // CREATE
    public void criar(Medicamento medicamento) {
        String sql = """
            INSERT INTO medicamentos (codigo, nome, descricao, preco, estoque, 
                                     estoque_minimo, fornecedor_id, requer_receita, ativo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, medicamento.getCodigo());
            stmt.setString(2, medicamento.getNome());
            stmt.setString(3, medicamento.getDescricao());
            stmt.setBigDecimal(4, medicamento.getPreco());
            stmt.setInt(5, medicamento.getEstoque());
            stmt.setInt(6, medicamento.getEstoqueMinimo());

            if (medicamento.getFornecedorId() != null) {
                stmt.setInt(7, medicamento.getFornecedorId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.setBoolean(8, medicamento.isRequerReceita());
            stmt.setBoolean(9, medicamento.isAtivo());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    medicamento.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar medicamento: " + e.getMessage(), e);
        }
    }

    // LEITURA POR ID
    public Medicamento buscarPorId(int id) {
        String sql = "SELECT * FROM medicamentos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToMedicamento(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar medicamento: " + e.getMessage(), e);
        }

        return null;
    }

    // LEITURA POR CODIGO
    public Medicamento buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM medicamentos WHERE codigo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToMedicamento(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar medicamento por código: " + e.getMessage(), e);
        }

        return null;
    }

    // LEITURA
    public List<Medicamento> buscarTodos() {
        String sql = "SELECT * FROM medicamentos ORDER BY nome";
        List<Medicamento> medicamentos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                medicamentos.add(mapResultSetToMedicamento(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar medicamentos: " + e.getMessage(), e);
        }

        return medicamentos;
    }

    // LEITURA POR MEDICAMENTOS ATIVOS
    public List<Medicamento> buscarAtivos() {
        String sql = "SELECT * FROM medicamentos WHERE ativo = TRUE ORDER BY nome";
        List<Medicamento> medicamentos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                medicamentos.add(mapResultSetToMedicamento(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar medicamentos ativos: " + e.getMessage(), e);
        }

        return medicamentos;
    }

    // BUSCA POR NOME
    public List<Medicamento> buscarPorNome(String nome) {
        String sql = "SELECT * FROM medicamentos WHERE LOWER(nome) LIKE LOWER(?) ORDER BY nome";
        List<Medicamento> medicamentos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                medicamentos.add(mapResultSetToMedicamento(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar medicamentos por nome: " + e.getMessage(), e);
        }

        return medicamentos;
    }

    // LEITURA DE ESTOQUE BAIXO
    public List<Medicamento> buscarEstoqueBaixo() {
        String sql = "SELECT * FROM medicamentos WHERE estoque <= estoque_minimo AND ativo = TRUE ORDER BY estoque";
        List<Medicamento> medicamentos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                medicamentos.add(mapResultSetToMedicamento(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar medicamentos com estoque baixo: " + e.getMessage(), e);
        }

        return medicamentos;
    }

    // UPDATE
    public void atualizar(Medicamento medicamento) {
        String sql = """
            UPDATE medicamentos 
            SET codigo = ?, nome = ?, descricao = ?, preco = ?, estoque = ?,
                estoque_minimo = ?, fornecedor_id = ?, requer_receita = ?, ativo = ?
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, medicamento.getCodigo());
            stmt.setString(2, medicamento.getNome());
            stmt.setString(3, medicamento.getDescricao());
            stmt.setBigDecimal(4, medicamento.getPreco());
            stmt.setInt(5, medicamento.getEstoque());
            stmt.setInt(6, medicamento.getEstoqueMinimo());

            if (medicamento.getFornecedorId() != null) {
                stmt.setInt(7, medicamento.getFornecedorId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.setBoolean(8, medicamento.isRequerReceita());
            stmt.setBoolean(9, medicamento.isAtivo());
            stmt.setInt(10, medicamento.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Medicamento não encontrado!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar medicamento: " + e.getMessage(), e);
        }
    }

    // ATUALIZAR ESTOQUE
    public void atualizarEstoque(int id, int quantidade) {
        String sql = "UPDATE medicamentos SET estoque = estoque + ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantidade);
            stmt.setInt(2, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Medicamento não encontrado!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar estoque: " + e.getMessage(), e);
        }
    }

    // DELETE
    public void deletar(int id) {
        String sql = "DELETE FROM medicamentos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("Medicamento não encontrado!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar medicamento: " + e.getMessage(), e);
        }
    }

    // ATIVAR/DESATIVAR
    public void ativarDesativar(int id, boolean ativo) {
        String sql = "UPDATE medicamentos SET ativo = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, ativo);
            stmt.setInt(2, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Medicamento não encontrado!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ativar/desativar medicamento: " + e.getMessage(), e);
        }
    }

    private Medicamento mapResultSetToMedicamento(ResultSet rs) throws SQLException {
        Medicamento medicamento = new Medicamento();
        medicamento.setId(rs.getInt("id"));
        medicamento.setCodigo(rs.getString("codigo"));
        medicamento.setNome(rs.getString("nome"));
        medicamento.setDescricao(rs.getString("descricao"));
        medicamento.setPreco(rs.getBigDecimal("preco"));
        medicamento.setEstoque(rs.getInt("estoque"));
        medicamento.setEstoqueMinimo(rs.getInt("estoque_minimo"));

        int fornecedorId = rs.getInt("fornecedor_id");
        if (!rs.wasNull()) {
            medicamento.setFornecedorId(fornecedorId);
        }

        medicamento.setRequerReceita(rs.getBoolean("requer_receita"));
        medicamento.setAtivo(rs.getBoolean("ativo"));
        medicamento.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        return medicamento;
    }
}
