package br.com.projeto.farmaja.model;

import java.time.LocalDateTime;

public class Favorito {
    private Integer id;
    private Integer usuarioId;
    private Integer medicamentoId;
    private Integer fornecedorId;
    private String tipoFavorito;
    private LocalDateTime dataAdicionado;

    public Favorito() {}

    // MEDICAMENTO
    public Favorito(Integer usuarioId, Integer medicamentoId) {
        setUsuarioId(usuarioId);
        setMedicamentoId(medicamentoId);
        this.tipoFavorito = "MEDICAMENTO";
        this.dataAdicionado = LocalDateTime.now();
    }

    // FORNECEDOR
    public static Favorito criarFavoritoFornecedor(Integer usuarioId, Integer fornecedorId) {
        Favorito favorito = new Favorito();
        favorito.setUsuarioId(usuarioId);
        favorito.setFornecedorId(fornecedorId);
        favorito.tipoFavorito = "FORNECEDOR";
        favorito.dataAdicionado = LocalDateTime.now();
        return favorito;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) {
        if (usuarioId == null) throw new IllegalArgumentException("usuarioId obrigatório");
        this.usuarioId = usuarioId;
    }

    public Integer getMedicamentoId() { return medicamentoId; }
    public void setMedicamentoId(Integer medicamentoId) {
        this.medicamentoId = medicamentoId;
    }

    public Integer getFornecedorId() { return fornecedorId; }
    public void setFornecedorId(Integer fornecedorId) {
        this.fornecedorId = fornecedorId;
    }

    public String getTipoFavorito() { return tipoFavorito; }
    public void setTipoFavorito(String tipoFavorito) {
        if (tipoFavorito == null || tipoFavorito.isBlank()) {
            throw new IllegalArgumentException("Tipo de favorito obrigatório");
        }
        String tipo = tipoFavorito.toUpperCase();
        if (!tipo.equals("MEDICAMENTO") && !tipo.equals("FORNECEDOR")) {
            throw new IllegalArgumentException("Tipo inválido. Use MEDICAMENTO ou FORNECEDOR");
        }
        this.tipoFavorito = tipo;
    }

    public LocalDateTime getDataAdicionado() { return dataAdicionado; }
    public void setDataAdicionado(LocalDateTime dataAdicionado) {
        this.dataAdicionado = dataAdicionado;
    }

    // toString()
    @Override
    public String toString() {
        return String.format("ID: %d | Usuário: %d | Tipo: %s | MedID: %s | FornID: %s",
                id, usuarioId, tipoFavorito,
                medicamentoId != null ? medicamentoId : "-",
                fornecedorId != null ? fornecedorId : "-");
    }
}