package br.com.projeto.farmaja.view.Cliente;

import br.com.projeto.farmaja.controller.MedicamentoController;
import br.com.projeto.farmaja.model.Medicamento;
import br.com.projeto.farmaja.view.util.LeitorConsole;

import java.util.List;

public class CatalogoView {

    private final MedicamentoController medicamentoController;

    public CatalogoView(MedicamentoController controller) {
        this.medicamentoController = controller;
    }

    public void mostrar() {
        System.out.println("\n========================================");
        System.out.println("        CATÁLOGO DE MEDICAMENTOS        ");
        System.out.println("========================================");

        List<Medicamento> medicamentos = medicamentoController.listarMedicamentosAtivos();

        if (medicamentos == null || medicamentos.isEmpty()) {
            System.out.println("Que pena! Nenhum medicamento disponível no momento.");
            LeitorConsole.lerString("\nPressione ENTER para voltar...");
            return;
        }

        System.out.printf("%-5s | %-25s | %-10s | %-10s\n", "ID", "Nome", "Preço", "Estoque");
        System.out.println("------------------------------------------------------------");

        for (Medicamento med : medicamentos) {
            System.out.printf("%-5d | %-25s | R$ %-7.2f | %-5d\n",
                    med.getId(),
                    formatarNome(med.getNome()),
                    med.getPreco(),
                    med.getEstoque());
        }
        System.out.println("------------------------------------------------------------");

        LeitorConsole.lerString("\nPressione ENTER para voltar ao menu...");
    }

    private String formatarNome(String nome) {
        if (nome != null && nome.length() > 22) {
            return nome.substring(0, 22) + "...";
        }
        return nome;
    }
}