package br.com.projeto.farmaja.controller;

import br.com.projeto.farmaja.dao.MedicamentoDAO;
import br.com.projeto.farmaja.model.Medicamento;

import java.math.BigDecimal;
import java.util.List;

public class MedicamentoController {

    private final MedicamentoDAO medicamentoDAO;

    public MedicamentoController(MedicamentoDAO medicamentoDAO) {
        this.medicamentoDAO = medicamentoDAO;
    }
    public String cadastrarMedicamento(Medicamento medicamento) {
        try {
            if (medicamento.getPreco().compareTo(BigDecimal.ZERO) < 0) {
                return "Erro: O preço não pode ser negativo.";
            }
            if (medicamentoDAO.buscarPorCodigo(medicamento.getCodigo()) != null) {
                return "Erro: Já existe um medicamento com este código.";
            }

            medicamentoDAO.criar(medicamento);
            return "Medicamento cadastrado com sucesso! (ID: " + medicamento.getId() + ")";

        } catch (Exception e) {
            return "Erro ao cadastrar: " + e.getMessage();
        }
    }

    public List<Medicamento> listarMedicamentosAtivos() {
        return medicamentoDAO.buscarAtivos();
    }

    public Medicamento buscarPorCodigo(String codigo) {
        return medicamentoDAO.buscarPorCodigo(codigo);
    }

    public List<Medicamento> listarEstoqueBaixo() {
        return medicamentoDAO.buscarEstoqueBaixo();
    }

    public String atualizarEstoque(int id, int qtd) {
        // ... (Lógica de estoque)
        medicamentoDAO.atualizarEstoque(id, qtd);
        return "Estoque atualizado.";
    }

    public String ativarDesativar(int id, boolean ativar) {
        medicamentoDAO.ativarDesativar(id, ativar);
        return "Status alterado.";
    }
}