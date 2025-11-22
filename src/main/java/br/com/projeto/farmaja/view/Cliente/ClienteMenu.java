package br.com.projeto.farmaja.view.Cliente;

import br.com.projeto.farmaja.controller.FavoritoController;
import br.com.projeto.farmaja.controller.MedicamentoController;
import br.com.projeto.farmaja.controller.PedidoController;
import br.com.projeto.farmaja.controller.UsuarioController;
import br.com.projeto.farmaja.dao.*;
import br.com.projeto.farmaja.model.*;
import br.com.projeto.farmaja.view.util.LeitorConsole;

import java.util.ArrayList;
import java.util.List;

public class ClienteMenu {

    // --- Dependências ---
    private final MedicamentoDAO medicamentoDAO;
    private final PedidoDAO pedidoDAO;
    private final ItemPedidoDAO itemPedidoDAO;
    private final UsuarioDAO usuarioDAO;
    private final EnderecoDAO enderecoDAO;
    private final HistoricoEntregaDAO historicoEntregaDAO;
    private final FornecedorDAO fornecedorDAO;
    private final FavoritoDAO favoritoDAO;

    private final MedicamentoController medicamentoController;
    private final PedidoController pedidoController;
    private final UsuarioController usuarioController;
    private final FavoritoController favoritoController;

    private final CatalogoView catalogoView;

    private final List<ItemPedido> carrinho;
    private Usuario clienteLogado;
    public ClienteMenu(Usuario usuarioLogado) {
        this.medicamentoDAO = new MedicamentoDAO();
        this.pedidoDAO = new PedidoDAO();
        this.itemPedidoDAO = new ItemPedidoDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.enderecoDAO = new EnderecoDAO();
        this.historicoEntregaDAO = new HistoricoEntregaDAO();
        this.fornecedorDAO = new FornecedorDAO();
        this.favoritoDAO = new FavoritoDAO();

        this.medicamentoController = new MedicamentoController(medicamentoDAO);
        this.usuarioController = new UsuarioController(usuarioDAO, enderecoDAO);
        this.pedidoController = new PedidoController(
                pedidoDAO, itemPedidoDAO, medicamentoDAO, usuarioDAO, historicoEntregaDAO
        );
        this.favoritoController = new FavoritoController(favoritoDAO, medicamentoDAO, fornecedorDAO);

        this.catalogoView = new CatalogoView(medicamentoController);

        this.carrinho = new ArrayList<>();
        this.clienteLogado = usuarioLogado;
    }

    public void mostrar() {
        System.out.println("Bem-vindo(a), " + clienteLogado.getNome());

        int opcao;
        do {
            String infoCarrinho = carrinho.isEmpty() ? "" : " (" + carrinho.size() + " itens)";

            System.out.println("\n=== MENU CLIENTE ===");
            System.out.println("1. Ver catálogo de medicamentos");
            System.out.println("2. Buscar medicamento por nome ou código");
            System.out.println("3. Adicionar ao carrinho" + infoCarrinho);
            System.out.println("4. Ver carrinho / Finalizar pedido");
            System.out.println("5. Meus pedidos / Ver status");
            System.out.println("6. Perfil (editar dados)");
            System.out.println("7. Meus Favoritos");
            System.out.println("0. Logout (Sair)");

            opcao = LeitorConsole.lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1: catalogoView.mostrar(); break;
                case 2: buscarMedicamento(); break;
                case 3: adicionarAoCarrinho(); break;
                case 4: menuCarrinho(); break;
                case 5: verMeusPedidos(); break;
                case 6: editarPerfil(); break;
                case 7: menuFavoritos(); break;
                case 0:
                    System.out.println("Fazendo logout... Até logo!");
                    break;
                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        } while (opcao != 0);
    }

    // MÉTODOS DE LÓGICA (sem mudanças)

    private void editarPerfil() {
        System.out.println("\n--- Editar Perfil ---");
        System.out.println("Nome: " + clienteLogado.getNome());
        System.out.println("Email: " + clienteLogado.getEmail());
        System.out.println("------------------------");
        System.out.println("1. Alterar Nome");
        System.out.println("2. Alterar Senha");
        System.out.println("3. Cadastrar Novo Endereço");
        System.out.println("0. Voltar");

        int op = LeitorConsole.lerInteiro("Opção: ");

        if (op == 1) {
            String novoNome = LeitorConsole.lerString("Digite o novo nome: ");
            clienteLogado.setNome(novoNome);
            usuarioDAO.atualizar(clienteLogado);
            System.out.println("Nome alterado com sucesso!");

        } else if (op == 2) {
            String novaSenha = LeitorConsole.lerString("Digite a nova senha: ");
            clienteLogado.setSenha(novaSenha);
            usuarioDAO.atualizar(clienteLogado);
            System.out.println("Senha alterada com sucesso!");

        } else if (op == 3) {
            System.out.println("\n--- Novo Endereço ---");
            String rua = LeitorConsole.lerString("Rua: ");
            String numero = LeitorConsole.lerString("Número: ");
            String bairro = LeitorConsole.lerString("Bairro: ");
            String cidade = LeitorConsole.lerString("Cidade: ");
            String uf = LeitorConsole.lerString("Estado (UF): ");
            String cep = LeitorConsole.lerString("CEP: ");
            String complemento = LeitorConsole.lerString("Complemento (opcional): ");

            Endereco novoEnd = new Endereco(
                    clienteLogado.getId(),
                    rua, numero, bairro, cidade, uf, cep, complemento
            );

            try {
                String res = usuarioController.adicionarEndereco(novoEnd);
                System.out.println("\n" + res);
            } catch (Exception e) {
                System.out.println("Erro ao salvar endereço: " + e.getMessage());
            }
        }
    }

    private void menuCarrinho() {
        System.out.println("\n--- 🛒 Seu Carrinho de Compras ---");

        if (carrinho.isEmpty()) {
            System.out.println("Seu carrinho está vazio. Adicione itens antes de finalizar.");
            LeitorConsole.lerString("Pressione ENTER para voltar...");
            return;
        }

        listarItensCarrinho();

        System.out.println("\nO que deseja fazer?");
        System.out.println("1. Finalizar Compra (Checkout)");
        System.out.println("2. Limpar Carrinho");
        System.out.println("0. Voltar e Comprar Mais");

        int op = LeitorConsole.lerInteiro("Opção: ");

        if (op == 1) {
            finalizarPedido();
        } else if (op == 2) {
            if (LeitorConsole.confirmar("Tem certeza que deseja esvaziar o carrinho?")) {
                carrinho.clear();
                System.out.println("Carrinho limpo.");
            }
        }
    }

    private void listarItensCarrinho() {
        double totalGeral = 0;
        System.out.println("------------------------------------------------");
        for (int i = 0; i < carrinho.size(); i++) {
            ItemPedido item = carrinho.get(i);
            Medicamento med = medicamentoDAO.buscarPorId(item.getMedicamentoId());
            String nome = (med != null) ? med.getNome() : "Item ID " + item.getMedicamentoId();

            System.out.printf("%d. %s | %dx R$%.2f = R$%.2f\n",
                    (i + 1), nome, item.getQuantidade(), item.getPrecoUnitario(), item.getSubtotal());

            totalGeral += item.getSubtotal().doubleValue();
        }
        System.out.println("------------------------------------------------");
        System.out.printf("TOTAL A PAGAR: R$ %.2f\n", totalGeral);
    }

    private void finalizarPedido() {
        System.out.println("\n--- Finalizando Pedido ---");
        try {
            Endereco enderecoEntrega = selecionarEndereco();
            if (enderecoEntrega == null) return;

            System.out.println("Formas de pagamento: PIX, CREDITO, DEBITO, DINHEIRO");
            String pagamento = LeitorConsole.lerString("Como deseja pagar? ").toUpperCase();
            String obs = LeitorConsole.lerString("Observação (opcional): ");

            Pedido novoPedido = new Pedido(clienteLogado.getId(), enderecoEntrega.getId(), pagamento);
            novoPedido.setObservacoes(obs);

            String resultado = pedidoController.criarNovoPedido(novoPedido, carrinho);

            System.out.println("\n" + resultado);
            System.out.println("Compra realizada com sucesso!");
            carrinho.clear();
            LeitorConsole.lerString("Pressione ENTER para voltar ao menu...");

        } catch (Exception e) {
            System.out.println("Erro ao finalizar pedido: " + e.getMessage());
        }
    }

    private Endereco selecionarEndereco() {
        List<Endereco> enderecos = usuarioController.listarEnderecosPorUsuario(clienteLogado.getId());

        if (enderecos == null || enderecos.isEmpty()) {
            System.out.println("Você não tem endereço cadastrado!");
            System.out.println("Vá em 'Perfil (6)' → 'Cadastrar Novo Endereço (3)'");
            return null;
        }

        System.out.println("\nSelecione o endereço de entrega:");
        for (int i = 0; i < enderecos.size(); i++) {
            Endereco e = enderecos.get(i);
            System.out.printf("%d. %s, %s - %s\n", (i + 1), e.getRua(), e.getNumero(), e.getBairro());
        }
        System.out.println("0. Cancelar");

        int op = LeitorConsole.lerInteiro("Opção: ");
        if (op > 0 && op <= enderecos.size()) {
            return enderecos.get(op - 1);
        }
        return null;
    }

    private void buscarMedicamento() {
        System.out.println("\n--- Buscar Medicamento ---");
        String termo = LeitorConsole.lerString("Digite o nome ou código: ");
        List<Medicamento> todos = medicamentoController.listarMedicamentosAtivos();
        List<Medicamento> resultados = new ArrayList<>();

        if (todos != null) {
            for (Medicamento med : todos) {
                if (med.getCodigo().equalsIgnoreCase(termo) ||
                        med.getNome().toLowerCase().contains(termo.toLowerCase())) {
                    resultados.add(med);
                }
            }
        }

        if (resultados.isEmpty()) {
            System.out.println("Nada encontrado.");
        } else {
            exibirTabelaMedicamentos(resultados);
        }
        LeitorConsole.lerString("\nPressione ENTER...");
    }

    private void exibirTabelaMedicamentos(List<Medicamento> lista) {
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-5s | %-25s | %-10s | %-10s\n", "ID", "Nome", "Preço", "Estoque");
        System.out.println("------------------------------------------------------------");
        for (Medicamento med : lista) {
            String nome = med.getNome().length() > 22 ? med.getNome().substring(0, 22) + "..." : med.getNome();
            System.out.printf("%-5d | %-25s | R$ %-7.2f | %-5d\n", med.getId(), nome, med.getPreco(), med.getEstoque());
        }
    }

    private void adicionarAoCarrinho() {
        System.out.println("\n--- Adicionar ao Carrinho ---");
        int idProduto = LeitorConsole.lerInteiro("Digite o ID do medicamento: ");
        Medicamento med = medicamentoDAO.buscarPorId(idProduto);

        if (med == null || !Boolean.TRUE.equals(med.getAtivo())) {
            System.out.println("Medicamento não encontrado ou indisponível.");
            return;
        }
        System.out.println("Item: " + med.getNome() + " | Preço: R$" + med.getPreco());
        int quantidade = LeitorConsole.lerInteiro("Quantidade: ");

        if (quantidade > med.getEstoque()) {
            System.out.println("Estoque insuficiente.");
            return;
        }
        carrinho.add(new ItemPedido(med.getId(), quantidade, med.getPreco()));
        System.out.println("Adicionado ao carrinho!");
    }

    private void verMeusPedidos() {
        System.out.println("\n--- Meus Pedidos ---");
        List<Pedido> pedidos = pedidoController.listarPedidosPorCliente(clienteLogado.getId());

        if (pedidos == null || pedidos.isEmpty()) {
            System.out.println("Você ainda não realizou nenhum pedido conosco.");
            LeitorConsole.lerString("Pressione ENTER para voltar...");
            return;
        }

        System.out.printf("%-5s | %-12s | %-10s | %-15s\n", "ID", "Data", "Total", "Status");
        System.out.println("----------------------------------------------------------");

        for (Pedido p : pedidos) {
            String dataFormatada = (p.getDataPedido() != null) ? p.getDataPedido().toString().substring(0, 10) : "--/--";
            System.out.printf("#%-4d | %-12s | R$ %-7.2f | %s\n",
                    p.getId(), dataFormatada, p.getValorTotal(), formatarStatus(p.getStatus()));
        }
        System.out.println("----------------------------------------------------------");
        LeitorConsole.lerString("Pressione ENTER para voltar...");
    }

    private void menuFavoritos() {
        System.out.println("\n=== MEUS FAVORITOS ===");
        System.out.println("1. Ver Medicamentos Favoritos");
        System.out.println("2. Ver Fornecedores Favoritos");
        System.out.println("3. Adicionar Medicamento aos Favoritos");
        System.out.println("4. Remover Medicamento dos Favoritos");
        System.out.println("0. Voltar");

        int op = LeitorConsole.lerInteiro("Opção: ");

        switch (op) {
            case 1:
                verMedicamentosFavoritos();
                break;
            case 2:
                verFornecedoresFavoritos();
                break;
            case 3:
                adicionarMedicamentoFavorito();
                break;
            case 4:
                removerMedicamentoFavorito();
                break;
            case 0:
                break;
            default:
                System.out.println("Opção inválida.");
        }
    }

    private void verMedicamentosFavoritos() {
        System.out.println("\n--- Meus Medicamentos Favoritos ---");

        List<Favorito> favoritos = favoritoController.listarMedicamentosFavoritos(clienteLogado.getId());

        if (favoritos == null || favoritos.isEmpty()) {
            System.out.println("Você ainda não tem medicamentos favoritos.");
            System.out.println("Use a opção '3. Adicionar Medicamento aos Favoritos' para adicionar!");
            LeitorConsole.lerString("\nPressione ENTER para voltar...");
            return;
        }

        System.out.println("------------------------------------------------------------");
        System.out.printf("%-5s | %-25s | %-10s | %-10s\n", "ID", "Nome", "Preço", "Estoque");
        System.out.println("------------------------------------------------------------");

        for (Favorito fav : favoritos) {
            Medicamento med = medicamentoDAO.buscarPorId(fav.getMedicamentoId());
            if (med != null) {
                String nome = med.getNome().length() > 22 ? med.getNome().substring(0, 22) + "..." : med.getNome();
                System.out.printf("%-5d | %-25s | R$ %-7.2f | %-5d\n",
                        med.getId(), nome, med.getPreco(), med.getEstoque());
            }
        }
        System.out.println("------------------------------------------------------------");

        System.out.println("\nDeseja adicionar algum ao carrinho?");
        System.out.println("1. Sim, adicionar ao carrinho");
        System.out.println("0. Voltar");

        int op = LeitorConsole.lerInteiro("Opção: ");
        if (op == 1) {
            adicionarAoCarrinho();
        }
    }

    private void verFornecedoresFavoritos() {
        System.out.println("\n--- Meus Fornecedores Favoritos ---");

        List<Favorito> favoritos = favoritoController.listarFornecedoresFavoritos(clienteLogado.getId());

        if (favoritos == null || favoritos.isEmpty()) {
            System.out.println("Você ainda não tem fornecedores favoritos.");
            LeitorConsole.lerString("\nPressione ENTER para voltar...");
            return;
        }

        System.out.println("------------------------------------------------------------");
        for (Favorito fav : favoritos) {
            Fornecedor forn = fornecedorDAO.buscarPorId(fav.getFornecedorId());
            if (forn != null) {
                System.out.printf("ID: %d | Nome: %s | CNPJ: %s\n",
                        forn.getId(), forn.getNome(), forn.getCnpj());
            }
        }
        System.out.println("------------------------------------------------------------");

        LeitorConsole.lerString("\nPressione ENTER para voltar...");
    }

    private void adicionarMedicamentoFavorito() {
        System.out.println("\n--- Adicionar Medicamento aos Favoritos ---");

        // Mostra o catálogo primeiro
        List<Medicamento> medicamentos = medicamentoController.listarMedicamentosAtivos();
        if (medicamentos == null || medicamentos.isEmpty()) {
            System.out.println("Nenhum medicamento disponível.");
            return;
        }

        exibirTabelaMedicamentos(medicamentos);

        int medicamentoId = LeitorConsole.lerInteiro("\nDigite o ID do medicamento para favoritar (0 para cancelar): ");

        if (medicamentoId <= 0) {
            System.out.println("Cancelado.");
            return;
        }

        String resultado = favoritoController.adicionarMedicamentoFavorito(clienteLogado.getId(), medicamentoId);
        System.out.println(resultado);

        LeitorConsole.lerString("\nPressione ENTER para continuar...");
    }

    private void removerMedicamentoFavorito() {
        System.out.println("\n--- Remover Medicamento dos Favoritos ---");

        List<Favorito> favoritos = favoritoController.listarMedicamentosFavoritos(clienteLogado.getId());

        if (favoritos == null || favoritos.isEmpty()) {
            System.out.println("Você não tem medicamentos favoritos.");
            LeitorConsole.lerString("\nPressione ENTER para voltar...");
            return;
        }

        // Mostra os favoritos
        System.out.println("------------------------------------------------------------");
        for (Favorito fav : favoritos) {
            Medicamento med = medicamentoDAO.buscarPorId(fav.getMedicamentoId());
            if (med != null) {
                System.out.printf("ID: %d | Nome: %s\n", med.getId(), med.getNome());
            }
        }
        System.out.println("------------------------------------------------------------");

        int medicamentoId = LeitorConsole.lerInteiro("\nDigite o ID do medicamento para remover (0 para cancelar): ");

        if (medicamentoId <= 0) {
            System.out.println("Cancelado.");
            return;
        }

        String resultado = favoritoController.removerMedicamentoFavorito(clienteLogado.getId(), medicamentoId);
        System.out.println(resultado);

        LeitorConsole.lerString("\nPressione ENTER para continuar...");
    }

    private String formatarStatus(String status) {
        if (status == null) return "DESCONHECIDO";
        return switch (status.toUpperCase()) {
            case "PENDENTE" -> "[PENDENTE]";
            case "PRONTO_PARA_ENTREGA" -> "[PRONTO]";
            case "EM_TRANSPORTE" -> "[A CAMINHO]";
            case "ENTREGUE" -> "[ENTREGUE]";
            case "CANCELADO" -> "[CANCELADO]";
            default -> status;
        };
    }
}