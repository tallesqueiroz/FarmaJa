package br.com.projeto.farmaja;

import br.com.projeto.farmaja.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDatabase {

    public static void main(String[] args) {
        System.out.println("=== TESTE DE CONEXÃO COM BANCO DE DADOS ===\n");

        try {
            // Teste 1: Inicializar o banco de dados
            System.out.println("1. Inicializando banco de dados...");
            DatabaseConnection.initializeDatabase();
            System.out.println("✓ Banco de dados inicializado com sucesso!\n");

            // Teste 2: Obter conexão
            System.out.println("2. Testando conexão...");
            Connection conn = DatabaseConnection.getConnection();

            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Conexão estabelecida com sucesso!");
                System.out.println("   Database: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("   Versão: " + conn.getMetaData().getDatabaseProductVersion());
                System.out.println();
            } else {
                System.out.println("✗ Falha ao estabelecer conexão!");
                return;
            }

            // Teste 3: Verificar tabelas criadas
            System.out.println("3. Verificando tabelas criadas...");
            Statement stmt = conn.createStatement();

            String[] tabelas = {
                    "usuarios", "enderecos", "fornecedores",
                    "medicamentos", "pedidos", "itens_pedido",
                    "historico_entregas"
            };

            for (String tabela : tabelas) {
                String sql = "SELECT COUNT(*) as total FROM " + tabela;
                ResultSet rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    System.out.println("   ✓ Tabela '" + tabela + "' existe (registros: " + rs.getInt("total") + ")");
                }
                rs.close();
            }
            System.out.println();

            // Teste 4: Verificar usuário admin padrão
            System.out.println("4. Verificando usuário administrador padrão...");
            String sqlAdmin = "SELECT * FROM usuarios WHERE email = 'admin@farmaja.com'";
            ResultSet rsAdmin = stmt.executeQuery(sqlAdmin);

            if (rsAdmin.next()) {
                System.out.println("   ✓ Usuário admin encontrado:");
                System.out.println("      ID: " + rsAdmin.getInt("id"));
                System.out.println("      Nome: " + rsAdmin.getString("nome"));
                System.out.println("      Email: " + rsAdmin.getString("email"));
                System.out.println("      CPF: " + rsAdmin.getString("cpf"));
                System.out.println("      Tipo: " + rsAdmin.getString("tipo_usuario"));
                System.out.println("      Ativo: " + rsAdmin.getBoolean("ativo"));
            } else {
                System.out.println("   ✗ Usuário admin não encontrado!");
            }
            rsAdmin.close();
            System.out.println();

            // Teste 5: Inserir e consultar um registro de teste
            System.out.println("5. Testando inserção de dados...");
            String sqlInsert = """
                INSERT INTO fornecedores (nome, cnpj, telefone, email, ativo)
                VALUES ('Fornecedor Teste', '12.345.678/0001-90', '(11) 98765-4321', 
                        'teste@fornecedor.com', TRUE)
            """;
            int rowsAffected = stmt.executeUpdate(sqlInsert);

            if (rowsAffected > 0) {
                System.out.println("   ✓ Fornecedor de teste inserido com sucesso!");

                String sqlSelect = "SELECT * FROM fornecedores WHERE cnpj = '12.345.678/0001-90'";
                ResultSet rsFornecedor = stmt.executeQuery(sqlSelect);

                if (rsFornecedor.next()) {
                    System.out.println("      ID: " + rsFornecedor.getInt("id"));
                    System.out.println("      Nome: " + rsFornecedor.getString("nome"));
                    System.out.println("      CNPJ: " + rsFornecedor.getString("cnpj"));
                    System.out.println("   ✓ Consulta realizada com sucesso!");
                }
                rsFornecedor.close();
            }
            System.out.println();

            // Teste 6: Deletar registro de teste
            System.out.println("6. Limpando dados de teste...");
            String sqlDelete = "DELETE FROM fornecedores WHERE cnpj = '12.345.678/0001-90'";
            int rowsDeleted = stmt.executeUpdate(sqlDelete);
            System.out.println("   ✓ Registro de teste deletado (" + rowsDeleted + " linha(s) afetada(s))");
            System.out.println();

            stmt.close();

            // Teste 7: Fechar conexão
            System.out.println("7. Fechando conexão...");
            DatabaseConnection.closeConnection();

            if (conn.isClosed()) {
                System.out.println("✓ Conexão fechada com sucesso!\n");
            }

            System.out.println("=== TODOS OS TESTES PASSARAM COM SUCESSO! ===");

        } catch (Exception e) {
            System.err.println("\n✗ ERRO DURANTE OS TESTES:");
            System.err.println("   Mensagem: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
