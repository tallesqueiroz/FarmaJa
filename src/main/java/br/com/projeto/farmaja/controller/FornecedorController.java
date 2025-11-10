package br.com.projeto.farmaja.controller;

import br.com.projeto.farmaja.dao.FornecedorDAO;
import br.com.projeto.farmaja.model.Fornecedor;

import java.util.List;

/**
 * Orquestra as ações relacionadas a Fornecedores.
 */
public class FornecedorController {

    private final FornecedorDAO fornecedorDAO;

    // Injeção de dependência
    public FornecedorController(FornecedorDAO fornecedorDAO) {
        this.fornecedorDAO = fornecedorDAO;
    }

    public String cadastrar(Fornecedor fornecedor) {
        try {
            // Regra: CNPJ deve ser único
            if (fornecedorDAO.buscarPorCnpj(fornecedor.getCnpj()) != null) {
                return "Erro: Já existe um fornecedor com este CNPJ.";
            }

            fornecedorDAO.criar(fornecedor);
            return "Fornecedor cadastrado com sucesso! (ID: " + fornecedor.getId() + ")";
            // --- CORREÇÃO ---
        } catch (IllegalArgumentException e) {
            return "Erro de validação: " + e.getMessage();
        } catch (RuntimeException e) {
            return "Erro ao cadastrar: " + e.getMessage();
        }
    }

    public String atualizar(Fornecedor fornecedor) {
        try {
            fornecedorDAO.atualizar(fornecedor);
            return "Fornecedor atualizado com sucesso!";
            // --- CORREÇÃO ---
        } catch (IllegalArgumentException e) {
            return "Erro de validação: " + e.getMessage();
        } catch (RuntimeException e) {
            return "Erro ao atualizar: " + e.getMessage();
        }
    }

    public List<Fornecedor> listarFornecedoresAtivos() {
        try {
            return fornecedorDAO.buscarAtivos();
        } catch (RuntimeException e) {
            System.err.println("Erro ao listar fornecedores: " + e.getMessage());
            return null;
        }
    }

    public Fornecedor buscarPorCnpj(String cnpj) {
        try {
            return fornecedorDAO.buscarPorCnpj(cnpj);
        } catch (RuntimeException e) {
            System.err.println("Erro ao buscar por CNPJ: " + e.getMessage());
            return null;
        }
    }

    public String ativarDesativar(int id, boolean ativar) {
        try {
            fornecedorDAO.ativarDesativar(id, ativar);
            return "Fornecedor " + (ativar ? "ativado" : "desativado") + " com sucesso.";
        } catch (RuntimeException e) {
            // Verifica se o erro é de FK (medicamentos vinculados)
            if (e.getMessage().contains("medicamentos vinculados")) {
                return "Erro: Não é possível desativar. " + e.getMessage();
            }
            return "Erro ao mudar status: " + e.getMessage();
        }
    }

    public String deletar(int id) {
        try {
            fornecedorDAO.deletar(id);
            return "Fornecedor excluído com sucesso.";
        } catch (RuntimeException e) {
            // Captura a exceção de FK do DAO
            if (e.getMessage().contains("medicamentos vinculados")) {
                return "Erro: Não é possível excluir! Existem medicamentos vinculados a este fornecedor.";
            }
            return "Erro ao excluir: " + e.getMessage();
        }
    }
}