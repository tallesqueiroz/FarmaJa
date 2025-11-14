package br.com.projeto.farmaja.model;

import java.math.BigDecimal;

public class ItemPedido {
    // 1. ATRIBUTOS (colunas do banco)
    private Integer id;
    private Integer pedidoId;      // pode ser nulo até o Pedido ser persistido
    private Integer medicamentoId;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;

    // 2. CONSTRUTOR VAZIO (obrigatório para os DAOs)
    public ItemPedido() {}

    // 3a. CONSTRUTOR COMPLETO (quando já existir pedidoId)
    public ItemPedido(Integer pedidoId, Integer medicamentoId, Integer quantidade, BigDecimal precoUnitario) {
        setPedidoId(pedidoId);
        setMedicamentoId(medicamentoId);
        setQuantidade(quantidade);
        setPrecoUnitario(precoUnitario);
        recalcularSubtotal();
    }

    // 3b. NOVO CONSTRUTOR (sem pedidoId): permite adicionar itens antes de persistir o Pedido
    public ItemPedido(Integer medicamentoId, Integer quantidade, BigDecimal precoUnitario) {
        setMedicamentoId(medicamentoId);
        setQuantidade(quantidade);
        setPrecoUnitario(precoUnitario);
        recalcularSubtotal();
    }

    // 4. GETTERS E SETTERS (para todos os atributos)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public void getSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Integer getPedidoId() { return pedidoId; }
    // setter flexibilizado: aceita null e não lança exceção
    public void setPedidoId(Integer pedidoId) { this.pedidoId = pedidoId; }

    public Integer getMedicamentoId() { return medicamentoId; }
    public void setMedicamentoId(Integer medicamentoId) {
        if (medicamentoId == null) throw new IllegalArgumentException("medicamentoId obrigatório");
        this.medicamentoId = medicamentoId;
    }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva");
        this.quantidade = quantidade;
        recalcularSubtotal();
    }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) {
        if (precoUnitario == null || precoUnitario.signum() < 0) throw new IllegalArgumentException("Preço unitário inválido");
        this.precoUnitario = precoUnitario;
        recalcularSubtotal();
    }

    public BigDecimal getSubtotal() { return subtotal; }

    // 5. REGRA DE CÁLCULO
    public void recalcularSubtotal() {
        if (precoUnitario != null && quantidade != null) {
            this.subtotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        } else {
            this.subtotal = null;
        }
    }

    // 6. toString() (para exibir informações formatadas)
    @Override
    public String toString() {
        return String.format("ID: %d | Pedido: %s | Medicamento: %d | Qtd: %d | PU: %s | Subtotal: %s",
                id,
                pedidoId == null ? "null" : pedidoId.toString(),
                medicamentoId,
                quantidade,
                precoUnitario,
                subtotal);
    }
}