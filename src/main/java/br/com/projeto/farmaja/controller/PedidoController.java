package br.com.projeto.farmaja.controller;

import br.com.projeto.farmaja.dao.*;
import br.com.projeto.farmaja.model.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Orquestra todo o fluxo de Vendas (Pedidos),
 * lidando com estoque, itens, totais e histórico.
 */
public class PedidoController {

    // Múltiplas dependências de DAO
    private final PedidoDAO pedidoDAO;
    private final ItemPedidoDAO itemPedidoDAO;
    private final MedicamentoDAO medicamentoDAO;
    private final UsuarioDAO usuarioDAO; // Para validar entregadores
    private final HistoricoEntregaDAO historicoEntregaDAO; // Para logar mudanças

    public PedidoController(PedidoDAO pedidoDAO, ItemPedidoDAO itemPedidoDAO,
                            MedicamentoDAO medicamentoDAO, UsuarioDAO usuarioDAO,
                            HistoricoEntregaDAO historicoEntregaDAO) {
        this.pedidoDAO = pedidoDAO;
        this.itemPedidoDAO = itemPedidoDAO;
        this.medicamentoDAO = medicamentoDAO;
        this.usuarioDAO = usuarioDAO;
        this.historicoEntregaDAO = historicoEntregaDAO;
    }

    /**
     * Orquestra a criação de um novo pedido.
     * Este é o principal fluxo de negócio de uma venda.
     */
    public String criarNovoPedido(Pedido pedido, List<ItemPedido> itens) {

        // Em um sistema real, isso seria uma TRANSACAO de banco de dados (Tudo ou Nada)

        try {
            BigDecimal valorTotal = BigDecimal.ZERO;

            // 1. Validação de estoque e cálculo de totais
            for (ItemPedido item : itens) {
                Medicamento med = medicamentoDAO.buscarPorId(item.getMedicamentoId());

                if (med == null) {
                    throw new RuntimeException("Medicamento (ID: " + item.getMedicamentoId() + ") não encontrado.");
                }

                // Seus models usam getAtivo() (Boolean) e não isAtivo() (boolean)
                if (!Boolean.TRUE.equals(med.getAtivo())) {
                    throw new RuntimeException("Medicamento '" + med.getNome() + "' está inativo.");
                }

                if (med.getEstoque() < item.getQuantidade()) {
                    throw new RuntimeException("Estoque insuficiente para '" + med.getNome() +
                            "'. (Disponível: " + med.getEstoque() + ")");
                }

                // --- CORREÇÃO APLICADA AQUI ---
                // Define o preço unitário. O model ItemPedido irá calcular o subtotal automaticamente.
                item.setPrecoUnitario(med.getPreco());

                // Apenas obtemos o subtotal já calculado pelo model
                valorTotal = valorTotal.add(item.getSubtotal());
                // --- FIM DA CORREÇÃO ---
            }

            // 2. Define o valor total
            pedido.setValorTotal(valorTotal);

            // O status "PENDENTE" já é definido pelo construtor do model Pedido
            // que é chamado na FarmaJaApp.

            // 3. Cria o Pedido (header)
            pedidoDAO.criar(pedido);
            int pedidoId = pedido.getId(); // Pega o ID gerado

            // 4. Cria os Itens do Pedido e Atualiza o Estoque
            for (ItemPedido item : itens) {
                // Vincula o item ao pedido (seu model ItemPedido permite isso)
                item.setPedidoId(pedidoId);
                itemPedidoDAO.criar(item);

                // Dá baixa no estoque
                int quantidadeNegativa = -item.getQuantidade();
                medicamentoDAO.atualizarEstoque(item.getMedicamentoId(), quantidadeNegativa);
            }

            // 5. Cria o primeiro registro no histórico
            // O status inicial agora é "PENDENTE" para bater com seu model Pedido
            criarLogHistorico(pedidoId, null, "PENDENTE", "Pedido criado", null);

            return "Pedido (ID: " + pedidoId + ") criado com sucesso! Valor Total: R$" + valorTotal;

        } catch (RuntimeException e) {
            // (Aqui entraria o ROOLBACK da transação)
            return "Erro ao criar pedido: " + e.getMessage();
        }
    }

    /**
     * Atualiza o status de um pedido e registra no histórico.
     */
    public String atualizarStatusPedido(int pedidoId, String novoStatus, String observacao, Integer entregadorId) {
        try {
            Pedido pedido = pedidoDAO.buscarPorId(pedidoId);
            if (pedido == null) {
                return "Erro: Pedido não encontrado.";
            }

            String statusAnterior = pedido.getStatus();

            // O model Pedido tem validação no setStatus.
            // (O model já faz o .toUpperCase())
            pedido.setStatus(novoStatus); // Isso pode lançar IllegalArgumentException

            // Atualiza o status no banco
            pedidoDAO.atualizarStatus(pedidoId, pedido.getStatus());

            // Se o status for "EM_TRANSPORTE", atribui o entregador
            if (entregadorId != null && "EM_TRANSPORTE".equals(pedido.getStatus())) {
                pedidoDAO.atribuirEntregador(pedidoId, entregadorId);
            }

            // Loga a mudança no histórico
            criarLogHistorico(pedidoId, statusAnterior, novoStatus, observacao, entregadorId);

            return "Status do Pedido " + pedidoId + " atualizado para: " + novoStatus;

        } catch (IllegalArgumentException e) {
            return "Erro: Status '" + novoStatus + "' é inválido. " + e.getMessage();
        } catch (RuntimeException e) {
            return "Erro ao atualizar status: " + e.getMessage();
        }
    }

    /**
     * Lista pedidos que precisam de um entregador.
     */
    public List<Pedido> listarPedidosPendentesAtribuicao() {
        try {
            // Seu DAO já tem o metodo correto para "PRONTO_PARA_ENTREGA"
            return pedidoDAO.buscarPendentesAtribuicao();
        } catch (RuntimeException e) {
            System.err.println("Erro ao listar pedidos pendentes: " + e.getMessage());
            return null;
        }
    }

    public List<Pedido> listarPedidosPorStatus(String status) {
        try {
            // Validação de status já ocorre no model, aqui apenas passamos para o DAO
            return pedidoDAO.buscarPorStatus(status.toUpperCase());
        } catch (RuntimeException e) {
            System.err.println("Erro ao listar por status: " + e.getMessage());
            return null;
        }
    }

    // Metodo auxiliar privado para criar logs
    private void criarLogHistorico(int pedidoId, String statusAnterior, String statusNovo, String observacao, Integer entregadorId) {
        HistoricoEntrega historico = new HistoricoEntrega();
        historico.setPedidoId(pedidoId);

        // Seu model HistoricoEntrega não permite statusAnterior nulo.
        if (statusAnterior == null) {
            historico.setStatusAnterior(statusNovo);
        } else {
            historico.setStatusAnterior(statusAnterior);
        }

        historico.setStatusNovo(statusNovo);
        historico.setObservacao(observacao);
        if (entregadorId != null) {
            historico.setEntregadorId(entregadorId);
        }
        historicoEntregaDAO.criar(historico);
    }
}