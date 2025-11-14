package br.com.projeto.farmaja.model;

import java.time.LocalDateTime;

public class HistoricoEntrega {
    // 1. ATRIBUTOS (colunas do banco)
    private Integer id;
    private Integer pedidoId;
    private Integer entregadorId;
    private String statusAnterior;
    private String statusNovo;
    private LocalDateTime dataAtualizacao;
    private String observacao;

    // 2. CONSTRUTOR VAZIO (obrigatório para os DAOs)
    public HistoricoEntrega() {}

    // 3. CONSTRUTOR COM PARÂMETROS (criação de objetos)
    public HistoricoEntrega(Integer pedidoId, Integer entregadorId, String statusAnterior, String statusNovo, String observacao) {
        setPedidoId(pedidoId);
        setEntregadorId(entregadorId);
        setStatusAnterior(statusAnterior);
        setStatusNovo(statusNovo);
        this.observacao = observacao;
        this.dataAtualizacao = LocalDateTime.now();
    }

    // 4. GETTERS E SETTERS (para todos os atributos)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getPedidoId() { return pedidoId; }
    public void setPedidoId(Integer pedidoId) {
        if (pedidoId == null) throw new IllegalArgumentException("pedidoId obrigatório");
        this.pedidoId = pedidoId;
    }

    public Integer getEntregadorId() { return entregadorId; }
    public void setEntregadorId(Integer entregadorId) { this.entregadorId = entregadorId; }

    public String getStatusAnterior() { return statusAnterior; }
    public void setStatusAnterior(String statusAnterior) {
        if (statusAnterior == null || statusAnterior.isBlank()) throw new IllegalArgumentException("Status anterior obrigatório");
        String s = statusAnterior.toUpperCase();
        if (!isStatusValido(s)) throw new IllegalArgumentException("Status anterior inválido");
        this.statusAnterior = s;
    }

    public String getStatusNovo() { return statusNovo; }
    public void setStatusNovo(String statusNovo) {
        if (statusNovo == null || statusNovo.isBlank()) throw new IllegalArgumentException("Status novo obrigatório");
        String s = statusNovo.toUpperCase();
        if (!isStatusValido(s)) throw new IllegalArgumentException("Status novo inválido");
        this.statusNovo = s;
    }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    // 5. Regras de apoio
    private boolean isStatusValido(String s) {
        switch (s) {
            case "PENDENTE":
            case "CONFIRMADO":
            case "PREPARANDO":
            case "PRONTO_PARA_ENTREGA":
            case "EM_TRANSPORTE":
            case "ENTREGUE":
            case "CANCELADO":
                return true;
            default:
                return false;
        }
    }

    // 6. toString() (para exibir informações formatadas)
    @Override
    public String toString() {
        return String.format("ID: %d | Pedido: %d | Entregador: %s | %s -> %s | Em: %s",
                id, pedidoId, entregadorId == null ? "null" : entregadorId.toString(), statusAnterior, statusNovo, dataAtualizacao);
    }
}