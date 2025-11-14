package br.com.projeto.farmaja.view.Entregador;

import br.com.projeto.farmaja.controller.PedidoController;
import br.com.projeto.farmaja.controller.UsuarioController;
import br.com.projeto.farmaja.dao.*;
import br.com.projeto.farmaja.model.Endereco;
import br.com.projeto.farmaja.model.Pedido;
import br.com.projeto.farmaja.model.Usuario;
import br.com.projeto.farmaja.view.util.LeitorConsole;

import java.util.List;

public class EntregadorMenu {

    private final PedidoDAO pedidoDAO;
    private final ItemPedidoDAO itemPedidoDAO;
    private final MedicamentoDAO medicamentoDAO;
    private final UsuarioDAO usuarioDAO;
    private final EnderecoDAO enderecoDAO;
    private final HistoricoEntregaDAO historicoEntregaDAO;

    private final PedidoController pedidoController;
    private final UsuarioController usuarioController;

    private Usuario entregadorLogado = null;

    public EntregadorMenu() {

        this.pedidoDAO = new PedidoDAO();
        this.itemPedidoDAO = new ItemPedidoDAO();
        this.medicamentoDAO = new MedicamentoDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.enderecoDAO = new EnderecoDAO();
        this.historicoEntregaDAO = new HistoricoEntregaDAO();

        this.usuarioController = new UsuarioController(usuarioDAO, enderecoDAO);
        this.pedidoController = new PedidoController(
                pedidoDAO, itemPedidoDAO, medicamentoDAO, usuarioDAO, historicoEntregaDAO
        );
    }

    public void mostrar() {
        simularLoginEntregador();

        if (entregadorLogado != null) {
            System.out.println("\n--- 🚚 Portal do Entregador ---");
            System.out.println("Bem-vindo, " + entregadorLogado.getNome());
        }

        int opcao;
        do {
            System.out.println("\n===== MENU ENTREGADOR =====");
            System.out.println("1. Ver minhas entregas (Em Transporte)");
            System.out.println("2. Marcar entrega como 'Concluída'");
            System.out.println("0. Logout (Voltar ao Menu Principal)");

            opcao = LeitorConsole.lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1:
                    listarMinhasEntregas();
                    break;
                case 2:
                    marcarEntregaConcluida();
                    break;
                case 0:
                    System.out.println("Fazendo logout... Bom descanso!");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void simularLoginEntregador() {
        try {
            this.entregadorLogado = usuarioController.login("entregador@farmaja.com", "entregador123");

            if (this.entregadorLogado == null) {
                System.out.println("(Debug) Criando entregador padrão...");

                Usuario novoEntregador = new Usuario("Entregador Padrão", "entregador@farmaja.com", "entregador123", "11122233344", "988887777", "ENTREGADOR");

                // --- Correção do Endereço (usando Setters) ---
                Endereco end = new Endereco();
                end.setRua("Rua Faria Lima");
                end.setNumero("1000");
                end.setBairro("Pinheiros");
                end.setCidade("São Paulo");
                end.setEstado("SP");
                end.setCep("01452-002");
                end.setComplemento("");

                // Define ID manual para teste (simulando banco)
                novoEntregador.setId(1);

                this.entregadorLogado = novoEntregador;
            }
        } catch (Exception e) {
            System.out.println("(Aviso) Modo offline ou erro de conexão.");
            this.entregadorLogado = new Usuario("Entregador Offline", "off", "off", "000", "000", "ENTREGADOR");
            this.entregadorLogado.setId(1);
        }
    }

    private void listarMinhasEntregas() {
        System.out.println("\n--- 📦 Minhas Entregas Pendentes ---");

        List<Pedido> pedidos = pedidoController.listarPedidosPorEntregadorEStatus(
                entregadorLogado.getId(),
                "EM_TRANSPORTE"
        );

        if (pedidos == null || pedidos.isEmpty()) {
            System.out.println("✅ Você não tem nenhuma entrega pendente no momento.");
            LeitorConsole.lerString("Pressione ENTER para voltar...");
            return;
        }

        System.out.println("----------------------------------------------------------------------");
        for (Pedido p : pedidos) {
            Usuario cliente = usuarioDAO.buscarPorId(p.getClienteId());
            Endereco endereco = enderecoDAO.buscarPorId(p.getEnderecoId());

            String nomeCliente = (cliente != null) ? cliente.getNome() : "Cliente Desconhecido";
            String enderecoStr = (endereco != null) ?
                    endereco.getRua() + ", " + endereco.getNumero() + " - " + endereco.getBairro() :
                    "Endereço não encontrado";

            System.out.printf("PEDIDO #%d | Valor: R$%.2f\n", p.getId(), p.getValorTotal());
            System.out.println("   👤 Cliente: " + nomeCliente);
            System.out.println("   📍 Destino: " + enderecoStr);
            System.out.println("----------------------------------------------------------------------");
        }
        LeitorConsole.lerString("Pressione ENTER para voltar...");
    }

    private void marcarEntregaConcluida() {
        System.out.println("\n--- ✅ Concluir Entrega ---");

        List<Pedido> pedidos = pedidoController.listarPedidosPorEntregadorEStatus(entregadorLogado.getId(), "EM_TRANSPORTE");
        if (pedidos == null || pedidos.isEmpty()) {
            System.out.println("Nenhuma entrega para concluir.");
            return;
        }

        for(Pedido p : pedidos) {
            System.out.println("ID: " + p.getId() + " - Valor: R$" + p.getValorTotal());
        }

        int pedidoId = LeitorConsole.lerInteiro("Digite o ID do Pedido entregue (0 para cancelar): ");

        if (pedidoId <= 0) return;

        String res = pedidoController.atualizarStatusPedido(
                pedidoId,
                "ENTREGUE",
                "Entrega realizada por: " + entregadorLogado.getNome(),
                entregadorLogado.getId()
        );

        System.out.println(res);
        LeitorConsole.lerString("Pressione ENTER para continuar...");
    }
}