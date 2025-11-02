package br.com.projeto.farmaja.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static ConnectionFactory connectionFactory;

    static {
        // Inicializa com H2 por padrão
        connectionFactory = new H2ConnectionFactory();
    }

    public static void setConnectionFactory(ConnectionFactory factory) {
        connectionFactory = factory;
    }

    public static Connection getConnection() throws SQLException {
        return connectionFactory.getConnection();
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Tabela de Usuários
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS usuarios (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nome VARCHAR(100) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    senha VARCHAR(100) NOT NULL,
                    cpf VARCHAR(14) UNIQUE NOT NULL,
                    telefone VARCHAR(15),
                    tipo_usuario VARCHAR(20) NOT NULL,
                    ativo BOOLEAN DEFAULT TRUE,
                    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Tabela de Endereços
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS enderecos (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    usuario_id INT NOT NULL,
                    rua VARCHAR(200) NOT NULL,
                    numero VARCHAR(10) NOT NULL,
                    complemento VARCHAR(100),
                    bairro VARCHAR(100) NOT NULL,
                    cidade VARCHAR(100) NOT NULL,
                    estado VARCHAR(2) NOT NULL,
                    cep VARCHAR(9) NOT NULL,
                    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
                )
            """);

            // Tabela de Fornecedores
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS fornecedores (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nome VARCHAR(100) NOT NULL,
                    cnpj VARCHAR(18) UNIQUE NOT NULL,
                    telefone VARCHAR(15),
                    email VARCHAR(100),
                    ativo BOOLEAN DEFAULT TRUE
                )
            """);

            // Tabela de Medicamentos
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS medicamentos (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    codigo VARCHAR(50) UNIQUE NOT NULL,
                    nome VARCHAR(200) NOT NULL,
                    descricao TEXT,
                    preco DECIMAL(10,2) NOT NULL,
                    estoque INT NOT NULL DEFAULT 0,
                    estoque_minimo INT NOT NULL DEFAULT 10,
                    fornecedor_id INT,
                    requer_receita BOOLEAN DEFAULT FALSE,
                    ativo BOOLEAN DEFAULT TRUE,
                    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (fornecedor_id) REFERENCES fornecedores(id)
                )
            """);

            // Tabela de Pedidos
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS pedidos (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    cliente_id INT NOT NULL,
                    entregador_id INT,
                    endereco_id INT NOT NULL,
                    valor_total DECIMAL(10,2) NOT NULL,
                    status VARCHAR(30) NOT NULL,
                    forma_pagamento VARCHAR(30),
                    data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    data_entrega TIMESTAMP,
                    observacoes TEXT,
                    FOREIGN KEY (cliente_id) REFERENCES usuarios(id),
                    FOREIGN KEY (entregador_id) REFERENCES usuarios(id),
                    FOREIGN KEY (endereco_id) REFERENCES enderecos(id)
                )
            """);

            // Tabela de Itens do Pedido
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS itens_pedido (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    pedido_id INT NOT NULL,
                    medicamento_id INT NOT NULL,
                    quantidade INT NOT NULL,
                    preco_unitario DECIMAL(10,2) NOT NULL,
                    subtotal DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
                    FOREIGN KEY (medicamento_id) REFERENCES medicamentos(id)
                )
            """);

            // Tabela de Histórico de Entregas
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS historico_entregas (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    pedido_id INT NOT NULL,
                    entregador_id INT,
                    status_anterior VARCHAR(30),
                    status_novo VARCHAR(30) NOT NULL,
                    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    observacao TEXT,
                    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
                    FOREIGN KEY (entregador_id) REFERENCES usuarios(id)
                )
            """);

            // Inserir usuário admin padrão
            stmt.execute("""
                MERGE INTO usuarios (nome, email, senha, cpf, telefone, tipo_usuario)
                KEY(email)
                VALUES ('Administrador', 'admin@farmaja.com', 'admin123', '000.000.000-00', 
                        '(00) 00000-0000', 'ADMINISTRADOR')
            """);

            System.out.println("Banco de dados inicializado com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        try {
            connectionFactory.closeConnection();
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}