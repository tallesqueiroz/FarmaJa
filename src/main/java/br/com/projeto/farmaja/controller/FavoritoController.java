package br.com.projeto.farmaja.controller;

import br.com.projeto.farmaja.dao.FavoritoDAO;
import br.com.projeto.farmaja.dao.MedicamentoDAO;
import br.com.projeto.farmaja.dao.FornecedorDAO;
import br.com.projeto.farmaja.model.Favorito;
import br.com.projeto.farmaja.model.Medicamento;
import br.com.projeto.farmaja.model.Fornecedor;

import java.util.List;

public class FavoritoController {

    private final FavoritoDAO favoritoDAO;
    private final MedicamentoDAO medicamentoDAO;
    private final FornecedorDAO fornecedorDAO;

    public FavoritoController(FavoritoDAO favoritoDAO, MedicamentoDAO medicamentoDAO, FornecedorDAO fornecedorDAO) {
        this.favoritoDAO = favoritoDAO;
        this.medicamentoDAO = medicamentoDAO;
        this.fornecedorDAO = fornecedorDAO;
    }

    // ADICIONAR MEDICAMENTO AOS FAVORITOS
    public String adicionarMedicamentoFavorito(int usuarioId, int medicamentoId) {
        try {
            // Verifica se o medicamento existe
            Medicamento medicamento = medicamentoDAO.buscarPorId(medicamentoId);
            if (medicamento == null) {
                return "Erro: Medicamento não encontrado.";
            }

            // Verifica se já está nos favoritos
            if (favoritoDAO.existeFavorito(usuarioId, medicamentoId, null)) {
                return "Este medicamento já está nos seus favoritos!";
            }

            // Adiciona aos favoritos
            Favorito favorito = new Favorito(usuarioId, medicamentoId);
            favoritoDAO.criar(favorito);
            return "Medicamento '" + medicamento.getNome() + "' adicionado aos favoritos!";

        } catch (IllegalArgumentException e) {
            return "Erro de validação: " + e.getMessage();
        } catch (RuntimeException e) {
            return "Erro ao adicionar favorito: " + e.getMessage();
        }
    }

    // ADICIONAR FORNECEDOR AOS FAVORITOS
    public String adicionarFornecedorFavorito(int usuarioId, int fornecedorId) {
        try {
            // Verifica se o fornecedor existe
            Fornecedor fornecedor = fornecedorDAO.buscarPorId(fornecedorId);
            if (fornecedor == null) {
                return "Erro: Fornecedor não encontrado.";
            }

            // Verifica se já está nos favoritos
            if (favoritoDAO.existeFavorito(usuarioId, null, fornecedorId)) {
                return "Este fornecedor já está nos seus favoritos!";
            }

            // Adiciona aos favoritos
            Favorito favorito = Favorito.criarFavoritoFornecedor(usuarioId, fornecedorId);
            favoritoDAO.criar(favorito);
            return "Fornecedor '" + fornecedor.getNome() + "' adicionado aos favoritos!";

        } catch (IllegalArgumentException e) {
            return "Erro de validação: " + e.getMessage();
        } catch (RuntimeException e) {
            return "Erro ao adicionar favorito: " + e.getMessage();
        }
    }

    // REMOVER MEDICAMENTO DOS FAVORITOS
    public String removerMedicamentoFavorito(int usuarioId, int medicamentoId) {
        try {
            if (!favoritoDAO.existeFavorito(usuarioId, medicamentoId, null)) {
                return "Este medicamento não está nos seus favoritos.";
            }

            favoritoDAO.deletarPorMedicamento(usuarioId, medicamentoId);
            return "Medicamento removido dos favoritos.";

        } catch (RuntimeException e) {
            return "Erro ao remover favorito: " + e.getMessage();
        }
    }

    // REMOVER FORNECEDOR DOS FAVORITOS
    public String removerFornecedorFavorito(int usuarioId, int fornecedorId) {
        try {
            if (!favoritoDAO.existeFavorito(usuarioId, null, fornecedorId)) {
                return "Este fornecedor não está nos seus favoritos.";
            }

            favoritoDAO.deletarPorFornecedor(usuarioId, fornecedorId);
            return "Fornecedor removido dos favoritos.";

        } catch (RuntimeException e) {
            return "Erro ao remover favorito: " + e.getMessage();
        }
    }

    // LISTAR MEDICAMENTOS FAVORITOS
    public List<Favorito> listarMedicamentosFavoritos(int usuarioId) {
        try {
            return favoritoDAO.buscarMedicamentosFavoritos(usuarioId);
        } catch (RuntimeException e) {
            System.err.println("Erro ao listar medicamentos favoritos: " + e.getMessage());
            return null;
        }
    }

    // LISTAR FORNECEDORES FAVORITOS
    public List<Favorito> listarFornecedoresFavoritos(int usuarioId) {
        try {
            return favoritoDAO.buscarFornecedoresFavoritos(usuarioId);
        } catch (RuntimeException e) {
            System.err.println("Erro ao listar fornecedores favoritos: " + e.getMessage());
            return null;
        }
    }

    // VERIFICAR SE UM MEDICAMENTO É FAVORITO
    public boolean isMedicamentoFavorito(int usuarioId, int medicamentoId) {
        try {
            return favoritoDAO.existeFavorito(usuarioId, medicamentoId, null);
        } catch (RuntimeException e) {
            return false;
        }
    }

    // VERIFICAR SE UM FORNECEDOR É FAVORITO
    public boolean isFornecedorFavorito(int usuarioId, int fornecedorId) {
        try {
            return favoritoDAO.existeFavorito(usuarioId, null, fornecedorId);
        } catch (RuntimeException e) {
            return false;
        }
    }
}