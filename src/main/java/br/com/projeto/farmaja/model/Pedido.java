package br.com.projeto.farmaja.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    // 1. ATRIBUTOS (colunas do banco)
    private Integer id;
    private Integer clienteId;
    private Integer entregadorId; // pode ser nulo enquanto não atribuído
    private Integer enderecoId;
    private BigDecimal valorTotal;
    private String status; // PENDENTE, CONFIRMADO, PREPARANDO, PRONTO_PARA_ENTREGA, EM_TRANSPORTE, ENTREGUE, CANCELADO
    private String formaPagamento;
    private LocalDateTime dataPedido;
    private LocalDateTime dataEntrega;
    private String observacoes;

    // Apoio de domínio (não persistido diretamente)
    private final List<ItemPedido> itens = new ArrayList<>();

    // 2. CONSTRUTOR VAZIO (obrigatório para os DAOs)
    public Pedido() {}

    // 3. CONSTRUTOR COM PARÂMETROS (criação de objetos)
    public Pedido(Integer clienteId, Integer enderecoId, String formaPagamento) {
        setClienteId(clienteId);
        setEnderecoId(enderecoId);
        setFormaPagamento(formaPagamento);
        this.status = "PENDENTE";
        this.valorTotal = BigDecimal.ZERO;
        this.dataPedido = LocalDateTime.now();
    }

    // 4. GETTERS E SETTERS (para todos os atributos)
    public Integer getId() { return id; }
    public void setId(Integer id) {
        this.id = id;
        // Se já existirem itens e o pedido acabou de ganhar ID, propaga para os itens que ainda não possuem pedidoId
        if (id != null) {
            for (ItemPedido item : itens) {
                if (item.getPedidoId() == null) {
                    item.setPedidoId(id);
                }
            }
        }
    }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) {
        if (clienteId == null) throw new IllegalArgumentException("clienteId obrigatório");
        this.clienteId = clienteId;
    }

    public Integer getEntregadorId() { return entregadorId; }
    public void setEntregadorId(Integer entregadorId) { this.entregadorId = entregadorId; }

    public Integer getEnderecoId() { return enderecoId; }
    public void setEnderecoId(Integer enderecoId) {
        if (enderecoId == null) throw new IllegalArgumentException("enderecoId obrigatório");
        this.enderecoId = enderecoId;
    }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        if (status == null || status.isBlank()) throw new IllegalArgumentException("Status obrigatório");
        String s = status.toUpperCase();
        switch (s) {
            case "PENDENTE":
            case "CONFIRMADO":
            case "PREPARANDO":
            case "PRONTO_PARA_ENTREGA":
            case "EM_TRANSPORTE":
            case "ENTREGUE":
            case "CANCELADO":
                this.status = s;
                break;
            default:
                throw new IllegalArgumentException("Status inválido");
        }
    }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) {
        if (formaPagamento == null || formaPagamento.isBlank()) throw new IllegalArgumentException("Forma de pagamento obrigatória");
        this.formaPagamento = formaPagamento.toUpperCase();
    }

    public LocalDateTime getDataPedido() { return dataPedido; }
    public void setDataPedido(LocalDateTime dataPedido) { this.dataPedido = dataPedido; }

    public LocalDateTime getDataEntrega() { return dataEntrega; }
    public void setDataEntrega(LocalDateTime dataEntrega) { this.dataEntrega = dataEntrega; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public List<ItemPedido> getItens() { return itens; }

    // 5. REGRAS DE NEGÓCIO DE APOIO
    public void adicionarItem(Integer medicamentoId, int quantidade, BigDecimal precoUnitario, int estoqueDisponivel) {
        if (medicamentoId == null) throw new IllegalArgumentException("medicamentoId obrigatório");
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva");
        if (precoUnitario == null || precoUnitario.signum() < 0) throw new IllegalArgumentException("Preço unitário inválido");
        if (estoqueDisponivel < quantidade) throw new IllegalStateException("Estoque insuficiente para o item");

        // Permite criar o item sem pedidoId; se este pedido já tiver ID, preenche agora
        ItemPedido item = new ItemPedido(medicamentoId, quantidade, precoUnitario);
        if (this.id != null) {
            item.setPedidoId(this.id);
        }
        itens.add(item);
        recalcularTotal();
    }

    public void removerItem(ItemPedido item) {
        itens.remove(item);
        recalcularTotal();
    }

    public void recalcularTotal() {
        this.valorTotal = itens.stream()
            .map(ItemPedido::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 6. toString() (para exibir informações formatadas)
    @Override
    public String toString() {
        return String.format("ID: %d | Cliente: %d | Endereço: %d | Total: %s | Status: %s",
                id, clienteId, enderecoId, valorTotal, status);
    }
}
