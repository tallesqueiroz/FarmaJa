package br.com.projeto.farmaja.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static ConnectionFactory connectionFactory;

    static {
        // PostgreSQL
        connectionFactory = new PostgreSQLConnectionFactory();
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
                    id SERIAL PRIMARY KEY,
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
                    id SERIAL PRIMARY KEY,
                    usuario_id INTEGER NOT NULL,
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
                    id SERIAL PRIMARY KEY,
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
                    id SERIAL PRIMARY KEY,
                    codigo VARCHAR(50) UNIQUE NOT NULL,
                    nome VARCHAR(200) NOT NULL,
                    descricao TEXT,
                    preco DECIMAL(10,2) NOT NULL,
                    estoque INTEGER NOT NULL DEFAULT 0,
                    estoque_minimo INTEGER NOT NULL DEFAULT 10,
                    fornecedor_id INTEGER,
                    requer_receita BOOLEAN DEFAULT FALSE,
                    ativo BOOLEAN DEFAULT TRUE,
                    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (fornecedor_id) REFERENCES fornecedores(id)
                )
            """);

            // Tabela de Pedidos
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS pedidos (
                    id SERIAL PRIMARY KEY,
                    cliente_id INTEGER NOT NULL,
                    entregador_id INTEGER,
                    endereco_id INTEGER NOT NULL,
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
                    id SERIAL PRIMARY KEY,
                    pedido_id INTEGER NOT NULL,
                    medicamento_id INTEGER NOT NULL,
                    quantidade INTEGER NOT NULL,
                    preco_unitario DECIMAL(10,2) NOT NULL,
                    subtotal DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
                    FOREIGN KEY (medicamento_id) REFERENCES medicamentos(id)
                )
            """);

            // Tabela de Histórico de Entregas
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS historico_entregas (
                    id SERIAL PRIMARY KEY,
                    pedido_id INTEGER NOT NULL,
                    entregador_id INTEGER,
                    status_anterior VARCHAR(30),
                    status_novo VARCHAR(30) NOT NULL,
                    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    observacao TEXT,
                    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
                    FOREIGN KEY (entregador_id) REFERENCES usuarios(id)
                )
            """);

            // Tabela de Favoritos
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS favoritos (
                    id SERIAL PRIMARY KEY,
                    usuario_id INTEGER NOT NULL,
                    medicamento_id INTEGER,
                    fornecedor_id INTEGER,
                    tipo_favorito VARCHAR(20) NOT NULL,
                    data_adicionado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
                    FOREIGN KEY (medicamento_id) REFERENCES medicamentos(id) ON DELETE CASCADE,
                    FOREIGN KEY (fornecedor_id) REFERENCES fornecedores(id) ON DELETE CASCADE,
                    CONSTRAINT chk_favorito CHECK (
                        (tipo_favorito = 'MEDICAMENTO' AND medicamento_id IS NOT NULL AND fornecedor_id IS NULL) OR
                        (tipo_favorito = 'FORNECEDOR' AND fornecedor_id IS NOT NULL AND medicamento_id IS NULL)
                    )
                )
            """);

            // Inserir usuário admin padrão (usando INSERT com ON CONFLICT para PostgreSQL)
            stmt.execute("""
                INSERT INTO usuarios (nome, email, senha, cpf, telefone, tipo_usuario)
                VALUES ('Administrador', 'admin@farmaja.com', 'admin123', '000.000.000-00', 
                        '(00) 00000-0000', 'ADMINISTRADOR')
                ON CONFLICT (email) DO NOTHING
            """);

            System.out.println("Banco de dados PostgreSQL inicializado com sucesso!");

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