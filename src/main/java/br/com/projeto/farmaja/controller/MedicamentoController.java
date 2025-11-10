package br.com.projeto.farmaja.controller;

import br.com.projeto.farmaja.dao.MedicamentoDAO;
import br.com.projeto.farmaja.model.Medicamento;

import java.math.BigDecimal;
import java.util.List;

/**
 * Orquestra as ações relacionadas a Medicamentos,
 * recebendo dados da View e interagindo com o DAO.
 */
public class MedicamentoController {

    private final MedicamentoDAO medicamentoDAO;

    // Injeção de dependência via construtor
    public MedicamentoController(MedicamentoDAO medicamentoDAO) {
        this.medicamentoDAO = medicamentoDAO;
    }

    /**
     * Valida e cadastra um novo medicamento.
     */
    public String cadastrarMedicamento(Medicamento medicamento) {
        try {
            // --- CORREÇÃO ---
            // A validação de preço negativo já é feita pelo seu model Medicamento
            // no construtor ou no setPreco(). Esta validação é redundante mas inofensiva.
            if (medicamento.getPreco().compareTo(BigDecimal.ZERO) < 0) {
                return "Erro: O preço não pode ser negativo.";
            }

            // Regra de negócio: Verifica se o código já existe
            if (medicamentoDAO.buscarPorCodigo(medicamento.getCodigo()) != null) {
                return "Erro: Já existe um medicamento com este código.";
            }

            medicamentoDAO.criar(medicamento);
            return "Medicamento cadastrado com sucesso! (ID: " + medicamento.getId() + ")";

            // --- CORREÇÃO ---
            // Captura exceções de validação vindas do Model
        } catch (IllegalArgumentException e) {
            return "Erro de validação: " + e.getMessage();
        } catch (RuntimeException e) {
            return "Erro ao cadastrar medicamento: " + e.getMessage();
        }
    }

    /**
     * Atualiza um medicamento existente.
     */
    public String atualizarMedicamento(Medicamento medicamento) {
        try {
            medicamentoDAO.atualizar(medicamento);
            return "Medicamento atualizado com sucesso!";
            // --- CORREÇÃO ---
        } catch (IllegalArgumentException e) {
            return "Erro de validação: " + e.getMessage();
        } catch (RuntimeException e) {
            return "Erro ao atualizar: " + e.getMessage();
        }
    }

    /**
     * Busca um medicamento pelo seu código único.
     */
    public Medicamento buscarPorCodigo(String codigo) {
        try {
            return medicamentoDAO.buscarPorCodigo(codigo);
        } catch (RuntimeException e) {
            System.err.println("Erro ao buscar por código: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lista todos os medicamentos que estão ativos.
     */
    public List<Medicamento> listarMedicamentosAtivos() {
        try {
            return medicamentoDAO.buscarAtivos();
        } catch (RuntimeException e) {
            System.err.println("Erro ao listar medicamentos ativos: " + e.getMessage());
            return null;
        }
    }

    /**
     * Relatório de medicamentos com estoque baixo (igual ou abaixo do mínimo).
     */
    public List<Medicamento> listarEstoqueBaixo() {
        try {
            return medicamentoDAO.buscarEstoqueBaixo();
        } catch (RuntimeException e) {
            System.err.println("Erro ao listar estoque baixo: " + e.getMessage());
            return null;
        }
    }

    /**
     * Adiciona ou remove estoque de um medicamento.
     */
    public String atualizarEstoque(int id, int quantidade) {
        try {
            // Regra de negócio: Verifica se o medicamento existe
            Medicamento med = medicamentoDAO.buscarPorId(id);
            if (med == null) {
                return "Erro: Medicamento não encontrado.";
            }

            // --- CORREÇÃO ---
            // A lógica de estoque negativo é tratada pelo model
            // Vamos usar os métodos do seu model para isso
            if (quantidade > 0) {
                med.aumentarEstoque(quantidade);
            } else if (quantidade < 0) {
                med.reduzirEstoque(Math.abs(quantidade)); // reduzirEstoque espera valor positivo
            } else {
                return "Nenhuma alteração de estoque.";
            }

            // Atualiza o estoque no banco
            medicamentoDAO.atualizarEstoque(id, quantidade);
            return "Estoque atualizado com sucesso!";

            // --- CORREÇÃO ---
        } catch (IllegalStateException | IllegalArgumentException e) {
            // Captura erros do model (ex: "Estoque insuficiente")
            return "Erro: " + e.getMessage();
        } catch (RuntimeException e) {
            return "Erro ao atualizar estoque: " + e.getMessage();
        }
    }

    /**
     * Altera o status (ativo/inativo) de um medicamento.
     */
    public String ativarDesativar(int id, boolean ativar) {
        try {
            medicamentoDAO.ativarDesativar(id, ativar);
            return "Medicamento " + (ativar ? "ativado" : "desativado") + " com sucesso!";
        } catch (RuntimeException e) {
            return "Erro ao mudar status: " + e.getMessage();
        }
    }
}