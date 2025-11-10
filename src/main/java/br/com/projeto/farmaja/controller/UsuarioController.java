package br.com.projeto.farmaja.controller;

import br.com.projeto.farmaja.dao.EnderecoDAO;
import br.com.projeto.farmaja.dao.UsuarioDAO;
import br.com.projeto.farmaja.model.Endereco;
import br.com.projeto.farmaja.model.Usuario;

import java.util.List;

/**
 * Orquestra as ações relacionadas a Usuários (Clientes, Entregadores, etc.)
 * e seus Endereços.
 */
public class UsuarioController {

    private final UsuarioDAO usuarioDAO;
    private final EnderecoDAO enderecoDAO;

    // Injeção de dependência de múltiplos DAOs
    public UsuarioController(UsuarioDAO usuarioDAO, EnderecoDAO enderecoDAO) {
        this.usuarioDAO = usuarioDAO;
        this.enderecoDAO = enderecoDAO;
    }

    /**
     * Realiza a autenticação (login) de um usuário.
     */
    public Usuario login(String email, String senha) {
        try {
            // Futuramente, a senha deve ser criptografada (hash)
            return usuarioDAO.autenticar(email, senha);
        } catch (RuntimeException e) {
            System.err.println("Erro ao autenticar: " + e.getMessage());
            return null;
        }
    }

    /**
     * Orquestra o cadastro de um novo usuário e seu primeiro endereço.
     */
    public String cadastrarNovoCliente(Usuario usuario, Endereco endereco) {
        try {
            // Regras de negócio
            if (usuarioDAO.buscarPorEmail(usuario.getEmail()) != null) {
                return "Erro: E-mail já cadastrado.";
            }
            if (usuarioDAO.buscarPorCpf(usuario.getCpf()) != null) {
                return "Erro: CPF já cadastrado.";
            }

            // 1. Cria o usuário
            usuarioDAO.criar(usuario);

            // 2. Associa o ID do usuário criado ao endereço
            // --- CORREÇÃO ---
            // Seu model Endereco tem validação no setUsuarioId.
            // Esta chamada irá validar se o ID é nulo, mas aqui ele já existe.
            endereco.setUsuarioId(usuario.getId());

            // 3. Cria o endereço
            enderecoDAO.criar(endereco);

            return "Cliente cadastrado com sucesso! (ID: " + usuario.getId() + ")";

            // --- CORREÇÃO ---
        } catch (IllegalArgumentException e) {
            return "Erro de validação: " + e.getMessage();
        } catch (RuntimeException e) {
            // Aqui seria o local ideal para implementar ROOLBACK de transação
            // Se o endereço falhar, o usuário deveria ser deletado.
            // (Simplicado por enquanto)
            return "Erro ao cadastrar cliente: " + e.getMessage();
        }
    }

    /**
     * Busca um usuário pelo ID.
     */
    public Usuario buscarUsuarioPorId(int id) {
        return usuarioDAO.buscarPorId(id);
    }

    /**
     * Lista usuários por tipo (CLIENTE, ENTREGADOR, ADMINISTRADOR).
     */
    public List<Usuario> listarUsuariosPorTipo(String tipo) {
        try {
            // --- CORREÇÃO ---
            // Garante que o tipo esteja em maiúsculas, conforme seu model espera
            return usuarioDAO.buscarPorTipo(tipo.toUpperCase());
        } catch (RuntimeException e) {
            System.err.println("Erro ao listar por tipo: " + e.getMessage());
            return null;
        }
    }

    /**
     * Adiciona um novo endereço para um usuário existente.
     */
    public String adicionarEndereco(Endereco endereco) {
        try {
            // Regra: Verifica se o usuário do endereço existe
            if (usuarioDAO.buscarPorId(endereco.getUsuarioId()) == null) {
                return "Erro: Usuário (ID: " + endereco.getUsuarioId() + ") não encontrado.";
            }
            enderecoDAO.criar(endereco);
            return "Endereço adicionado com sucesso!";
            // --- CORREÇÃO ---
        } catch (IllegalArgumentException e) {
            return "Erro de validação: " + e.getMessage();
        } catch (RuntimeException e) {
            return "Erro ao adicionar endereço: " + e.getMessage();
        }
    }

    /**
     * Lista todos os endereços de um usuário específico.
     */
    public List<Endereco> listarEnderecosPorUsuario(int usuarioId) {
        try {
            return enderecoDAO.buscarPorUsuario(usuarioId);
        } catch (RuntimeException e) {
            System.err.println("Erro ao listar endereços: " + e.getMessage());
            return null;
        }
    }
}